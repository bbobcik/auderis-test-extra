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

package cz.auderis.test.matcher.log;

import cz.auderis.test.logging.LogLevel;
import cz.auderis.test.logging.LogRecord;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;

/**
 * Created on 24.10.2016.
 */
public class LogRecordMatcher extends TypeSafeMatcher<LogRecord> {

    private final Matcher<? super Date> timestampMatcher;
    private final Matcher<? super LogLevel> levelMatcher;
    private final Matcher<? super String> nameMatcher;
    private final Matcher<? super String> messageMatcher;

    protected LogRecordMatcher(Matcher<? super Date> timestamp, Matcher<? super LogLevel> level, Matcher<? super String> name, Matcher<? super String> message) {
        super(LogRecord.class);
        this.timestampMatcher = timestamp;
        this.levelMatcher = level;
        this.nameMatcher = name;
        this.messageMatcher = message;
    }

    @Override
    protected boolean matchesSafely(LogRecord item) {
        if ((null != timestampMatcher) && !timestampMatcher.matches(item.getTimestamp())) {
            return false;
        } else if ((null != levelMatcher) && !levelMatcher.matches(item.getLevel())) {
            return false;
        } else if ((null != nameMatcher) && !nameMatcher.matches(item.getLoggerName())) {
            return false;
        } else if ((null != messageMatcher) && !messageMatcher.matches(item.getMessage())) {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("log record");
        String separator = null;
        if (null != nameMatcher) {
            description.appendText(" with name that is ");
            nameMatcher.describeTo(description);
            separator = " and";
        }
        if (null != messageMatcher) {
            if (null == separator) {
                description.appendText(" with");
                separator = " and";
            } else {
                description.appendText(separator);
            }
            description.appendText(" message that is ");
            messageMatcher.describeTo(description);
        }
        if (null != levelMatcher) {
            if (null == separator) {
                description.appendText(" with");
                separator = " and";
            } else {
                description.appendText(separator);
            }
            description.appendText(" level that is ");
            levelMatcher.describeTo(description);
        }
        if (null != timestampMatcher) {
            if (null != separator) {
                description.appendText(separator);
            }
            description.appendText(" timestamp that is ");
            timestampMatcher.describeTo(description);
        }
    }

    @Override
    protected void describeMismatchSafely(LogRecord item, Description description) {
        String separator = null;
        if (null != nameMatcher) {
            description.appendText("name was ");
            nameMatcher.describeMismatch(item.getLoggerName(), description);
            separator = " and";
        }
        if (null != messageMatcher) {
            if (null == separator) {
                separator = " and";
            } else {
                description.appendText(separator);
            }
            description.appendText(" message was ");
            messageMatcher.describeMismatch(item.getMessage(), description);
        }
        if (null != levelMatcher) {
            if (null == separator) {
                separator = " and";
            } else {
                description.appendText(separator);
            }
            description.appendText(" level was ");
            levelMatcher.describeMismatch(item.getLevel(), description);
        }
        if (null != timestampMatcher) {
            if (null != separator) {
                description.appendText(separator);
            }
            description.appendText(" timestamp was ");
            timestampMatcher.describeMismatch(item.getTimestamp(), description);
        }
    }

    @Factory
    public static Matcher<? super LogRecord> hasName(Matcher<? super String> nameMatcher) {
        return new LogRecordMatcher(null, null, nameMatcher, null);
    }

    @Factory
    public static Matcher<? super LogRecord> hasName(String name) {
        return new LogRecordMatcher(null, null, is(name), null);
    }

    @Factory
    public static Matcher<? super LogRecord> hasLevel(Matcher<? super LogLevel> levelMatcher) {
        return new LogRecordMatcher(null, levelMatcher, null, null);
    }

    @Factory
    public static Matcher<? super LogRecord> hasLevel(LogLevel level) {
        return new LogRecordMatcher(null, is(level), null, null);
    }

    @Factory
    public static Matcher<? super LogRecord> hasMessage(Matcher<? super String> messageMatcher) {
        return new LogRecordMatcher(null, null, null, messageMatcher);
    }

    @Factory
    public static Matcher<? super LogRecord> hasMessage(String message) {
        return new LogRecordMatcher(null, null, null, is(message));
    }

}
