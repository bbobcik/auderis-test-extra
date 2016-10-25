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

import cz.auderis.test.logging.LogLevel;
import cz.auderis.test.logging.LogRecord;
import cz.auderis.test.logging.LogRecordCollector;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;

import java.util.Arrays;

public class TestLoggerAdapter extends MarkerIgnoringBase {

    public TestLoggerAdapter(String name) {
        this.name = name;
    }

    private boolean isLevelEnabled(LogLevel level) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        return recordCollector.getEnabledLevels().contains(level);
    }

    @Override
    public boolean isTraceEnabled() {
        return isLevelEnabled(LogLevel.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLevelEnabled(LogLevel.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLevelEnabled(LogLevel.INFO);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLevelEnabled(LogLevel.WARNING);
    }

    @Override
    public boolean isErrorEnabled() {
        return isLevelEnabled(LogLevel.ERROR);
    }

    private void log(LogLevel currentLevel, String s, Object o) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            assert !(o instanceof Throwable);
            final String msg = render(s, o);
            recordCollector.add(new LogRecord(currentLevel, name, msg, null));
        }
    }

    private void log(LogLevel currentLevel, String s, Object o1, Object o2) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            final String msg;
            final Throwable cause;
            if (o2 instanceof Throwable) {
                cause = (Throwable) o2;
                msg = render(s, o1);
            } else {
                cause = null;
                msg = render(s, o1, o2);
            }
            recordCollector.add(new LogRecord(currentLevel, name, msg, cause));
        }
    }

    private void log(LogLevel currentLevel, String s, Object[] objects) {
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            final String msg;
            final Throwable cause;
            if ((0 != objects.length) && (objects[objects.length - 1] instanceof Throwable)) {
                cause = (Throwable) objects[objects.length - 1];
                msg = render(s, true, objects);
            } else {
                cause = null;
                msg = render(s, false, objects);
            }
            recordCollector.add(new LogRecord(currentLevel, name, msg, cause));
        }
    }

    @Override
    public void trace(String s) {
        final LogLevel currentLevel = LogLevel.TRACE;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, null));
        }
    }

    @Override
    public void trace(String s, Throwable throwable) {
        final LogLevel currentLevel = LogLevel.TRACE;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, throwable));
        }
    }

    @Override
    public void trace(String s, Object o) {
        log(LogLevel.TRACE, s, o);
    }

    @Override
    public void trace(String s, Object o1, Object o2) {
        log(LogLevel.TRACE, s, o1, o2);
    }

    @Override
    public void trace(String s, Object... objects) {
        log(LogLevel.TRACE, s, objects);
    }

    @Override
    public void debug(String s) {
        final LogLevel currentLevel = LogLevel.DEBUG;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, null));
        }
    }

    @Override
    public void debug(String s, Throwable throwable) {
        final LogLevel currentLevel = LogLevel.DEBUG;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, throwable));
        }
    }

    @Override
    public void debug(String s, Object o) {
        log(LogLevel.DEBUG, s, o);
    }

    @Override
    public void debug(String s, Object o1, Object o2) {
        log(LogLevel.DEBUG, s, o1, o2);
    }

    @Override
    public void debug(String s, Object... objects) {
        log(LogLevel.DEBUG, s, objects);
    }

    @Override
    public void info(String s) {
        final LogLevel currentLevel = LogLevel.INFO;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, null));
        }
    }

    @Override
    public void info(String s, Throwable throwable) {
        final LogLevel currentLevel = LogLevel.INFO;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, throwable));
        }
    }

    @Override
    public void info(String s, Object o) {
        log(LogLevel.INFO, s, o);
    }

    @Override
    public void info(String s, Object o1, Object o2) {
        log(LogLevel.INFO, s, o1, o2);
    }

    @Override
    public void info(String s, Object... objects) {
        log(LogLevel.INFO, s, objects);
    }

    @Override
    public void warn(String s) {
        final LogLevel currentLevel = LogLevel.WARNING;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, null));
        }
    }

    @Override
    public void warn(String s, Throwable throwable) {
        final LogLevel currentLevel = LogLevel.WARNING;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, throwable));
        }
    }

    @Override
    public void warn(String s, Object o) {
        log(LogLevel.WARNING, s, o);
    }

    @Override
    public void warn(String s, Object o1, Object o2) {
        log(LogLevel.WARNING, s, o1, o2);
    }

    @Override
    public void warn(String s, Object... objects) {
        log(LogLevel.WARNING, s, objects);
    }

    @Override
    public void error(String s) {
        final LogLevel currentLevel = LogLevel.ERROR;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, null));
        }
    }

    @Override
    public void error(String s, Throwable throwable) {
        final LogLevel currentLevel = LogLevel.ERROR;
        final LogRecordCollector recordCollector = LogRecordCollector.RECORD_COLLECTORS.get();
        if (recordCollector.getEnabledLevels().contains(currentLevel)) {
            recordCollector.add(new LogRecord(currentLevel, name, s, throwable));
        }
    }

    @Override
    public void error(String s, Object o) {
        log(LogLevel.ERROR, s, o);
    }

    @Override
    public void error(String s, Object o1, Object o2) {
        log(LogLevel.ERROR, s, o1, o2);
    }

    @Override
    public void error(String s, Object... objects) {
        log(LogLevel.ERROR, s, objects);
    }

    private String render(String msg, Object o) {
        assert !(o instanceof Throwable);
        final FormattingTuple fmtTuple = MessageFormatter.arrayFormat(msg, new Object[] { o }, null);
        return fmtTuple.getMessage();
    }

    private String render(String msg, Object o1, Object o2) {
        assert !(o2 instanceof Throwable);
        final FormattingTuple fmtTuple = MessageFormatter.arrayFormat(msg, new Object[] { o1, o2 }, null);
        return fmtTuple.getMessage();
    }

    private String render(String msg, boolean ignoreLast, Object[] objects) {
        assert objects.length > 0;
        assert ignoreLast || !(objects[objects.length - 1] instanceof Throwable);
        final Object[] objectArg;
        if (!ignoreLast || (null == msg)) {
            objectArg = objects;
        } else {
            final int availableParameters = objects.length - 1;
            // If there is at most 'availableParameters' placeholders in the msg argument, we can safely pass
            // the reference to the original array, as the last array item will be ignored. Notice that the
            // number of placeholders is only approximated.
            int placeholderCount = 0;
            int searchIndex = 0;
            while ((placeholderCount <= availableParameters) && (-1 != (searchIndex = msg.indexOf('{', searchIndex)))) {
                ++placeholderCount;
                ++searchIndex;
            }
            if (placeholderCount <= availableParameters) {
                objectArg = objects;
            } else {
                objectArg = Arrays.copyOf(objects, availableParameters);
            }
        }
        final FormattingTuple fmtTuple = MessageFormatter.arrayFormat(msg, objectArg, null);
        return fmtTuple.getMessage();
    }

}
