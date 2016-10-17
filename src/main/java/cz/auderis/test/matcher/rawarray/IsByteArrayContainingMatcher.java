/*
 * Copyright 2015-2016 Boleslav Bobcik - Auderis
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

package cz.auderis.test.matcher.rawarray;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

public class IsByteArrayContainingMatcher extends TypeSafeMatcher<byte[]> {

    private final byte[] expectedData;

    public IsByteArrayContainingMatcher(byte[] expectedData) {
        super(byte[].class);
        this.expectedData = expectedData;
    }

    public IsByteArrayContainingMatcher(int... intValues) {
        super(byte[].class);
        expectedData = new byte[intValues.length];
        for (int i=0; i<intValues.length; ++i) {
            expectedData[i] = (byte) intValues[i];
        }
    }

    @Override
    protected boolean matchesSafely(byte[] bytes) {
        return Arrays.equals(bytes, expectedData);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("byte array of size " + expectedData.length);
        if (0 == expectedData.length) {
            return;
        }
        description.appendText(" containing ");
        description.appendText(formatByteArray(expectedData).toString());
    }

    @Override
    protected void describeMismatchSafely(byte[] bytes, Description out) {
        final int size = bytes.length;
        if (size != expectedData.length) {
            out.appendText("data size was " + size);
        } else {
            out.appendText("was ").appendText(formatByteArray(bytes).toString());
        }
    }

    static StringBuilder formatByteArray(byte[] data) {
        final StringBuilder str = new StringBuilder(3 * data.length + 1);
        str.append('<');
        char separator = 0;
        for (final byte x : data) {
            if (0 == separator) {
                separator = ':';
            } else {
                str.append(separator);
            }
            final int byteValue = 0xFF & x;
            str.append(Character.forDigit(byteValue >>> 4, 16));
            str.append(Character.forDigit(byteValue & 0x0F, 16));
        }
        str.append('>');
        return str;
    }

}
