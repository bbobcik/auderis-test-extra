/*
 * Copyright 2016 Boleslav Bobcik - Auderis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cz.auderis.test.logging.slf4j;

import cz.auderis.test.logging.LogCaptureInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Slf4jInitializer implements LogCaptureInitializer {

    private static boolean INITIALIZED = false;

    @Override
    public boolean isFrameworkPresent() {
        try {
            final Class<?> frameworkClass = Class.forName("org.slf4j.LoggerFactory");
            assert null != frameworkClass;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize() throws Exception {
        synchronized (Slf4jInitializer.class) {
            if (INITIALIZED) {
                return;
            }
            final Field modifiersField;
            try {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException("Cannot intercept SLF4J framework", e);
            }
            final Slf4jLoggerFactory testLoggerFactory = new Slf4jLoggerFactory();
            try {
                // Intercept fallback NOP factory
                final Class<?> apiFactoryClass = Class.forName("org.slf4j.LoggerFactory");
                final Field factoryField = apiFactoryClass.getDeclaredField("NOP_FALLBACK_FACTORY");
                factoryField.setAccessible(true);
                modifiersField.setInt(factoryField, factoryField.getModifiers() & ~Modifier.FINAL);
                factoryField.set(null, testLoggerFactory);
                // Make sure that NOP fallback factory will be unconditionally used
                final Field fallbackField = apiFactoryClass.getDeclaredField("NOP_FALLBACK_INITIALIZATION");
                fallbackField.setAccessible(true);
                final int fallbackStateValue = fallbackField.getInt(null);
                final Field stateField = apiFactoryClass.getDeclaredField("INITIALIZATION_STATE");
                stateField.setAccessible(true);
                modifiersField.setInt(stateField, stateField.getModifiers() & ~Modifier.FINAL);
                stateField.setInt(null, fallbackStateValue);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("SLF4J framework not detected");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unsupported SLF4J framework API version");
            } catch (Exception e) {
                throw new RuntimeException("Failed to intercept SLF4J provider", e);
            } finally {
                INITIALIZED = true;
            }
        }
    }

}
