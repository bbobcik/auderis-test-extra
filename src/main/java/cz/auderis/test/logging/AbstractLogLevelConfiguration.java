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
import java.util.EnumSet;
import java.util.Set;

public abstract class AbstractLogLevelConfiguration implements LogLevelConfiguration {

    protected abstract Set<LogLevel> getLevelSet();
    private transient String description;

    protected void levelSetChanged() {
        // No operation, intended to be overridden as a notification mechanism
    }

    @Override
    public Set<LogLevel> getEnabledLevels() {
        return EnumSet.copyOf(getLevelSet());
    }

    @Override
    public boolean isLevelEnabled(LogLevel level) {
        if (null == level) {
            throw new NullPointerException();
        }
        return getLevelSet().contains(level);
    }

    @Override
    public void setEnabledLevels(Set<LogLevel> levelsToEnable) {
        ensureValidLevels(levelsToEnable);
        final Set<LogLevel> targetLevels = getLevelSet();
        final boolean levelsRemoved = targetLevels.retainAll(levelsToEnable);
        final boolean levelsAdded = targetLevels.addAll(levelsToEnable);
        if (levelsAdded || levelsRemoved) {
            description = null;
            levelSetChanged();
        }
    }

    @Override
    public LogLevelConfiguration enableOnly(LogLevel... levelsToEnable) {
        ensureValidLevels(levelsToEnable);
        final Set<LogLevel> newLevelSet;
        if (0 == levelsToEnable.length) {
            newLevelSet = EnumSet.noneOf(LogLevel.class);
        } else {
            newLevelSet = EnumSet.of(levelsToEnable[0], levelsToEnable);
        }
        setEnabledLevels(newLevelSet);
        return this;
    }

    @Override
    public LogLevelConfiguration enableOnly(Collection<LogLevel> levelsToEnable) {
        ensureValidLevels(levelsToEnable);
        final Set<LogLevel> newLevels;
        if (levelsToEnable instanceof Set) {
            newLevels = (Set<LogLevel>) levelsToEnable;
        } else {
            newLevels = EnumSet.copyOf(levelsToEnable);
        }
        setEnabledLevels(newLevels);
        return this;
    }

    @Override
    public LogLevelConfiguration enableAllLevels() {
        setEnabledLevels(EnumSet.allOf(LogLevel.class));
        return this;
    }

    @Override
    public LogLevelConfiguration disableAllLevels() {
        setEnabledLevels(EnumSet.noneOf(LogLevel.class));
        return this;
    }

    @Override
    public LogLevelConfiguration enable(LogLevel... levelsToEnable) {
        ensureValidLevels(levelsToEnable);
        if (levelsToEnable.length > 0) {
            final Set<LogLevel> newlyAddedLevels = EnumSet.of(levelsToEnable[0], levelsToEnable);
            final boolean levelsAdded = getLevelSet().addAll(newlyAddedLevels);
            if (levelsAdded) {
                description = null;
                levelSetChanged();
            }
        }
        return this;
    }

    @Override
    public LogLevelConfiguration enable(Collection<LogLevel> levelsToEnable) {
        ensureValidLevels(levelsToEnable);
        final boolean levelsAdded = getLevelSet().addAll(levelsToEnable);
        if (levelsAdded) {
            description = null;
            levelSetChanged();
        }
        return this;
    }

    @Override
    public LogLevelConfiguration disable(LogLevel... levelsToDisable) {
        ensureValidLevels(levelsToDisable);
        if (levelsToDisable.length > 0) {
            final Set<LogLevel> newlyRemovedLevels = EnumSet.of(levelsToDisable[0], levelsToDisable);
            final boolean levelsRemoved = getLevelSet().removeAll(newlyRemovedLevels);
            if (levelsRemoved) {
                description = null;
                levelSetChanged();
            }
        }
        return this;
    }

    @Override
    public LogLevelConfiguration disable(Collection<LogLevel> levelsToDisable) {
        ensureValidLevels(levelsToDisable);
        final boolean levelsRemoved = getLevelSet().removeAll(levelsToDisable);
        if (levelsRemoved) {
            description = null;
            levelSetChanged();
        }
        return this;
    }

    @Override
    public LogLevelConfiguration enableLevelsAtOrAbove(LogLevel thresholdLevel) {
        if (null == thresholdLevel) {
            throw new NullPointerException();
        }
        final Set<LogLevel> newlyAddedLevels = thresholdLevel.plusHigherLevels();
        final boolean levelsAdded = getLevelSet().addAll(newlyAddedLevels);
        if (levelsAdded) {
            description = null;
            levelSetChanged();
        }
        return this;
    }

    @Override
    public LogLevelConfiguration disableLevelsBelow(LogLevel thresholdLevel) {
        if (null == thresholdLevel) {
            throw new NullPointerException();
        }
        final Set<LogLevel> removedLevels = thresholdLevel.getLowerLevels();
        final boolean levelsRemoved = getLevelSet().removeAll(removedLevels);
        if (levelsRemoved) {
            description = null;
            levelSetChanged();
        }
        return this;
    }

    private void ensureValidLevels(Collection<LogLevel> levels) {
        if (null == levels) {
            throw new NullPointerException("undefined set of logging levels");
        }
        for (final LogLevel level : levels) {
            if (null == level) {
                throw new IllegalArgumentException("set of logging levels contains null value");
            }
        }
    }

    private void ensureValidLevels(LogLevel[] levels) {
        for (final LogLevel level : levels) {
            if (null == level) {
                throw new IllegalArgumentException("set of logging levels contains null value");
            }
        }
    }

    @Override
    public String toString() {
        if (null == description) {
            final StringBuilder str = new StringBuilder(128);
            str.append("LogLevelCfg{levels=");
            final Set<LogLevel> levels = getLevelSet();
            char sep = 0;
            for (final LogLevel level : LogLevel.values()) {
                if (!levels.contains(level)) {
                    continue;
                }
                if (0 != sep) {
                    str.append(sep);
                } else {
                    sep = ',';
                }
                str.append(level.name());
            }
            if (0 == sep) {
                str.append("none");
            }
            str.append('}');
            description = str.toString();
        }
        return description;
    }

}
