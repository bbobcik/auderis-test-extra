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

package cz.auderis.test.logging.log4j;

import cz.auderis.test.logging.LogCaptureInitializer;

import java.lang.reflect.Field;

public class Log4jInitializer implements LogCaptureInitializer {

    private static boolean INITIALIZED = false;

    @Override
    public boolean isFrameworkPresent() {
        try {
            final Class<?> frameworkClass = Class.forName("org.apache.log4j.LogManager");
            assert null != frameworkClass;
        } catch (ClassNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public void initialize() throws Exception {
        synchronized (Log4jInitializer.class) {
            if (INITIALIZED) {
                return;
            }
            try {
                final Class<?> managerClass = Class.forName("org.apache.log4j.LogManager");
                final Field selectorField = managerClass.getDeclaredField("repositorySelector");
                selectorField.setAccessible(true);
                selectorField.set(null, Log4jRepositorySelector.INSTANCE);
                final Field guardField = managerClass.getDeclaredField("guard");
                guardField.setAccessible(true);
                guardField.set(null, new Object[0]);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Log4J framework not detected");
            } catch (NoSuchFieldException e) {
                throw new RuntimeException("Unsupported version of Log4J framework");
            } catch (Exception e) {
                throw new RuntimeException("Failed to intercept Log4J manager", e);
            } finally {
                INITIALIZED = true;
            }
        }
    }

}
