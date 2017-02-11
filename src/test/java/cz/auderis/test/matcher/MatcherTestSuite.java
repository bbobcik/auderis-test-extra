package cz.auderis.test.matcher;

import cz.auderis.test.matcher.date.DateMatchersSuite;
import cz.auderis.test.matcher.multi.MultiPropertyMatcherTestSuite;
import cz.auderis.test.matcher.numeric.NumericMatchersSuite;
import cz.auderis.test.matcher.text.TextMatchersSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NumericMatchersSuite.class,
        TextMatchersSuite.class,
        DateMatchersSuite.class,
        MultiPropertyMatcherTestSuite.class
})
public class MatcherTestSuite {

	// Only a suite definition

}
