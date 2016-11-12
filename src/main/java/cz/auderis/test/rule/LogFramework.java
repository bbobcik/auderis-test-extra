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

import cz.auderis.test.logging.LogCaptureInitializer;
import cz.auderis.test.logging.LogRecordCollector;
import cz.auderis.test.logging.jboss.JBossLoggingInitializer;
import cz.auderis.test.logging.log4j.Log4jInitializer;
import cz.auderis.test.logging.slf4j.Slf4jInitializer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public class LogFramework implements TestRule {

    private final Set<Flavour> loggingFlavours;

    public static LogFramework everything() {
        return new LogFramework(EnumSet.allOf(Flavour.class));
    }

    public static LogFramework slf4j() {
        return new LogFramework(Flavour.SLF4J);
    }

    public static LogFramework jboss() { return new LogFramework(Flavour.JBOSS); }

    public static LogFramework log4j() { return new LogFramework(Flavour.LOG4J); }

    public LogFramework andSlf4j() {
        loggingFlavours.add(Flavour.SLF4J);
        return this;
    }

    public LogFramework andJboss() {
        loggingFlavours.add(Flavour.JBOSS);
        return this;
    }

    public LogFramework andLog4j() {
        loggingFlavours.add(Flavour.LOG4J);
        return this;
    }


    private LogFramework(Flavour loggingFlavour) {
        this.loggingFlavours = EnumSet.of(loggingFlavour);
    }

    private LogFramework(EnumSet<Flavour> flavours) {
        loggingFlavours = flavours;
    }


    @Override
    public Statement apply(final Statement base, Description description) {
        final boolean captureInitialized = initializeLogFrameworks();
        if (!captureInitialized) {
            return base;
        }
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                LogRecordCollector.RECORD_COLLECTOR.reset();
                base.evaluate();
            }
        };
    }

    private boolean initializeLogFrameworks() {
        final Set<Flavour> presentFrameworks = EnumSet.copyOf(loggingFlavours);
        for (Iterator<Flavour> iter = presentFrameworks.iterator(); iter.hasNext(); ) {
            final Flavour flavour = iter.next();
            final LogCaptureInitializer initializer = flavour.getInitializer();
            if (initializer.isFrameworkPresent()) {
                try {
                    initializer.initialize();
                } catch (Throwable e) {
                    // We failed to initialize the framework, but that is not a reason to cancel test
                    iter.remove();
                }
            } else {
                iter.remove();
            }
        }
        return !presentFrameworks.isEmpty();
    }

    private enum Flavour {
        SLF4J {
            @Override
            LogCaptureInitializer getInitializer() {
                return new Slf4jInitializer();
            }
        },
        JBOSS {
            @Override
            LogCaptureInitializer getInitializer() {
                return new JBossLoggingInitializer();
            }
        },
        LOG4J {
            @Override
            LogCaptureInitializer getInitializer() {
                return new Log4jInitializer();
            }
        }
        ;

        abstract LogCaptureInitializer getInitializer();
    }

}
