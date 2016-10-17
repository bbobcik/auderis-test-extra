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

package cz.auderis.test.matcher.date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Calendar;
import java.util.Date;

final class CalendarMatcherAdapter extends BaseMatcher<Calendar> {

    private final Matcher<Date> delegate;

    CalendarMatcherAdapter(Matcher<Date> delegate) {
        assert null != delegate;
        this.delegate = delegate;
    }

    @Override
    public boolean matches(Object obj) {
        if (obj instanceof Calendar) {
            obj = ((Calendar) obj).getTime();
        }
        return delegate.matches(obj);
    }

    @Override
    public void describeTo(Description description) {
	    delegate.describeTo(description);
    }

	@Override
	public void describeMismatch(Object obj, Description description) {
		if (obj instanceof Calendar) {
			obj = ((Calendar) obj).getTime();
		}
		delegate.describeMismatch(obj, description);
	}

}
