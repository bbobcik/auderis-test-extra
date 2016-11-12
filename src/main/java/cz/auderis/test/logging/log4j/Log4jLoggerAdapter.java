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

import cz.auderis.test.logging.LogLevel;
import cz.auderis.test.logging.LogRecord;
import cz.auderis.test.logging.LogRecordCollector;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Log4jLoggerAdapter extends Logger {

    public static final Map<Level, LogLevel> LOG4J_TO_INTERNAL = prepareLevelMap();

    private static Map<Level, LogLevel> prepareLevelMap() {
        final Map<Level, LogLevel> levelMap = new HashMap<>();
        levelMap.put(Level.TRACE, LogLevel.TRACE);
        levelMap.put(Level.DEBUG, LogLevel.DEBUG);
        levelMap.put(Level.INFO, LogLevel.INFO);
        levelMap.put(Level.WARN, LogLevel.WARNING);
        levelMap.put(Level.ERROR, LogLevel.ERROR);
        levelMap.put(Level.FATAL, LogLevel.FATAL);
        return Collections.unmodifiableMap(levelMap);
    }

    protected Log4jLoggerAdapter(String name) {
        super(name);
    }

    @Override
    public void fatal(Object message) {
        forcedLog(null, Level.FATAL, message, null);
    }

    @Override
    public void fatal(Object message, Throwable t) {
        forcedLog(null, Level.FATAL, message, t);
    }

    @Override
    public void error(Object message) {
        forcedLog(null, Level.ERROR, message, null);
    }

    @Override
    public void error(Object message, Throwable t) {
        forcedLog(null, Level.ERROR, message, t);
    }

    @Override
    public void warn(Object message) {
        forcedLog(null, Level.WARN, message, null);
    }

    @Override
    public void warn(Object message, Throwable t) {
        forcedLog(null, Level.WARN, message, t);
    }

    @Override
    public void info(Object message) {
        forcedLog(null, Level.INFO, message, null);
    }

    @Override
    public void info(Object message, Throwable t) {
        forcedLog(null, Level.INFO, message, t);
    }

    @Override
    public void debug(Object message) {
        forcedLog(null, Level.DEBUG, message, null);
    }

    @Override
    public void debug(Object message, Throwable t) {
        forcedLog(null, Level.DEBUG, message, t);
    }

    @Override
    public void trace(Object message) {
        forcedLog(null, Level.TRACE, message, null);
    }

    @Override
    public void trace(Object message, Throwable t) {
        forcedLog(null, Level.TRACE, message, t);
    }

    @Override
    protected void forcedLog(String ignored, Priority level, Object message, Throwable t) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTOR;
        final LogLevel internalLevel = LOG4J_TO_INTERNAL.get((Level) level);
        if ((null == internalLevel) || !recordCollector.isLevelEnabled(internalLevel)) {
            return;
        }
        final String msgText;
        if (null == message) {
            msgText = "";
        } else if (message instanceof String) {
            msgText = (String) message;
        } else {
            msgText = message.toString();
        }
        final LogRecord record = new LogRecord(internalLevel, name, msgText, t);
        recordCollector.add(record);
    }

    @Override
    public boolean isTraceEnabled() {
        return LogRecordCollector.RECORD_COLLECTOR.isLevelEnabled(LogLevel.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return LogRecordCollector.RECORD_COLLECTOR.isLevelEnabled(LogLevel.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return LogRecordCollector.RECORD_COLLECTOR.isLevelEnabled(LogLevel.INFO);
    }

}
