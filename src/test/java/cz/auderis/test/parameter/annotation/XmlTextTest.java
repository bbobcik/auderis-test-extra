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

import cz.auderis.test.parameter.annotation.impl.XmlTextAnnotationConverter;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.xml.transform.Source;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

@RunWith(JUnitParamsRunner.class)
public class XmlTextTest {

    @Test
    @Parameters({
            "<x>Text</xy>"
    })
    public void shouldConvertXml(String xmlSource) throws Exception {
        // Given
        XmlTextAnnotationConverter converter = new XmlTextAnnotationConverter();

        // When
        final Source resultSource = converter.convert(xmlSource);

        // Then
        assertThat(resultSource, Matchers.is(notNullValue(Source.class)));
    }


}
