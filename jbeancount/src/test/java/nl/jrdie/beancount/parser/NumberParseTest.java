package nl.jrdie.beancount.parser;

import static nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer.NUMBER;
import static nl.jrdie.beancount.testing.TestUtil.assertToLanguage;
import static nl.jrdie.beancount.testing.TestUtil.t;

import java.math.BigDecimal;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class NumberParseTest {

  @ParameterizedTest
  @CsvSource(
      textBlock =
          """
          '1,,,,4.5',  '14.5'
          '1,2,3,4.5', '1234.5'
          '123,45.00', '12345.00'
          '123.00',    '123.00'
          '123.0',     '123.0'
          '123,00',    '12300'
          '123.',      '123'
          '123',       '123'
          '1,2.3',     '12.3'
          """)
  public void assertToLanguageNumberParses(String numberString, String targetValue) {
    assertToLanguage(antlr -> antlr.parseNumberToken(t(NUMBER, numberString)))
        .isEqualTo(new BigDecimal(targetValue));
  }
}
