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

package cz.auderis.test.logging;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 23.10.2016.
 */
public class LogRecordCollector {

    public static final ThreadLocal<LogRecordCollector> RECORD_COLLECTORS = initRecordCollectors();
    private static final EnumSet<LogLevel> INITIAL_LEVELS_INTERNAL = LogLevel.DEBUG.plusHigherLevels();
    public static final Set<LogLevel> INITIAL_LOG_LEVELS = Collections.unmodifiableSet(INITIAL_LEVELS_INTERNAL);

    private final List<LogRecord> collectedRecords;
    private final EnumSet<LogLevel> enabledLevels;

    public LogRecordCollector() {
        collectedRecords = new ArrayList<LogRecord>(1024);
        enabledLevels = EnumSet.copyOf(INITIAL_LEVELS_INTERNAL);
    }

    public void reset() {
        collectedRecords.clear();
        enabledLevels.clear();;
        enabledLevels.addAll(INITIAL_LEVELS_INTERNAL);
    }

    public Set<LogLevel> getEnabledLevels() {
        return EnumSet.copyOf(enabledLevels);
    }

    public void setEnabledLevels(Set<LogLevel> newLevels) {
        if (null == newLevels) {
            throw new NullPointerException();
        }
        enabledLevels.clear();
        enabledLevels.addAll(newLevels);
    }

    public void add(LogRecord record) {
        if (enabledLevels.contains(record.getLevel())) {
            collectedRecords.add(record);
        }
    }

    public List<LogRecord> getRecords() {
        return collectedRecords;
    }

    public void dump(PrintStream out, LogLevel threshold) {
        final Set<LogLevel> dumpLevels = threshold.plusHigherLevels();
        for (final LogRecord record : collectedRecords) {
            final LogLevel level = record.getLevel();
            if (!dumpLevels.contains(level)) {
                continue;
            }
            // TODO
        }
    }

    private static ThreadLocal<LogRecordCollector> initRecordCollectors() {
        return new ThreadLocal<LogRecordCollector>() {
            @Override
            protected LogRecordCollector initialValue() {
                return new LogRecordCollector();
            }
        };
    }

}
