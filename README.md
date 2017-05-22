# auderis-test-extra &mdash; Comfortable unit testing

Useful additions to JUnit/Hamcrest/JUnitParams testing environment. You will reduce most of unit test boilerplate
significantly:
* Test cases can receive parameters of various types, as complex as needed.
* Test assertions will be almost perfectly human-readable, providing natural constraints for tested objects
* Creation of additional object matchers is simplified by `MultiPropertyMatcher`

As a result, many test cases shrink to a couple of lines of code (excluding parameter definition). Following
the [Given-When-Then](https://martinfowler.com/bliki/GivenWhenThen.html) (also known as "Arrange-Act-Assert")
test style, you'll notice that *Given* section will be often empty and each of sections *When*, *Then* may
consist of a single line of code. 

Keywords: `Unit tests`, `Hamcrest`, `JUnitParams`

[![Build Status](https://travis-ci.org/bbobcik/auderis-test-extra.svg?branch=master)](https://travis-ci.org/bbobcik/auderis-test-extra)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/cz.auderis/auderis-test-extra/badge.svg)](https://maven-badges.herokuapp.com/maven-central/cz.auderis/auderis-test-extra)


## Changelog

### 1.3.3
* Extracted `AbstractKeyValueConverter` that does not implement `Converter` interface
  so that class signatures do not clash

### 1.3.2
* Parameter converter `KeyValueBeanAnnotationConverter` can become a base for specialized
  annotation converters

### 1.3.1
* Parameter annotation `@KeyValueBean` facilitates creation and initialization of simple beans.
  The parameter text should be a whitespace-separated list of `key=value` pairs; the keys are then
  interpreted either as property names (in JavaBean sense) or as field names.
* `@KeyValueBean` supports so-called property delegates, where setters are looked-up in a different
  class. These "delegate setters" require two arguments &ndash; a target bean reference and the actual property
  value. The delegate setters can be static.
* To facilitate special conversion needs when using a `@KeyValueBean` delegate setter, the property value
  argument can be a `String` instance &ndash; in this case the delegate setter is responsible for
  appropriate parsing and conversion.
* Property editors for `BigDecimal` and `BigInteger` added (registered either directly or via
  `AdditionalPropertyEditors.register()`)

### 1.3.0
* `ArrayPartMatcher` implemented

### 1.2.9
* (Bug) Incorrect handling of parameter annotation `@BigInt` fixed 

### 1.2.8
* File handling interface `WorkFolderInterface` provided for `WorkFolder` as well as for
  its subfolders.
* `FileMatchers` can match file attributes, using a new matcher `FileFlagMatcher`

### 1.2.7
* Functionality of `MultiPropertyMatcher` improved to simplify an instance construction

### 1.2.6
* To simplify creation of dedicated multi-property matchers that tend to follow a common pattern,
  `MultiPropertyMatcher` class was added
* Natural list joining facility `NaturalDescriptionJoiner` added support for functional
  description generation; in Java 8, lambda construction can be efficiently used.

### 1.2.5
* Natural list joining facility `NaturalDescriptionJoiner` has better support for
  Hamcrest-related activities: descriptions of matchers' mismatches can be added to the
  result list (items where the matcher is either `null` or matches the provided value are
  omitted from the result).

### 1.2.4
* Parameter annotation `@HexBuffer` can optionally specify desired buffer capacity as well as whether
  to skip the rewinding of the produced `ByteBuffer`
* Parameter annotation `@HexArray` can optionally specify desired size of produced byte array
* New matchers targetting `ByteBuffer`s were added
* Parameter annotations `@BigDec` and `@BigInt` can produce `null` values from the text source,
  using appropriate annotation argument 
* `ByteSequenceFormatSupport` allows to format byte sequences into a readable form, typically
  into hexadecimal chunks
* Added utility class `NaturalDescriptionJoiner` that facilitates construction of natural language item lists 

### 1.2.3
* Support for JUnitParams "metaconversion"
  * Annotation `@UsingPropertyEditor` will delegate parameter conversion to an appropriate
    JavaBeans `PropertyEditor` implementation
  * A factory method can be used for String-to-Target conversion using annotation
    `@UsingFactory`

### 1.2.2
* Build platform switched from Maven to Gradle
* Logging output testing extended
  * Added support for [Log4J 1.2.x](http://logging.apache.org/log4j/1.2)
  * Multiple logging frameworks can be captured at the same time (e.g. using `LogFramework.everything()`)
  * When requested logging framework is not on classpath, its capturing is disabled

### 1.2.1
* Support for tests of multidimensional arrays
  * `ArrayDimensionMatcher` with matcher factory methods for checking array dimensions
  * JUnitParams annotations `@MultiArray` and `@MultiArrayInt` facilitating easy generation of multidimensional
    arrays as unit test's arguments. `@MultiArray` can work with arbitrary array item types, including
    all primitive types and with support for selected built-in types (e.g. `java.math.BigDecimal`).

### 1.2.0
* Tools for testing logging output
  * Normal output of a logging framework is redirected to memory buffer, preventing disk/network activity outside of
    the scope of unit tests 
  * Frameworks currently supported: [SLF4J](http://www.slf4j.org) and [JBoss Logging](https://github.com/jboss-logging/jboss-logging)
  * Logging framework intercepted by applying class rule `@LogFramework` in a test class
  * Test rule `@LogBuffer` facilitates filtering and collection of log messages stored as `cz.auderis.test.logging.LogRecord` instances
  * Hamcrest matchers for `LogRecord` allow detailed inspection of captured log output

### 1.1.0
* Support for [JUnitParams 1.0.5](https://github.com/Pragmatists/JUnitParams) annotation-based converters
  * `@BigDec` for `java.math.BigDecimal` objects
  * `@BigInt` for `java.math.BigInteger` objects
  * `@DateParam` and `@CalendarParam` for legacy temporal classes `java.util.Date` and `java.util.Calendar`
  * `@HexArray` and `@HexBuffer` for arbitrary byte sequences, producing `byte[]` and `java.nio.ByteBuffer` objects, respectively
  * `@XmlText` wraps text into `javax.xml.transform.Source` for further XML processing
  * Utility converter `@Guid` for UUID objects
* Additional Hamcrest matchers for text, date and raw array validation
* Hamcrest matchers for `java.math.BigDecimal`
  * Matchers for numeric properties (scale, precision)
  * Matchers working with rounded values

### 1.0.1
* Added various type converters for [JUnitParams](https://github.com/Pragmatists/JUnitParams) library.



## Usage examples

### Testing log output - SLF4J

```java
    public class LoggingTest {
        @ClassRule
        public static LogFramework logFramework = LogFramework.slf4j();

        @Rule
        public LogBuffer logBuffer = new LogBuffer();

        @Test
        public void testOfLogging() throws Exception {
            // GIVEN
            logBuffer.levels().enableOnly( LogLevel.INFO.plusHigherLevels() );
            // WHEN
            Logger log = LoggerFactory.getLogger("x");
            log.info( "This is a log" );
            // THEN
            final List<LogRecord> infoRecords = logBuffer.getRecords();
            assertThat( infoRecords, hasSize(1) );
            final LogRecord logRecord = infoRecords.get(0);
            assertThat( logRecord, hasLevel(LogLevel.INFO) );
            assertThat( logRecord, hasMessage("This is a log") );
        }
    }
```

### Multidimensional arrays

The following tests demonstrate:
* Conversion of flat parameter lists into multidimensional arrays (see `@MultiArray` JavaDoc for details)
* Usage of specialized Hamcrest matcher that checks multidimensional array dimensions

```java
    @RunWith(JUnitParamsRunner.class)
    public class MultiDimTest {
            
        @Test
        @Parameters({
            "2x2x2 : 1 2 -3 -4 -5 -6 7 8 | 2 * 2 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
        })
        public void testPrimitiveIntArray(@MultiArray(int.class) Object intArray, String expectedDimension) throws Exception {
            assertThat( intArray, is( arrayWithDimensions( expectedDimension )));
        }
        
        @Test
        @Parameters({
            "2x1x2 : -5.009 -6.999 7.432 8.977131 | 2 * 1 * 2",
            "3 : 9 8 7                   | 3",
            "4x0 :                       | 4 X 0"
        })
        public void testBigDecimalArray(@MultiArray(BigDecimal.class) Object bigDecArray, String expectedDimension) throws Exception {
            assertThat( bigDecArray, is( arrayWithDimensions( expectedDimension )));
        }
    }
```
