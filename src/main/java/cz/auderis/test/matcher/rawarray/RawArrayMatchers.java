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

import org.hamcrest.Factory;
import org.hamcrest.Matcher;

import java.nio.ByteBuffer;

public final class RawArrayMatchers {

    @Factory
    public static <T> Matcher<byte[]>  byteArrayContaining(byte... bytes) {
        return new IsByteArrayContainingMatcher(bytes);
    }

    @Factory
    public static <T> Matcher<byte[]>  byteArrayContaining(int... byteValues) {
        return new IsByteArrayContainingMatcher(byteValues);
    }

    @Factory
    public static <T> Matcher<ByteBuffer> containsBytes(byte[] bytes) {
        return new IsByteBufferContainingMatcher(bytes);
    }

    @Factory
    public static <T> Matcher<ByteBuffer> containsBytes(int... byteValues) {
        return new IsByteBufferContainingMatcher(byteValues);
    }

    @Factory
    public static <T> Matcher<ByteBuffer> hasSameContentsAs(ByteBuffer reference) {
        return new IsByteBufferContainingMatcher(reference);
    }

    private RawArrayMatchers() {
        throw new AssertionError();
    }

}
