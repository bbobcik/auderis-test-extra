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

package cz.auderis.test.rule;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public final class ContentProviders {

    private static final InitialContentsProvider EMPTY_PROVIDER = new EmptyProvider();

    public static InitialContentsProvider lines(CharSequence... lines) {
        return lines(Arrays.asList(lines));
    }

    public static InitialContentsProvider lines(Iterable<CharSequence> lines) {
        int size = 0;
        for (final CharSequence line : lines) {
            size += (null != line ? line.length() + 1 : 0);
        }
        if (0 == size) {
            return EMPTY_PROVIDER;
        }
        final StringBuilder contents = new StringBuilder(size);
        for (final CharSequence line : lines) {
            if (null != line) {
                contents.append(line);
                contents.append('\n');
            }
        }
        final byte[] contentsData = contents.toString().getBytes(StandardCharsets.UTF_8);
        return new ByteArrayProvider(contentsData);
    }

    public static InitialContentsProvider bytes(int... byteData) {
        final int length = byteData.length;
        if (0 == length) {
            return EMPTY_PROVIDER;
        }
        final byte[] data = new byte[length];
        for (int i = 0; i< length; ++i) {
            data[i] = (byte) (0xFF & byteData[i]);
        }
        return new ByteArrayProvider(data);
    }

    public static InitialContentsProvider serializedForm(Object object) {
        if (null == object) {
            return EMPTY_PROVIDER;
        } else if (!(object instanceof Serializable)) {
            throw new IllegalArgumentException("Object is not a serializable type: " + object.getClass());
        }
        final InitialContentsProvider provider;
        try (
            final ByteArrayOutputStream objectData = new ByteArrayOutputStream(1024);
            final ObjectOutputStream objectStream = new ObjectOutputStream(objectData)
        ) {
            objectStream.writeObject(object);
            objectStream.flush();
            provider = new ByteArrayProvider(objectData.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Cannot serialize object of type " + object.getClass(), e);
        }
        return provider;
    }

    private ContentProviders() {
        throw new AssertionError();
    }

    private static final class EmptyProvider extends InputStream implements InitialContentsProvider {
        @Override
        public InputStream getContents() {
            return this;
        }

        @Override
        public int read() throws IOException {
            return -1;
        }
    }

    private static final class ByteArrayProvider implements InitialContentsProvider {
        private final byte[] data;

        private ByteArrayProvider(byte[] data) {
            this.data = data;
        }

        @Override
        public InputStream getContents() {
            final ByteArrayInputStream stream = new ByteArrayInputStream(data);
            return stream;
        }
    }

}
