package cz.auderis.test.matcher.numeric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
		InRangeMatcherTest.class,
		InBigRangeMatcherTest.class,
		BigDecPropertyMatcherTest.class,
        BigDecRoundingMatcherTest.class
})
public class NumericMatchersSuite {

	// Only a suite definition

}
