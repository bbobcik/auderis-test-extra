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

package cz.auderis.test.matcher.rawarray;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static cz.auderis.test.matcher.rawarray.IsByteArrayContainingMatcher.formatByteArray;

public class IsByteBufferContainingMatcher extends TypeSafeMatcher<ByteBuffer> {

    private final byte[] expectedData;

    public IsByteBufferContainingMatcher(byte[] expectedData) {
        super(ByteBuffer.class);
        if (null == expectedData) {
            throw new NullPointerException();
        } else if (0 == expectedData.length) {
            this.expectedData = expectedData;
        } else {
            this.expectedData = Arrays.copyOf(expectedData, expectedData.length);
        }
    }

    public IsByteBufferContainingMatcher(int... intValues) {
        super(ByteBuffer.class);
        expectedData = new byte[intValues.length];
        for (int i=0; i<intValues.length; ++i) {
            expectedData[i] = (byte) intValues[i];
        }
    }

    public IsByteBufferContainingMatcher(ByteBuffer reference) {
        super(ByteBuffer.class);
        if (null == reference) {
            throw new NullPointerException();
        }
        final ByteBuffer refWindow = reference.slice();
        expectedData = new byte[refWindow.remaining()];
        if (refWindow.hasRemaining()) {
            refWindow.get(expectedData);
        }
    }

    @Override
    protected boolean matchesSafely(ByteBuffer buffer) {
        if (expectedData.length != buffer.remaining()) {
            return false;
        }
        final ByteBuffer bufferView = buffer.asReadOnlyBuffer();
        for (final byte x : expectedData) {
            if (bufferView.get() != x) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        if (0 == expectedData.length) {
            description.appendText("empty byte buffer");
        } else {
            description.appendText("byte buffer with available " + expectedData.length + " bytes");
            description.appendText(" containing ");
            description.appendText(formatByteArray(expectedData, null).toString());
        }
    }

    @Override
    protected void describeMismatchSafely(ByteBuffer buffer, Description out) {
        final ByteBuffer bufferView = buffer.asReadOnlyBuffer();
        final int size = bufferView.remaining();
        if (size != expectedData.length) {
            out.appendText("buffer available data size was " + size);
        } else {
            final byte[] bufferBytes = new byte[size];
            bufferView.get(bufferBytes);
            out.appendText("was ").appendText(formatByteArray(bufferBytes, expectedData).toString());
        }
    }

}
