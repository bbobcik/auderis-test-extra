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

package cz.auderis.test.logging.jboss;

import cz.auderis.test.logging.LogCaptureInitializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class JBossLoggingInitializer implements LogCaptureInitializer {

    private static boolean INITIALIZED = false;

    @Override
    public boolean isFrameworkPresent() {
        try {
            final Class<?> frameworkClass = Class.forName("org.jboss.logging.LoggerProviders");
            assert null != frameworkClass;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize() throws Exception {
        synchronized (JBossLoggingInitializer.class) {
            if (INITIALIZED) {
                return;
            }
            final Field modifiersField;
            try {
                modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException("Cannot intercept JBoss Logging framework", e);
            }
            try {
                final Class<?> providerClass = Class.forName("org.jboss.logging.LoggerProviders");
                final Field providerField = providerClass.getDeclaredField("PROVIDER");
                providerField.setAccessible(true);
                modifiersField.setInt(providerField, providerField.getModifiers() & ~Modifier.FINAL);
                providerField.set(null, JBossLoggerProvider.INSTANCE);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("JBoss Logging framework not detected");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unsupported version of JBoss Logging framework");
            } catch (Exception e) {
                throw new RuntimeException("Failed to intercept JBoss Logging provider", e);
            } finally {
                INITIALIZED = true;
            }
        }
    }

}
