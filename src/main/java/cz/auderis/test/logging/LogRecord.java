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

import java.util.Date;

public class LogRecord {

    final long timestamp;
    final LogLevel level;
    final String loggerName;
    final String message;
    final Throwable cause;

    public LogRecord(LogLevel level, String loggerName, String message, Throwable cause) {
        if ((null == level) || (null == loggerName) || (null == message)) {
            throw new NullPointerException();
        }
        this.timestamp = System.currentTimeMillis();
        this.level = level;
        this.loggerName = loggerName;
        this.message = message;
        this.cause = cause;
    }

    public Date getTimestamp() {
        return new Date(timestamp);
    }

    public LogLevel getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public String getMessage() {
        return message;
    }

    public Throwable getCause() {
        return cause;
    }

}
