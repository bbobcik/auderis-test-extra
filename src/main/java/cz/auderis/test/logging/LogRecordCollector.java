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

public class LogRecordCollector {

    private static final EnumSet<LogLevel> INITIAL_LEVELS_INTERNAL = LogLevel.DEBUG.plusHigherLevels();
    public static final Set<LogLevel> INITIAL_LOG_LEVELS = Collections.unmodifiableSet(INITIAL_LEVELS_INTERNAL);
    public static final LogRecordCollector RECORD_COLLECTOR = new LogRecordCollector();

    private final List<LogRecord> collectedRecords;
    private final EnumSet<LogLevel> enabledLevels;

    LogRecordCollector() {
        collectedRecords = new ArrayList<LogRecord>(1024);
        enabledLevels = EnumSet.copyOf(INITIAL_LEVELS_INTERNAL);
    }

    public synchronized void reset() {
        collectedRecords.clear();
        enabledLevels.clear();
        enabledLevels.addAll(INITIAL_LEVELS_INTERNAL);
    }

    public synchronized boolean isLevelEnabled(LogLevel level) {
        return enabledLevels.contains(level);
    }

    public synchronized Set<LogLevel> getEnabledLevels() {
        return EnumSet.copyOf(enabledLevels);
    }

    public synchronized void setEnabledLevels(Set<LogLevel> newLevels) {
        if (null == newLevels) {
            throw new NullPointerException();
        }
        enabledLevels.clear();
        enabledLevels.addAll(newLevels);
    }

    public synchronized void add(LogRecord record) {
        if (enabledLevels.contains(record.getLevel())) {
            collectedRecords.add(record);
        }
    }

    public synchronized List<LogRecord> getRecords() {
        return new ArrayList<>(collectedRecords);
    }

    public synchronized void dump(PrintStream out, LogLevel threshold) {
        final Set<LogLevel> dumpLevels = threshold.plusHigherLevels();
        for (final LogRecord record : collectedRecords) {
            final LogLevel level = record.getLevel();
            if (!dumpLevels.contains(level)) {
                continue;
            }
            // TODO
        }
    }

}
