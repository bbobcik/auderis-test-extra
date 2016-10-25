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

public class Slf4jInitializer implements Runnable {

    @Override
    public void run() {
        ensureApiAvailable();
        prepareBinder();

    }

    private void prepareBinder() {
        final ClassLoader classLoader = this.getClass().getClassLoader();
//        classLoader.


    }

    private void ensureApiAvailable() {
        try {
            final ClassLoader classLoader = this.getClass().getClassLoader();
            final Class<?> loggerClass = Class.forName("org.slf4j.Logger", false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot use SLF4J - API library not available");
        }
    }

}
