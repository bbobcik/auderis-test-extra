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

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TestLoggerFactory implements ILoggerFactory {

    final ConcurrentMap<String, TestLoggerAdapter> loggerMap;

    public TestLoggerFactory() {
        loggerMap = new ConcurrentHashMap<String, TestLoggerAdapter>(16);
    }

    @Override
    public Logger getLogger(String loggerName) {
        final TestLoggerAdapter logger = loggerMap.get(loggerName);
        if (null != logger) {
            return logger;
        }
        final TestLoggerAdapter newLogger = new TestLoggerAdapter(loggerName);
        final TestLoggerAdapter oldLogger = loggerMap.putIfAbsent(loggerName, newLogger);
        return (null != oldLogger) ? oldLogger : newLogger;
    }

}
