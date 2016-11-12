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

import cz.auderis.test.logging.AbstractLogLevelConfiguration;
import cz.auderis.test.logging.LogLevel;
import cz.auderis.test.logging.LogLevelConfiguration;
import cz.auderis.test.logging.LogRecord;
import cz.auderis.test.logging.LogRecordCollector;
import org.hamcrest.Matcher;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Created on 23.10.2016.
 */
public class LogBuffer extends TestWatcher {

    private final Set<LogLevel> enabledLevels;
    private LogLevel failureLogDumpThreshold;
    private LogLevel successLogDumpThreshold;

    public LogBuffer() {
        enabledLevels = EnumSet.allOf(LogLevel.class);
    }

    public LogLevelConfiguration levels() {
        return new LogLevelConfigurationImpl();
    }

    public LogLevel getSuccessLogDumpThreshold() {
        return successLogDumpThreshold;
    }

    public void setSuccessLogDumpThreshold(LogLevel successLogDumpThreshold) {
        this.successLogDumpThreshold = successLogDumpThreshold;
    }

    public void dumpOnSuccess() {
        this.successLogDumpThreshold = LogLevel.INFO;
    }

    public void dumpOnSuccess(LogLevel threshold) {
        this.successLogDumpThreshold = threshold;
    }

    public LogLevel getFailureLogDumpThreshold() {
        return failureLogDumpThreshold;
    }

    public void setFailureLogDumpThreshold(LogLevel failureLogDumpThreshold) {
        this.failureLogDumpThreshold = failureLogDumpThreshold;
    }

    public void dumpOnFailure() {
        this.failureLogDumpThreshold = LogLevel.INFO;
    }

    public void dumpOnFailure(LogLevel threshold) {
        this.failureLogDumpThreshold = threshold;
    }

    public List<LogRecord> getRecords() {
        final LogRecordCollector collector = LogRecordCollector.RECORD_COLLECTOR;
        return new ArrayList<LogRecord>(collector.getRecords());
    }

    public List<LogRecord> getRecords(Matcher<? super LogRecord> recordMatcher) {
        final LogRecordCollector collector = LogRecordCollector.RECORD_COLLECTOR;
        final List<LogRecord> allRecords = collector.getRecords();
        final List<LogRecord> result = new ArrayList<LogRecord>(allRecords.size());
        if ((null == recordMatcher) || allRecords.isEmpty()) {
            result.addAll(allRecords);
        } else {
            for (final LogRecord record : allRecords) {
                if (recordMatcher.matches(record)) {
                    result.add(record);
                }
            }
        }
        return result;
    }

    @Override
    protected void starting(Description description) {
        resetLogging();
    }

    @Override
    protected void failed(Throwable e, Description description) {
        dumpLog(failureLogDumpThreshold);
    }

    @Override
    protected void succeeded(Description description) {
        dumpLog(successLogDumpThreshold);
    }

    private void resetLogging() {
        final LogRecordCollector collector = LogRecordCollector.RECORD_COLLECTOR;
        collector.reset();
        enabledLevels.clear();
        enabledLevels.addAll(collector.getEnabledLevels());
    }

    private void dumpLog(LogLevel threshold) {
        if (null == threshold) {
            return;
        }
        final LogRecordCollector collector = LogRecordCollector.RECORD_COLLECTOR;
        collector.dump(System.out, threshold);
    }

    private class LogLevelConfigurationImpl extends AbstractLogLevelConfiguration {
        @Override
        protected Set<LogLevel> getLevelSet() {
            return enabledLevels;
        }

        @Override
        protected void levelSetChanged() {
            super.levelSetChanged();
            LogRecordCollector.RECORD_COLLECTOR.setEnabledLevels(enabledLevels);
        }
    }

}
