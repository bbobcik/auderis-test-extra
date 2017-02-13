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

package cz.auderis.test.matcher.multi;

import cz.auderis.test.support.DescriptionProvider;
import cz.auderis.test.support.MismatchDescriptionProvider;
import cz.auderis.test.support.NaturalDescriptionJoiner;
import org.hamcrest.Matcher;

final class PropMatcherEntry implements PropertyEntry {
    final Matcher<Object> matcher;
    final PropertyExtractor extractor;
    Object descPrefix;
    Object descSuffix;
    DescriptionProvider<Matcher<?>> valueDescriber;
    MismatchDescriptionProvider<Object> mismatchDescriber;

    PropMatcherEntry(Matcher<Object> matcher, PropertyExtractor extractor, Object descPrefix, Object descSuffix) {
        assert null != matcher;
        assert null != extractor;
        this.matcher = matcher;
        this.extractor = extractor;
        this.descPrefix = descPrefix;
        this.descSuffix = descSuffix;
    }

    @Override
    public PropertyEntry withPrefix(Object prefix) {
        this.descPrefix = prefix;
        return this;
    }

    @Override
    public PropertyEntry withSuffix(Object suffix) {
        this.descSuffix = suffix;
        return this;
    }

    @Override
    public PropertyEntry withMatcherDescriber(DescriptionProvider<Matcher<?>> describer) {
        this.valueDescriber = describer;
        return this;
    }

    @Override
    public PropertyEntry withMismatchDescriber(MismatchDescriptionProvider<?> describer) {
        this.mismatchDescriber = (MismatchDescriptionProvider<Object>) describer;
        return this;
    }

    boolean matches(Object obj) {
        final Object property = extractor.extract(obj);
        return matcher.matches(property);
    }

    void addToJoiner(NaturalDescriptionJoiner joiner) {
        joiner.add(descPrefix, matcher, descSuffix, valueDescriber);
    }

    void addMismatchToJoiner(Object obj, NaturalDescriptionJoiner joiner) {
        final Object property = extractor.extract(obj);
        if (!matcher.matches(property)) {
            joiner.addMismatch(descPrefix, matcher, property, descSuffix, mismatchDescriber);
        }
    }
}
