/*
 * Copyright 2017 Boleslav Bobcik - Auderis
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

package cz.auderis.test.matcher.array;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created on 10.5.2017.
 */
public class ArrayPartMatcher extends TypeSafeMatcher<Object[]> {

    public static <T> ArrayPartMatcher contains(T... referenceItems) {
        return new ArrayPartMatcher(referenceItems);
    }

    public static <T> Matcher<? super Object[]> startsWith(T... referenceItems) {
        final ArrayPartMatcher matcher = new ArrayPartMatcher(referenceItems);
        matcher.withStartIndex(0);
        return matcher;
    }

    public static <T> Matcher<? super Object[]> endsWith(T... referenceItems) {
        final ArrayPartMatcher matcher = new ArrayPartMatcher(referenceItems);
        matcher.withNumberOfFollowingItems(0);
        return matcher;
    }

    public ArrayPartMatcher withStartIndex(int idx) {
        if (idx < 0) {
            throw new IllegalArgumentException("Start index must be non-negative: " + idx);
        }
        requiredOffset = idx;
        matchStartMatcher = null;
        matchEndMatcher = null;
        reinitialize();
        return this;
    }

    public ArrayPartMatcher withStartIndex(Matcher<? super Integer> startIndexConstraint) {
        if (null == startIndexConstraint) {
            throw new NullPointerException();
        }
        requiredOffset = null;
        matchStartMatcher = startIndexConstraint;
        reinitialize();
        return this;
    }

    public ArrayPartMatcher withNumberOfFollowingItems(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Following item count must be non-negative: " + count);
        }
        requiredOffset = -count - 1;
        matchStartMatcher = null;
        matchEndMatcher = null;
        reinitialize();
        return this;
    }

    public ArrayPartMatcher withNumberOfFollowingItems(Matcher<? super Integer> followingItemsConstraint) {
        if (null == followingItemsConstraint) {
            throw new NullPointerException();
        }
        requiredOffset = null;
        matchEndMatcher = followingItemsConstraint;
        reinitialize();
        return this;
    }


    final Object[] referenceSubarray;
    final int referenceLength;
    Integer requiredOffset;
    Matcher<? super Integer> matchStartMatcher;
    Matcher<? super Integer> matchEndMatcher;
    MatcherImplementation matcher;

    ArrayPartMatcher(Object[] referenceSubarray) {
        if (null == referenceSubarray) {
            throw new NullPointerException();
        }
        this.referenceSubarray = referenceSubarray;
        this.referenceLength = referenceSubarray.length;
        reinitialize();
    }

    void reinitialize() {
        if (null != requiredOffset) {
            if (requiredOffset >= 0) {
                this.matcher = new FixedStartMatcher();
            } else {
                this.matcher = new FixedEndMatcher();
            }
        } else {
            this.matcher = new FullScanMatcher();
        }
        assert null != matcher;
        matcher.initialize();
    }

    @Override
    protected boolean matchesSafely(Object[] array) {
        return matcher.match(array);
    }

    @Override
    public void describeTo(Description description) {
        matcher.describe(description);
    }

    @Override
    protected void describeMismatchSafely(Object[] array, Description mismatchDescription) {
        matcher.describeMismatch(array, mismatchDescription);
    }

    interface MatcherImplementation {
        void initialize();
        boolean match(Object[] testedArray);
        void describe(Description description);
        void describeMismatch(Object[] testedArray, Description description);
    }

    class FixedStartMatcher implements MatcherImplementation {
        @Override
        public void initialize() {
            if (null == requiredOffset) {
                throw new NullPointerException("Offset was not provided");
            } else if (requiredOffset < 0) {
                throw new IllegalArgumentException("Offset must be positive: " + requiredOffset);
            }
        }

        protected int getStartIndex(Object[] testedArray) {
            return requiredOffset;
        }

        @Override
        public boolean match(Object[] testedArray) {
            final int startOffset = getStartIndex(testedArray);
            if (startOffset + referenceLength > testedArray.length) {
                return false;
            }
            int testIdx = startOffset;
            for (final Object refItem : referenceSubarray) {
                final Object testItem = testedArray[testIdx];
                if (!Objects.equals(testItem, refItem)) {
                    return false;
                }
                ++testIdx;
            }
            return true;
        }

        @Override
        public void describe(Description desc) {
            if (0 == requiredOffset) {
                desc.appendText("array that starts with prefix ");
            } else {
                desc.appendText("array that contains ");
            }
            desc.appendValueList("[", ", ", "]", referenceSubarray);
            if (referenceLength > 3) {
                desc.appendText(" of size " + referenceLength);
            }
            if (0 != requiredOffset) {
                desc.appendText(" starting at offset " + requiredOffset);
            }
        }

        @Override
        public void describeMismatch(Object[] testedArray, Description desc) {
            desc.appendText("array ");
            desc.appendValueList("[", ", ", "]", testedArray);
            final int startOffset = getStartIndex(testedArray);
            final int missingItems = startOffset + referenceLength - testedArray.length;
            if (missingItems > 0) {
                desc.appendText(" has insufficient length (missing " + missingItems + " items)");
                return;
            }
            // Offset list will contain pairs of start and end offset of a mismatch group
            List<Integer> mismatchGroupOffsets = null;
            boolean mismatchGroupOpen = false;
            int testIdx = startOffset - 1;
            for (final Object refItem : referenceSubarray) {
                ++testIdx;
                final Object testItem = testedArray[testIdx];
                if (Objects.equals(testItem, refItem)) {
                    if (mismatchGroupOpen) {
                        mismatchGroupOpen = false;
                        assert 1 == (mismatchGroupOffsets.size() & 1);
                        mismatchGroupOffsets.add(testIdx - 1);
                    }
                } else if (!mismatchGroupOpen) {
                    mismatchGroupOpen = true;
                    if (null == mismatchGroupOffsets) {
                        mismatchGroupOffsets = new ArrayList<>(8);
                    }
                    assert 0 == (mismatchGroupOffsets.size() & 1);
                    mismatchGroupOffsets.add(testIdx);
                } else {
                    // Not matching and mismatch group active - no operation needed
                }
            }
            assert null != mismatchGroupOffsets;
            if (mismatchGroupOpen) {
                assert 1 == (mismatchGroupOffsets.size() & 1);
                mismatchGroupOffsets.add(testIdx);
            }
            desc.appendText(" ");
            appendMismatchGroups(mismatchGroupOffsets, desc);
        }

        void appendMismatchGroups(List<Integer> mismatchGroupOffsets, Description desc) {
            assert 0 == (mismatchGroupOffsets.size() & 1);
            final int mismatchGroups = mismatchGroupOffsets.size() / 2;
            final int firstStart = mismatchGroupOffsets.get(0);
            final int firstEnd = mismatchGroupOffsets.get(1);
            if (1 == mismatchGroups) {
                desc.appendText("contains mismatch ");
                if (firstStart == firstEnd) {
                    desc.appendText("at offset " + firstStart);
                } else {
                    desc.appendText("block at offsets " + firstStart + '-' + firstEnd);
                }
            } else {
                desc.appendText("contains " + mismatchGroups + " mismatching blocks, first ");
                if (firstStart == firstEnd) {
                    desc.appendText("at offset " + firstStart);
                } else {
                    desc.appendText("at offsets " + firstStart + '-' + firstEnd);
                }
            }
        }
    }

    class FixedEndMatcher extends FixedStartMatcher {
        @Override
        public void initialize() {
            if (null == requiredOffset) {
                throw new NullPointerException("Offset was not provided");
            } else if (requiredOffset >= 0) {
                throw new IllegalArgumentException("Offset must be negative: " + requiredOffset);
            }        }

        @Override
        protected int getStartIndex(Object[] testedArray) {
            int startOffset = testedArray.length - referenceLength + requiredOffset + 1;
            if (startOffset < 0) {
                startOffset = 0;
            }
            return startOffset;
        }

        @Override
        public void describe(Description desc) {
            if (-1 == requiredOffset) {
                desc.appendText("array that ends with suffix ");
            } else {
                desc.appendText("array that contains ");
            }
            desc.appendValueList("[", ", ", "]", referenceSubarray);
            if (referenceLength > 3) {
                desc.appendText(" of size " + referenceLength);
            }
            if (-1 != requiredOffset) {
                final int trailingItems = -requiredOffset - 1;
                desc.appendText(" with " + trailingItems + " following");
            }
        }
    }

    class FullScanMatcher implements MatcherImplementation {
        int offsetShift;

        @Override
        public void initialize() {
            offsetShift = 1;
            if (referenceLength > 1) {
                final Object tailItem = referenceSubarray[referenceLength - 1];
                for (int idx=referenceLength - 2; idx >= 0; --idx) {
                    final Object item = referenceSubarray[idx];
                    if (Objects.equals(item, tailItem)) {
                        break;
                    }
                    ++offsetShift;
                }
            }
        }

        @Override
        public boolean match(Object[] testedArray) {
            if (0 == referenceLength) {
                return true;
            } else if (testedArray.length < referenceLength) {
                return false;
            }
            final int headLength = referenceLength - 1;
            int tailIdx = headLength;
            final Object tailItem = referenceSubarray[tailIdx];
            while (tailIdx < testedArray.length) {
                // Check tail item
                final Object item = testedArray[tailIdx];
                if (!Objects.equals(item, tailItem)) {
                    ++tailIdx;
                    continue;
                }
                // Tail item is matching, check the rest
                int toCheck = headLength;
                int arrayOffset = tailIdx;
                while (toCheck > 0) {
                    --arrayOffset;
                    if (!Objects.equals(testedArray[arrayOffset], referenceSubarray[toCheck - 1])) {
                        break;
                    }
                    --toCheck;
                }
                if ((0 == toCheck) && isMatchValid(arrayOffset, testedArray)) {
                    return true;
                }
                // Either no match or the match was vetoed, continue
                tailIdx += offsetShift;
            }
            return false;
        }

        protected boolean isMatchValid(int startOffset, Object[] array) {
            final boolean result;
            if (null != matchStartMatcher && !matchStartMatcher.matches(startOffset)) {
                result = false;
            } else if (null != matchEndMatcher) {
                final int trailingItems = array.length - startOffset - referenceLength;
                result = matchEndMatcher.matches(trailingItems);
            } else {
                result = true;
            }
            return result;
        }

        @Override
        public void describe(Description desc) {
            if (0 == referenceLength) {
                desc.appendText("any array");
                return;
            }
            desc.appendText("array that contains ");
            desc.appendValueList("[", ", ", "]", referenceSubarray);
            if (referenceLength > 3) {
                desc.appendText(" of size " + referenceLength);
            }
            String separator = "";
            if (null != matchStartMatcher) {
                desc.appendText(" with start offset ");
                matchStartMatcher.describeTo(desc);
                separator = " and";
            }
            if (null != matchEndMatcher) {
                desc.appendText(separator);
                desc.appendText(" with number of following items ");
                matchEndMatcher.describeTo(desc);
            }
        }

        @Override
        public void describeMismatch(Object[] testedArray, Description desc) {
            assert referenceLength > 0;
            desc.appendText("array ");
            desc.appendValueList("[", ", ", "]", testedArray);
            final int missingItems = referenceLength - testedArray.length;
            if (missingItems > 0) {
                desc.appendText(" has insufficient length (missing " + missingItems);
                if (1 == missingItems) {
                    desc.appendText(" item)");
                } else {
                    desc.appendText(" items)");
                }
                return;
            }
            final int headLength = referenceLength - 1;
            int tailIdx = headLength;
            final Object tailItem = referenceSubarray[tailIdx];
            int rawMatches = 0;
            while (tailIdx < testedArray.length) {
                // Check tail item
                final Object item = testedArray[tailIdx];
                if (!Objects.equals(item, tailItem)) {
                    ++tailIdx;
                    continue;
                }
                // Tail item is matching, check the rest
                int toCheck = headLength;
                int arrayOffset = tailIdx;
                while (toCheck > 0) {
                    --arrayOffset;
                    if (!Objects.equals(testedArray[arrayOffset], referenceSubarray[toCheck - 1])) {
                        break;
                    }
                    --toCheck;
                }
                if (0 == toCheck) {
                    assert !isMatchValid(arrayOffset, testedArray);
                    ++rawMatches;
                }
                // Either no match or the match was vetoed, continue
                tailIdx += offsetShift;
            }
            if (0 == rawMatches) {
                desc.appendText(" does not contain the reference sub-array");
            } else {
                if (1 == rawMatches) {
                    desc.appendText(" contains a match, but ");
                } else {
                    desc.appendText(" contains " + rawMatches + " matches, but ");
                }
                String separator = "";
                String finalClause = " is not satisfied";
                if (null != matchStartMatcher) {
                    if (null != matchEndMatcher) {
                        desc.appendText("neither ");
                        separator = "nor ";
                        finalClause = " is satisfied";
                    }
                    desc.appendText("start offset constraint (");
                    matchStartMatcher.describeTo(desc);
                    desc.appendText(")");
                }
                if (null != matchEndMatcher) {
                    desc.appendText(separator);
                    desc.appendText("constraint for following item count (");
                    matchEndMatcher.describeTo(desc);
                    desc.appendText(")");
                }
                desc.appendText(finalClause);
            }
        }
    }

}
