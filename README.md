# auderis-test-extra

Useful additions to JUnit/Hamcrest testing environment.



## Changelog

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
