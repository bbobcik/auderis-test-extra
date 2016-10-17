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

package cz.auderis.test.parameter.annotation.impl;

import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class HexChunkParser {

    private static final byte[] EMPTY_ARRAY = new byte[0];
    private static final Pattern HEX_CHUNK_PATTERN = Pattern.compile("(?:0x([0-9A-F]++))|([0-9A-F]{2,}+|[1-9A-F]|0(?!x))(?:H)?", Pattern.CASE_INSENSITIVE);

    private final Matcher hexChunkMatcher;
    private final boolean useDirectBuffer;
    private ByteBuffer resultBuffer;

    HexChunkParser() {
        this(false);
    }

    HexChunkParser(boolean useDirectBuffer) {
        this.useDirectBuffer = useDirectBuffer;
        hexChunkMatcher = HEX_CHUNK_PATTERN.matcher("");
    }

    byte[] parseByteArray(Object dataSpec) {
        if (null == dataSpec) {
            return EMPTY_ARRAY;
        } else if (dataSpec instanceof byte[]) {
            return (byte[]) dataSpec;
        }
        hexChunkMatcher.reset(dataSpec.toString());
        final int totalBytes = countBytes();
        if (0 == totalBytes) {
            return EMPTY_ARRAY;
        }
        final byte[] result = new byte[totalBytes];
        resultBuffer = ByteBuffer.wrap(result);
        parseChunks();
        return result;
    }

    ByteBuffer parseBuffer(Object dataSpec) {
        if (null == dataSpec) {
            return newBuffer(0);
        } else if (dataSpec instanceof ByteBuffer) {
            final ByteBuffer paramBuffer = (ByteBuffer) dataSpec;
            final ByteBuffer result;
            if (useDirectBuffer == paramBuffer.isDirect()) {
                result = paramBuffer;
            } else {
                final ByteBuffer copySource = paramBuffer.asReadOnlyBuffer();
                result = newBuffer(copySource.capacity());
                copySource.clear();
                result.put(copySource);
                result.limit(paramBuffer.limit());
                result.position(paramBuffer.position());
            }
            return result;
        }
        hexChunkMatcher.reset(dataSpec.toString());
        final int totalBytes = countBytes();
        if (0 == totalBytes) {
            return newBuffer(0);
        }
        resultBuffer = newBuffer(totalBytes);
        parseChunks();
        resultBuffer.flip();
        return resultBuffer;
    }

    private int countBytes() {
        int totalBytes = 0;
        while (hexChunkMatcher.find()) {
            final String prefixedChunk = hexChunkMatcher.group(1);
            if (null != prefixedChunk) {
                totalBytes += (prefixedChunk.length() + 1) / 2;
            }
            final String suffixedChunk = hexChunkMatcher.group(2);
            if (null != suffixedChunk) {
                totalBytes += (suffixedChunk.length() + 1) / 2;
            }
        }
        return totalBytes;
    }

    private void parseChunks() {
        hexChunkMatcher.reset();
        while (hexChunkMatcher.find()) {
            final String prefixedChunk = hexChunkMatcher.group(1);
            appendChunk(prefixedChunk);
            final String suffixedChunk = hexChunkMatcher.group(2);
            appendChunk(suffixedChunk);
        }
    }

    private void appendChunk(String hexChunk) {
        if (null == hexChunk) {
            return;
        }
        final int chunkSize = hexChunk.length();
        boolean byteEnd;
        if (1 == (chunkSize & 1)) {
            // Odd number of digits, assume leading zero
            byteEnd = true;
        } else {
            byteEnd = false;
        }
        int prevDigit = 0;
        for (int pos = 0; pos < chunkSize; ++pos) {
            final char c = hexChunk.charAt(pos);
            final int hexDigit = Character.digit(c, 16);
            if (byteEnd) {
                final int byteValue = (prevDigit << 4) | hexDigit;
                resultBuffer.put((byte) byteValue);
            } else {
                prevDigit = hexDigit;
            }
            byteEnd = !byteEnd;
        }
    }

    private ByteBuffer newBuffer(int size) {
        if (useDirectBuffer) {
            return ByteBuffer.allocateDirect(size);
        }
        return ByteBuffer.allocate(size);
    }

}
