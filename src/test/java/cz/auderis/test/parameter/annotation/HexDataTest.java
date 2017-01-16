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

package cz.auderis.test.parameter.annotation;

import cz.auderis.test.parameter.annotation.impl.HexArrayAnnotationConverter;
import cz.auderis.test.parameter.annotation.impl.HexBufferAnnotationConverter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.ByteBuffer;

import static cz.auderis.test.matcher.rawarray.RawArrayMatchers.byteArrayContaining;
import static cz.auderis.test.matcher.rawarray.RawArrayMatchers.hasSameContentsAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(JUnitParamsRunner.class)
public class HexDataTest {

    @Test
    @Parameters({
            "123 4567 89ABC       | 01 23 45 67 08 9A BC",
            "0x123 0x4567 0x89ABC | 01 23 45 67 08 9A BC",
            "123h 4567h 89ABCh    | 01 23 45 67 08 9A BC",
            "0x0000 0xFFFF 0x0000 | 00 00 FF FF 00 00",
            "ca-fe-ba-be          | CA FE BA BE",
            "0b:ad:be:ef          | 0B AD BE EF",
            "... 123 ...          | 01 23",
            "{ first part 03 } 12:34:56 { second part 99 88 } FE:DC:BA | 12 34 56 FE DC BA",
    })
    public void shouldConvertTextToByteArray(String byteDef, @HexArray byte[] expectedBytes) throws Exception {
        // Given
        final HexArrayAnnotationConverter converter = new HexArrayAnnotationConverter();

        // When
        final byte[] bytes = converter.convert(byteDef);

        // Then
        assertThat(bytes, is(byteArrayContaining(expectedBytes)));
    }

    @Test
    @Parameters({
            "123 4567 89ABC       | 01 23 45 67",
            "0x123 0x4567 0x89ABC | 01 23 45 67",
            "123h 4567h 89ABCh    | 01 23 45 67",
            "0x0000 0xFFFF 0x0000 | 00 00 FF FF",
            "ca-fe-ba-be          | CA FE BA BE",
            "0b:ad:be             | 0B AD BE 00",
            "... 123 ...          | 01 23 00 00",
            "{ first part 03 } 12:34:56 { second part 99 88 } FE:DC:BA | 12 34 56 FE",
    })
    public void shouldConvertTextToByteArrayWithFixedSize(@HexArray(size = 4) byte[] bytes, @HexArray byte[] expectedBytes) throws Exception {
        assertThat(bytes, is(byteArrayContaining(expectedBytes)));
    }

    @Test
    @Parameters({
            "123 4567 89ABC       | 01 23 45 67 08 9A BC",
            "0x123 0x4567 0x89ABC | 01 23 45 67 08 9A BC",
            "123h 4567h 89ABCh    | 01 23 45 67 08 9A BC",
            "0x0000 0xFFFF 0x0000 | 00 00 FF FF 00 00",
            "ca-fe-ba-be          | CA FE BA BE",
            "0b:ad:be:ef          | 0B AD BE EF",
            "... 123 ...          | 01 23",
            "{ first part 03 } 12:34:56 { second part 99 88 } FE:DC:BA | 12 34 56 FE DC BA",
    })
    public void shouldConvertTextToBuffer(String byteDef, @HexBuffer ByteBuffer expectedBuffer) throws Exception {
        // Given
        final HexBufferAnnotationConverter converter = new HexBufferAnnotationConverter();

        // When
        final ByteBuffer buffer = converter.convert(byteDef);

        // Then
        assertThat(buffer, hasSameContentsAs(expectedBuffer));
    }

    @Test
    @Parameters({
            "123 4567 89ABC       | 01 23 45 67",
            "0x123 0x4567 0x89ABC | 01 23 45 67",
            "123h 4567h 89ABCh    | 01 23 45 67",
            "0x0000 0xFFFF 0x0000 | 00 00 FF FF",
            "ca-fe-ba-be          | CA FE BA BE",
            "0b:ad:be             | 0B AD BE 00",
            "... 123 ...          | 01 23 00 00",
            "{ first part 03 } 12:34:56 { second part 99 88 } FE:DC:BA | 12 34 56 FE",
    })
    public void shouldConvertTextToBufferWithFixedCapacity(@HexBuffer(capacity = 4) ByteBuffer buffer, @HexBuffer ByteBuffer expectedBuffer) throws Exception {
        assertThat(buffer, hasSameContentsAs(expectedBuffer));
    }

}
