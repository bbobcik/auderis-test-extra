package cz.auderis.test.matcher.text;

import cz.auderis.test.category.UnitTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static cz.auderis.test.matcher.text.TextMatchers.hasCaseInsensitivePrefix;
import static cz.auderis.test.matcher.text.TextMatchers.hasCaseInsensitiveSuffix;
import static cz.auderis.test.matcher.text.TextMatchers.hasPrefix;
import static cz.auderis.test.matcher.text.TextMatchers.hasSuffix;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class SuffixMatcherTest {

	@Test
	@Category(UnitTest.class)
	public void shouldMatchEmptySuffix() throws Exception {
		final String testText = "Abc123";
		//
		final Matcher<CharSequence> hasEmptySuffix = hasSuffix("");
		//
		for (int i=0; i<=testText.length(); ++i) {
			assertThat(testText.substring(0, i), hasEmptySuffix);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchEmptyCaseInsensitiveSuffix() throws Exception {
		final String testText = "Abc123";
		//
		final Matcher<CharSequence> hasEmptySuffix = hasCaseInsensitiveSuffix("");
		//
		for (int i=0; i<=testText.length(); ++i) {
			assertThat(testText.substring(0, i), hasEmptySuffix);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchRegularSuffix() throws Exception {
		assertThat("Abc123", hasSuffix("3"));
		assertThat("Abc123", hasSuffix("23"));
		assertThat("Abc123", hasSuffix("123"));
		assertThat("Abc123", hasSuffix("c123"));
		assertThat("Abc123", hasSuffix("bc123"));
		assertThat("Abc123", hasSuffix("Abc123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchCaseInsensitiveRegularSuffix() throws Exception {
		assertThat("Abc123", hasCaseInsensitiveSuffix("3"));
		assertThat("Abc123", hasCaseInsensitiveSuffix("23"));
		assertThat("Abc123", hasCaseInsensitiveSuffix("123"));
		assertThat("Abc123", hasCaseInsensitiveSuffix("C123"));
		assertThat("Abc123", hasCaseInsensitiveSuffix("BC123"));
		assertThat("Abc123", hasCaseInsensitiveSuffix("aBC123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldDetectCompleteMismatch() throws Exception {
		final Matcher<CharSequence> hasXyzSuffix = hasSuffix("Xyz");
		final Description description = new StringDescription();
		//
		assertThat("Abc123", not(hasXyzSuffix));
		hasXyzSuffix.describeMismatch("Abc123", description);
		//
		assertThat(description.toString(), is("was \"Abc123\""));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldDetectPartialMismatch() throws Exception {
		final Matcher<CharSequence> hasXyzSuffix = hasSuffix("Xyz");
		final Description description = new StringDescription();
		//
		assertThat("Abc123yz", not(hasXyzSuffix));
		hasXyzSuffix.describeMismatch("Abc123yz", description);
		//
		assertThat(description.toString(), is("was \"Abc12<3>yz\", with last 2 characters matching"));
	}


}
