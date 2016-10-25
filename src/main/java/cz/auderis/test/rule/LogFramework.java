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

package cz.auderis.test.rule;

import cz.auderis.test.logging.slf4j.Slf4jInitializer;
import org.junit.rules.ExternalResource;

public class LogFramework extends ExternalResource {

    private final Flavour loggingFlavour;

    public static LogFramework slf4j() {
        return new LogFramework(Flavour.SLF4J);
    }

    private LogFramework(Flavour loggingFlavour) {
        this.loggingFlavour = loggingFlavour;
    }

    @Override
    protected void before() throws Throwable {
        assert null != loggingFlavour;
        final Runnable initializer = loggingFlavour.getInitializer();
        initializer.run();
    }

    private enum Flavour {
        SLF4J {
            @Override
            Runnable getInitializer() {
                return new Slf4jInitializer();
            }
        }
        ;

        abstract Runnable getInitializer();
    }

}
