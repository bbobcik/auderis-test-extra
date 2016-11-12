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
import cz.auderis.test.logging.LogRecordCollector;
import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.HierarchyEventListener;
import org.apache.log4j.spi.LoggerFactory;
import org.apache.log4j.spi.LoggerRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class Log4jLoggerRepository implements LoggerRepository {

    public static final Map<LogLevel, Level> INTERNAL_TO_LOG4J = prepareLevelMap();
    static final Log4jLoggerRepository INSTANCE = new Log4jLoggerRepository();
    private static final ConcurrentMap<String, Log4jLoggerAdapter> LOGGERS = new ConcurrentHashMap<>(16);
    private static final String ROOT_LOGGER_NAME = "";

    private static Map<LogLevel, Level> prepareLevelMap() {
        final Map<LogLevel, Level> levelMap = new EnumMap<LogLevel, Level>(LogLevel.class);
        levelMap.put(LogLevel.TRACE, Level.TRACE);
        levelMap.put(LogLevel.DEBUG, Level.DEBUG);
        levelMap.put(LogLevel.INFO, Level.INFO);
        levelMap.put(LogLevel.WARNING, Level.WARN);
        levelMap.put(LogLevel.ERROR, Level.ERROR);
        levelMap.put(LogLevel.FATAL, Level.FATAL);
        return Collections.unmodifiableMap(levelMap);
    }

    Log4jLoggerRepository() {
    }

    @Override
    public Logger getLogger(String name) {
        Logger logger = LOGGERS.get(name);
        if (null == logger) {
            final Log4jLoggerAdapter newLogger = new Log4jLoggerAdapter(name);
            final Log4jLoggerAdapter origLogger = LOGGERS.putIfAbsent(name, newLogger);
            logger = (null != origLogger) ? origLogger : newLogger;
        }
        return logger;
    }

    @Override
    public Logger getLogger(String name, LoggerFactory factory) {
        return getLogger(name);
    }

    @Override
    public Logger exists(String name) {
        return LOGGERS.get(name);
    }

    @Override
    public Logger getRootLogger() {
        return getLogger(ROOT_LOGGER_NAME);
    }

    @Override
    public Enumeration getCurrentLoggers() {
        final Set<String> loggerNames = LOGGERS.keySet();
        loggerNames.remove(ROOT_LOGGER_NAME);
        if (loggerNames.isEmpty()) {
            return Collections.emptyEnumeration();
        }
        final List<Logger> loggerInstances = new ArrayList<>(loggerNames.size());
        for (final String loggerName : loggerNames) {
            final Log4jLoggerAdapter instance = LOGGERS.get(loggerName);
            if (null != instance) {
                loggerInstances.add(instance);
            }
        }
        return Collections.enumeration(loggerInstances);
    }

    @Override
    public Enumeration getCurrentCategories() {
        return getCurrentLoggers();
    }

    @Override
    public void resetConfiguration() {
        // Ignored
    }

    @Override
    public void emitNoAppenderWarning(Category cat) {
        // Ignored
    }

    @Override
    public void shutdown() {
        // Ignored
    }

    public void setThreshold(String levelStr) {
        final Level level = Level.toLevel(levelStr, null);
        assert null != level : "Invalid Log4J level: " + levelStr;
        final LogLevel internalLevel = Log4jLoggerAdapter.LOG4J_TO_INTERNAL.get(level);
        assert null != internalLevel;
        LogRecordCollector.RECORD_COLLECTOR.setEnabledLevels(internalLevel.plusHigherLevels());
    }

    public void setThreshold(Level level) {
        final LogLevel internalLevel = Log4jLoggerAdapter.LOG4J_TO_INTERNAL.get(level);
        assert null != internalLevel;
        LogRecordCollector.RECORD_COLLECTOR.setEnabledLevels(internalLevel.plusHigherLevels());
    }

    @Override
    public Level getThreshold() {
        final Set<LogLevel> enabledLevels = LogRecordCollector.RECORD_COLLECTOR.getEnabledLevels();
        final LogLevel threshold = LogLevel.lowestLevel(enabledLevels);
        return INTERNAL_TO_LOG4J.get(threshold);
    }

    @Override
    public boolean isDisabled(int level) {
        return getThreshold().toInt() > level;
    }

    @Override
    public void addHierarchyEventListener(HierarchyEventListener listener) {
        // Ignored
    }

    @Override
    public void fireAddAppenderEvent(Category logger, Appender appender) {
        // Ignored
    }

}
