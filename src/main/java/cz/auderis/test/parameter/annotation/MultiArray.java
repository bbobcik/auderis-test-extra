/*
 * Copyright 2016 Boleslav Bobcik - Auderis
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

import cz.auderis.test.parameter.annotation.impl.MultiArrayConverter;
import junitparams.converters.Param;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the appropriate argument will be interpreted as a specification of multidimensional array.
 * The specification consists of two parts, described by the following BNF grammar:
 * <pre>
 *     SPEC                ::= DIMENSIONS <u>:</u> FLATTENED_DATA
 *     DIMENSIONS          ::= NUMBER | NUMBER DIM_SEPARATOR DIMENSIONS
 *     DIM_SEPARATOR       ::= <u>x</u> | <u>X</u> | <u>*</u>
 *     FLATTENED_DATA      ::= ( TOKEN )*
 *     TOKEN               ::= SIMPLE_TOKEN | QUOTED_TOKEN | SINGLE_QUOTED_TOKEN
 *     SIMPLE_TOKEN        ::= [^\s'"]+
 *     QUOTED_TOKEN        ::= <u>"</u> ( [^"\\] | <u>\\</u> | <u>\"</u> )+ <u>"</u>
 *     SINGLE_QUOTED_TOKEN ::= <u>'</u> ( [^'\\] | <u>\\</u> | <u>\'</u> )+ <u>'</u>
 * </pre>
 * The following are valid examples of the array specification.
 * <pre>
 *     1 x 1 : hello
 *     2*2: 1 2 3 4
 *     3x1x2 : test1 "quoted token2" 'single-quoted token3' 4 "escape \" in token 5" 'escape \' in token 6'
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
@Param(converter = MultiArrayConverter.class)
public @interface MultiArray {

    Class<?> value();

}
