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
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class LogLevelMatcher extends TypeSafeMatcher<LogLevel> {

    private final Set<LogLevel> matchingLevels;

    public LogLevelMatcher(Collection<LogLevel> levels) {
        super(LogLevel.class);
        if ((null == levels) || levels.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            matchingLevels = EnumSet.copyOf(levels);
        }
    }

    @Override
    protected boolean matchesSafely(LogLevel item) {
        return matchingLevels.contains(item);
    }

    @Override
    public void describeTo(Description description) {
        if (matchingLevels.size() == 1) {
            final LogLevel singleLevel = matchingLevels.iterator().next();
            description.appendText("log level ").appendValue(singleLevel);
        } else {
            description.appendText("one of log levels ");
            description.appendValueList("", ", ", "", matchingLevels);
        }
    }

    @Override
    protected void describeMismatchSafely(LogLevel item, Description mismatchDescription) {
        mismatchDescription.appendText("logging level ").appendValue(item);
    }

    @Factory
    public static Matcher<? super LogLevel> atLeast(LogLevel threshold) {
        return new LogLevelMatcher(threshold.plusHigherLevels());
    }

    @Factory
    public static Matcher<? super LogLevel> higherThan(LogLevel threshold) {
        return new LogLevelMatcher(threshold.getHigherLevels());
    }

    @Factory
    public static Matcher<? super LogLevel> atMost(LogLevel threshold) {
        return new LogLevelMatcher(threshold.plusLowerLevels());
    }

    @Factory
    public static Matcher<? super LogLevel> lowerThan(LogLevel threshold) {
        return new LogLevelMatcher(threshold.getLowerLevels());
    }

    @Factory
    public static Matcher<? super LogLevel> between(LogLevel lowLevel, LogLevel highLevel) {
        final EnumSet<LogLevel> range;
        if (lowLevel.compareTo(highLevel) <= 0) {
            range = EnumSet.range(lowLevel, highLevel);
        } else {
            range = EnumSet.range(highLevel, lowLevel);
        }
        return new LogLevelMatcher(range);
    }

}
