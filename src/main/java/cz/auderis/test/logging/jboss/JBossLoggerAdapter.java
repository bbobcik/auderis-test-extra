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

import cz.auderis.test.logging.LogLevel;
import cz.auderis.test.logging.LogRecord;
import cz.auderis.test.logging.LogRecordCollector;
import org.jboss.logging.Logger;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public class JBossLoggerAdapter extends Logger {

    public static final Map<Level, LogLevel> JBOSS_TO_INTERNAL = prepareLevelMap1();

    private static Map<Level, LogLevel> prepareLevelMap1() {
        final EnumMap levelMap = new EnumMap(Level.class);
        levelMap.put(Level.TRACE, LogLevel.TRACE);
        levelMap.put(Level.DEBUG, LogLevel.DEBUG);
        levelMap.put(Level.INFO, LogLevel.INFO);
        levelMap.put(Level.WARN, LogLevel.WARNING);
        levelMap.put(Level.ERROR, LogLevel.ERROR);
        levelMap.put(Level.FATAL, LogLevel.FATAL);
        return Collections.unmodifiableMap(levelMap);
    }

    protected JBossLoggerAdapter(String name) {
        super(name);
    }

    @Override
    public boolean isEnabled(Level level) {
        final LogLevel internalLevel = JBOSS_TO_INTERNAL.get(level);
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        return recordCollector.getEnabledLevels().contains(internalLevel);
    }

    @Override
    protected void doLogf(Level level, String loggerClassName, String format, Object[] parameters, Throwable thrown) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        final LogLevel internalLevel = JBOSS_TO_INTERNAL.get(level);
        if (!recordCollector.getEnabledLevels().contains(internalLevel)) {
            return;
        }
        final String message;
        if (null == parameters) {
            message = String.format(format);
        } else {
            message = String.format(format, parameters);
        }
        final LogRecord record = new LogRecord(internalLevel, loggerClassName, message, thrown);
        recordCollector.add(record);
    }

    @Override
    protected void doLog(Level level, String loggerClassName, Object msgObj, Object[] parameters, Throwable thrown) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        final LogLevel internalLevel = JBOSS_TO_INTERNAL.get(level);
        if (!recordCollector.getEnabledLevels().contains(internalLevel)) {
            return;
        }
        final String message;
        if ((null == parameters) || (0 == parameters.length)) {
            message = String.valueOf(msgObj);
        } else {
            message = MessageFormat.format(String.valueOf(msgObj), parameters);
        }
        final LogRecord record = new LogRecord(internalLevel, loggerClassName, message, thrown);
        recordCollector.add(record);
    }

}
