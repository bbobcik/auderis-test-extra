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

import cz.auderis.test.category.SanityTest;
import cz.auderis.test.rule.LogBuffer;
import cz.auderis.test.rule.LogFramework;
import junitparams.JUnitParamsRunner;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.test.temp.log.Slf4jStaticTester;

import java.util.List;

import static cz.auderis.test.matcher.log.LogRecordMatcher.hasLevel;
import static cz.auderis.test.matcher.log.LogRecordMatcher.hasMessage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

@RunWith(JUnitParamsRunner.class)
public class Slf4jLoggingRuleTest {

    @ClassRule
    public static LogFramework logFramework = LogFramework.slf4j();

    @Rule
    public LogBuffer logBuffer = new LogBuffer();


    @Test
    @Category(SanityTest.class)
    public void shouldInterceptSlf4jLogging() throws Exception {
        // Given
        logBuffer.levels().enableOnly(LogLevel.INFO.plusHigherLevels());
        Slf4jStaticTester tester = new Slf4jStaticTester();

        // When
        tester.doWork();

        // Then
        final List<LogRecord> infoRecords = logBuffer.getRecords(hasLevel(LogLevel.INFO));
        assertThat(infoRecords, hasSize(1));
        final LogRecord logRecord = infoRecords.get(0);
        assertThat(logRecord, hasLevel(LogLevel.INFO));
        assertThat(logRecord, hasMessage("Work 3"));
    }

}
