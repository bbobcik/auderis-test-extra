package cz.auderis.test.matcher.text;

import cz.auderis.test.category.UnitTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static cz.auderis.test.matcher.text.TextMatchers.hasCaseInsensitivePrefix;
import static cz.auderis.test.matcher.text.TextMatchers.hasPrefix;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PrefixMatcherTest {

	@Test
	@Category(UnitTest.class)
	public void shouldMatchEmptyPrefix() throws Exception {
		final String testText = "Abc123";
		//
		final Matcher<CharSequence> hasEmptyPrefix = hasPrefix("");
		//
		for (int i=0; i<=testText.length(); ++i) {
			assertThat(testText.substring(0, i), hasEmptyPrefix);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchEmptyCaseInsensitivePrefix() throws Exception {
		final String testText = "Abc123";
		//
		final Matcher<CharSequence> hasEmptyPrefix = hasCaseInsensitivePrefix("");
		//
		for (int i=0; i<=testText.length(); ++i) {
			assertThat(testText.substring(0, i), hasEmptyPrefix);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchRegularPrefix() throws Exception {
		assertThat("Abc123", hasPrefix("A"));
		assertThat("Abc123", hasPrefix("Ab"));
		assertThat("Abc123", hasPrefix("Abc"));
		assertThat("Abc123", hasPrefix("Abc1"));
		assertThat("Abc123", hasPrefix("Abc12"));
		assertThat("Abc123", hasPrefix("Abc123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchCaseInsensitiveRegularPrefix() throws Exception {
		assertThat("Abc123", hasCaseInsensitivePrefix("a"));
		assertThat("Abc123", hasCaseInsensitivePrefix("aB"));
		assertThat("Abc123", hasCaseInsensitivePrefix("aBC"));
		assertThat("Abc123", hasCaseInsensitivePrefix("aBC1"));
		assertThat("Abc123", hasCaseInsensitivePrefix("aBC12"));
		assertThat("Abc123", hasCaseInsensitivePrefix("aBC123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldDetectCompleteMismatch() throws Exception {
		final Matcher<CharSequence> hasXyzPrefix = hasPrefix("Xyz");
		final Description description = new StringDescription();
		//
		assertThat("Abc123", not(hasXyzPrefix));
		hasXyzPrefix.describeMismatch("Abc123", description);
		//
		assertThat(description.toString(), is("was \"Abc123\""));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldDetectPartialMismatch() throws Exception {
		final Matcher<CharSequence> hasXyzPrefix = hasPrefix("Xyz");
		final Description description = new StringDescription();
		//
		assertThat("XyAbc123", not(hasXyzPrefix));
		hasXyzPrefix.describeMismatch("XyAbc123", description);
		//
		assertThat(description.toString(), is("was \"Xy<A>bc123\", with mismatch starting at position 2"));
	}

}
