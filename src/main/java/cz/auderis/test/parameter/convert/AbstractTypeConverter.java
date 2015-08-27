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
import junitparams.converters.ParamConverter;

public abstract class AbstractTypeConverter<T> implements ParamConverter<T> {

	protected final Class<T> targetClass;

	protected AbstractTypeConverter(Class<T> targetClass) {
		assert null != targetClass;
		this.targetClass = targetClass;
	}

	@Override
	public T convert(Object obj, String option) throws ConversionFailedException {
		if (null == obj) {
			return nullValue(option);
		} else if (targetClass.isInstance(obj)) {
			return targetClass.cast(obj);
		} else if (obj instanceof String) {
			return fromString((String) obj, option);
		}
		return fromOtherType(obj, option);
	}

	protected abstract T fromString(String objText, String option) throws ConversionFailedException;

	protected T nullValue(String option) throws ConversionFailedException {
		return null;
	}

	protected T fromOtherType(Object obj, String option) throws ConversionFailedException {
		final String objType = obj.getClass().getName();
		final String converterType = getClass().getSimpleName();
		throw new ConversionFailedException("Type " + objType + " not supported by " + converterType);
	}

}
