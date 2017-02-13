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
import org.hamcrest.Matcher;

enum DummyPropertyEntry implements PropertyEntry {
    INSTANCE {
        @Override
        public PropertyEntry withPrefix(Object prefix) {
            return this;
        }

        @Override
        public PropertyEntry withSuffix(Object suffix) {
            return this;
        }

        @Override
        public PropertyEntry withMatcherDescriber(DescriptionProvider<Matcher<?>> describer) {
            return this;
        }

        @Override
        public PropertyEntry withMismatchDescriber(MismatchDescriptionProvider<?> describer) {
            return this;
        }
    }
}
