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
        if (null == expectedData) {
            throw new NullPointerException();
        } else if (0 == expectedData.length) {
            this.expectedData = expectedData;
        } else {
            this.expectedData = Arrays.copyOf(expectedData, expectedData.length);
        }
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
        if (0 == expectedData.length) {
            description.appendText("empty byte array");
        } else {
            description.appendText("byte array of size " + expectedData.length);
            description.appendText(" containing ");
            description.appendText(formatByteArray(expectedData, null).toString());
        }
    }

    @Override
    protected void describeMismatchSafely(byte[] bytes, Description out) {
        final int size = bytes.length;
        if (size != expectedData.length) {
            out.appendText("data size was " + size);
        } else {
            out.appendText("was ").appendText(formatByteArray(bytes, expectedData).toString());
        }
    }

    static StringBuilder formatByteArray(byte[] data, byte[] reference) {
        if ((null != reference) && (reference.length != data.length)) {
            reference = null;
        }
        final StringBuilder str = new StringBuilder(4 * data.length + 1);
        str.append('<');
        char separator = 0;
        for (int i=0; i<data.length; ++i) {
            if (0 == separator) {
                separator = ' ';
            } else {
                str.append(separator);
            }
            if ((null != reference) && (data[i] != reference[i])) {
                str.append('*');
            }
            final int byteValue = 0xFF & data[i];
            str.append(Character.toUpperCase(Character.forDigit(byteValue >>> 4, 16)));
            str.append(Character.toUpperCase(Character.forDigit(byteValue & 0x0F, 16)));
        }
        str.append('>');
        return str;
    }

}
