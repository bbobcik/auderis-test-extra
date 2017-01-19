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

package cz.auderis.test.support.array;

import java.io.IOException;
import java.nio.ByteBuffer;

public class ByteSequenceFormatSupport {

    private static final int BYTE_MASK = (1 << Byte.SIZE) - 1;
    private static final int NIBBLE_SIZE = Byte.SIZE / 2;
    private static final int NIBBLE_MASK = (1 << NIBBLE_SIZE) - 1;

    private String startDelimiter;
    private String endDelimiter;
    private String separator;
    private String majorSeparator;
    private int majorGroupBytes;
    private boolean uppercaseDigits;

    public static String defaultFormat(byte[] bytes) {
        final ByteSequenceFormatSupport formatter = new ByteSequenceFormatSupport();
        return formatter.format(bytes);
    }

    public static String defaultFormat(ByteBuffer bytes) {
        final ByteSequenceFormatSupport formatter = new ByteSequenceFormatSupport();
        return formatter.format(bytes);
    }

    public static String format4(byte[] bytes) {
        return defaultGroupFormatter(4).format(bytes);
    }

    public static String format4(ByteBuffer bytes) {
        return defaultGroupFormatter(4).format(bytes);
    }

    public static String formatGroups(byte[] bytes, int groupSize) {
        return defaultGroupFormatter(groupSize).format(bytes);
    }

    public static String formatGroups(ByteBuffer bytes, int groupSize) {
        return defaultGroupFormatter(groupSize).format(bytes);
    }

    public static int defaultFormatTo(byte[] bytes, Appendable output) {
        final ByteSequenceFormatSupport formatter = new ByteSequenceFormatSupport();
        return formatter.appendSafe(bytes, output);
    }

    public static int defaultFormatTo(ByteBuffer bytes, Appendable output) {
        final ByteSequenceFormatSupport formatter = new ByteSequenceFormatSupport();
        return formatter.appendSafe(bytes, output);
    }

    public static int format4To(byte[] bytes, Appendable output) {
        return defaultGroupFormatter(4).appendSafe(bytes, output);
    }

    public static int format4To(ByteBuffer bytes, Appendable output) {
        return defaultGroupFormatter(4).appendSafe(bytes, output);
    }

    public static int formatGroupsTo(byte[] bytes, Appendable output, int groupSize) {
        return defaultGroupFormatter(groupSize).appendSafe(bytes, output);
    }

    public static int formatGroupsTo(ByteBuffer bytes, Appendable output, int groupSize) {
        return defaultGroupFormatter(groupSize).appendSafe(bytes, output);
    }

    public ByteSequenceFormatSupport() {
        this("<", ">", " ");
    }

    public ByteSequenceFormatSupport(String startDelimiter, String endDelimiter, String separator) {
        this.startDelimiter = startDelimiter;
        this.endDelimiter = endDelimiter;
        this.separator = separator;
        this.uppercaseDigits = true;
        this.majorGroupBytes = Integer.MAX_VALUE;
        this.majorSeparator = null;
    }

    public String format(byte[] bytes) {
        try {
            final int expectedSize = getExpectedOutputLength((null != bytes) ? bytes.length : -1);
            final StringBuilder str = new StringBuilder(expectedSize);
            final int[] count = { 0 };
            appendInternal(bytes, str, count);
            return str.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public String format(ByteBuffer data) {
        try {
            final int expectedSize = getExpectedOutputLength((null != data) ? data.remaining() : -1);
            final StringBuilder str = new StringBuilder(expectedSize);
            final int[] count = { 0 };
            final ByteBuffer dataView = (null != data) ? data.asReadOnlyBuffer() : null;
            appendInternal(dataView, str, count);
            return str.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public int appendSafe(byte[] bytes, Appendable output) {
        final int[] count = { 0 };
        try {
            final Appendable safeOutput = null != output ? output : SinkAppendable.INSTANCE;
            appendInternal(bytes, safeOutput, count);
        } catch (IOException e) {
            // Exception consumed (i.e. transformed to an error code)
            count[0] = -1 - count[0];
        }
        return count[0];
    }

    public int appendSafe(ByteBuffer data, Appendable output) {
        final int[] count = { 0 };
        try {
            final ByteBuffer dataView = (null != data) ? data.asReadOnlyBuffer() : null;
            final Appendable safeOutput = null != output ? output : SinkAppendable.INSTANCE;
            appendInternal(dataView, safeOutput, count);
        } catch (IOException e) {
            // Exception consumed (i.e. transformed to an error code)
            count[0] = -1 - count[0];
        }
        return count[0];
    }

    public int append(byte[] bytes, Appendable output) throws IOException {
        final int[] count = { 0 };
        appendInternal(bytes, null != output ? output : SinkAppendable.INSTANCE, count);
        return count[0];
    }

    public int append(ByteBuffer data, Appendable output) throws IOException {
        final int[] count = { 0 };
        final ByteBuffer dataView = (null != data) ? data.asReadOnlyBuffer() : null;
        final Appendable safeOutput = null != output ? output : SinkAppendable.INSTANCE;
        appendInternal(dataView, safeOutput, count);
        return count[0];
    }

    private void appendInternal(byte[] bytes, Appendable output, int[] count) throws IOException {
        final ByteBuffer sourceBuffer;
        if (null != bytes) {
            sourceBuffer = ByteBuffer.wrap(bytes);
        } else {
            sourceBuffer = null;
        }
        appendInternal(sourceBuffer, output, count);
    }

    protected void appendInternal(ByteBuffer source, Appendable output, int[] count) throws IOException {
        assert null != output;
        assert null != count;
        if (null == source) {
            final String nullText = String.valueOf(null);
            output.append(nullText);
            count[0] = nullText.length();
            return;
        }
        count[0] = appendDelimiter(output, startDelimiter);
        String sep = null;
        String majorSep = null;
        final int sepLength = (null != separator) ? separator.length() : 0;
        final boolean hasMajorSeparator = ((null != majorSeparator) && (majorGroupBytes > 0));
        final int majorSepLength = hasMajorSeparator ? majorSeparator.length() : 0;
        int groupRemaining = 0;
        while (source.hasRemaining()) {
            if (hasMajorSeparator && (0 == groupRemaining)) {
                // Time for major separator
                if (null != majorSep) {
                    output.append(majorSep);
                    count[0] += majorSepLength;
                } else {
                    majorSep = majorSeparator;
                }
                groupRemaining = majorGroupBytes;
            } else if (null != sep) {
                output.append(sep);
                count[0] += sepLength;
            } else {
                sep = separator;
            }
            final int byteValue = BYTE_MASK & source.get();
            char hiNibble = Character.forDigit(NIBBLE_MASK & (byteValue >>> NIBBLE_SIZE), 16);
            char loNibble = Character.forDigit(NIBBLE_MASK & byteValue, 16);
            if (uppercaseDigits) {
                hiNibble = Character.toUpperCase(hiNibble);
                loNibble = Character.toUpperCase(loNibble);
            } else {
                hiNibble = Character.toLowerCase(hiNibble);
                loNibble = Character.toLowerCase(loNibble);
            }
            output.append(hiNibble);
            ++count[0];
            output.append(loNibble);
            ++count[0];
            --groupRemaining;
        }
        count[0] += appendDelimiter(output, endDelimiter);
    }

    protected int getExpectedOutputLength(int byteCount) {
        if (byteCount < 0) {
            return String.valueOf(null).length();
        }
        int size = 2 * byteCount;
        if (null != startDelimiter) {
            size += startDelimiter.length();
        }
        if (null != endDelimiter) {
            size += endDelimiter.length();
        }
        if (byteCount > 0) {
            final int minorSepLength = (null != separator) ? separator.length() : 0;
            size += (byteCount - 1) * minorSepLength;
            if ((null != majorSeparator) && (majorGroupBytes > 0) && (majorGroupBytes < byteCount)) {
                final int sepLengthDiff = majorSeparator.length() - minorSepLength;
                final int groups = (byteCount + majorGroupBytes - 1) / majorGroupBytes;
                size += (groups - 1) * sepLengthDiff;
            }
        }
        return size;
    }

    public String getStartDelimiter() {
        return startDelimiter;
    }

    public void setStartDelimiter(String startDelimiter) {
        this.startDelimiter = startDelimiter;
    }

    public String getEndDelimiter() {
        return endDelimiter;
    }

    public void setEndDelimiter(String endDelimiter) {
        this.endDelimiter = endDelimiter;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public String getMajorSeparator() {
        return majorSeparator;
    }

    public void setMajorSeparator(String majorSeparator) {
        this.majorSeparator = majorSeparator;
    }

    public int getMajorGroupBytes() {
        return majorGroupBytes;
    }

    public void setMajorGroupBytes(int majorGroupBytes) {
        this.majorGroupBytes = majorGroupBytes;
    }

    public boolean isUppercaseDigits() {
        return uppercaseDigits;
    }

    public void setUppercaseDigits(boolean uppercaseDigits) {
        this.uppercaseDigits = uppercaseDigits;
    }

    private static int appendDelimiter(Appendable output, String delimiter) throws IOException {
        int chars = 0;
        if ((null != delimiter) && !delimiter.isEmpty()) {
            output.append(delimiter);
            chars += delimiter.length();
        }
        return chars;
    }

    private static ByteSequenceFormatSupport defaultGroupFormatter(int groupSize) {
        final ByteSequenceFormatSupport formatter = new ByteSequenceFormatSupport();
        formatter.setSeparator(":");
        formatter.setMajorSeparator(" ");
        formatter.setMajorGroupBytes(groupSize);
        return formatter;
    }

}
