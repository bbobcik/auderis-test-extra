/*
 * Copyright 2015 Boleslav Bobcik - Auderis
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

package cz.auderis.test.parameter.convert;

import junitparams.converters.ConversionFailedException;

import java.math.BigInteger;

public class BigIntegerConverter extends AbstractTypeConverter<BigInteger> {

	public BigIntegerConverter() {
		super(BigInteger.class);
	}

	@Override
	protected BigInteger fromString(String objText, String option) throws ConversionFailedException {
		try {
			return new BigInteger(objText);
		} catch (NumberFormatException e) {
			throw new ConversionFailedException(e.getMessage());
		}
	}

	@Override
	protected BigInteger fromOtherType(Object obj, String option) throws ConversionFailedException {
		if ((obj instanceof Long) || (obj instanceof Integer) || (obj instanceof Short) || (obj instanceof Byte)) {
			return BigInteger.valueOf(((Number) obj).longValue());
		}
		return super.fromOtherType(obj, option);
	}

}
