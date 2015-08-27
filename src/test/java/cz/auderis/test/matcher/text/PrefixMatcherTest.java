package cz.auderis.test.matcher.text;

import cz.auderis.test.category.UnitTest;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static cz.auderis.test.matcher.text.TextMatchers.withCaseInsensitivePrefix;
import static cz.auderis.test.matcher.text.TextMatchers.withPrefix;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class PrefixMatcherTest {

	@Test
	@Category(UnitTest.class)
	public void shouldMatchEmptyPrefix() throws Exception {
		final String testText = "Abc123";
		//
		final Matcher<CharSequence> hasEmptyPrefix = withPrefix("");
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
		final Matcher<CharSequence> hasEmptyPrefix = withCaseInsensitivePrefix("");
		//
		for (int i=0; i<=testText.length(); ++i) {
			assertThat(testText.substring(0, i), hasEmptyPrefix);
		}
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchRegularPrefix() throws Exception {
		assertThat("Abc123", withPrefix("A"));
		assertThat("Abc123", withPrefix("Ab"));
		assertThat("Abc123", withPrefix("Abc"));
		assertThat("Abc123", withPrefix("Abc1"));
		assertThat("Abc123", withPrefix("Abc12"));
		assertThat("Abc123", withPrefix("Abc123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldMatchCaseInsensitiveRegularPrefix() throws Exception {
		assertThat("Abc123", withCaseInsensitivePrefix("a"));
		assertThat("Abc123", withCaseInsensitivePrefix("aB"));
		assertThat("Abc123", withCaseInsensitivePrefix("aBC"));
		assertThat("Abc123", withCaseInsensitivePrefix("aBC1"));
		assertThat("Abc123", withCaseInsensitivePrefix("aBC12"));
		assertThat("Abc123", withCaseInsensitivePrefix("aBC123"));
	}

	@Test
	@Category(UnitTest.class)
	public void shouldDetectCompleteMismatch() throws Exception {
		final Matcher<CharSequence> hasXyzPrefix = withPrefix("Xyz");
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
		final Matcher<CharSequence> hasXyzPrefix = withPrefix("Xyz");
		final Description description = new StringDescription();
		//
		assertThat("XyAbc123", not(hasXyzPrefix));
		hasXyzPrefix.describeMismatch("XyAbc123", description);
		//
		assertThat(description.toString(), is("was \"Xy<A>bc123\", with mismatch starting at position 2"));
	}

}
