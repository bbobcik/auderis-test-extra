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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;

public enum LogLevel {

    FATAL,
    ERROR,
    WARNING,
    INFO,
    DEBUG,
    TRACE
    ;

    public EnumSet<LogLevel> getHigherLevels() {
        if (FATAL == this) {
            return EnumSet.noneOf(LogLevel.class);
        } else {
            return EnumSet.range(FATAL, values()[ordinal() - 1]);
        }
    }

    public EnumSet<LogLevel> getLowerLevels() {
        if (TRACE == this) {
            return EnumSet.noneOf(LogLevel.class);
        } else {
            return EnumSet.range(values()[ordinal() + 1], TRACE);
        }
    }

    public EnumSet<LogLevel> plusHigherLevels() {
        return EnumSet.range(FATAL, this);
    }

    public EnumSet<LogLevel> plusLowerLevels() {
        return EnumSet.range(this, TRACE);
    }

    public static LogLevel lowestLevel(Collection<LogLevel> levels) {
        if (levels.isEmpty()) {
            return FATAL;
        }
        return Collections.min(levels);
    }

}
