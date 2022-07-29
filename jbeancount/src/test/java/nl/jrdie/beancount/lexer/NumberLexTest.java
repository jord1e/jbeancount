package nl.jrdie.beancount.lexer;

import static nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer.COMMA;
import static nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer.NUMBER;
import static nl.jrdie.beancount.testing.TestUtil.assertLexedTokensEquals;
import static nl.jrdie.beancount.testing.TestUtil.et;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class NumberLexTest {

  @Test
  public void wholeNumber() {
    assertLexedTokensEquals("100", et(NUMBER, "100", 1, 0));
  }

  @Test
  public void decimalNumber() {
    assertLexedTokensEquals("4.123", et(NUMBER, "4.123", 1, 0));
  }

  @Test
  public void decimalWithoutContinuation() {
    assertLexedTokensEquals("4.", et(NUMBER, "4.", 1, 0));
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "1,000",
        "1,000,000",
        "1,000,000.00",
        "1,1",
        "1,,2.12",
        "1,,,2,,,3",
        "123,456,789"
      })
  public void validSeparator(String separatedNumber) {
    assertLexedTokensEquals(separatedNumber, et(NUMBER, separatedNumber, 1, 0));
  }

  @Test
  public void commaBeforeNumberIsNotNumber() {
    assertLexedTokensEquals(",1", et(COMMA, ",", 1, 0), et(NUMBER, "1", 1, 1));
  }

  @Test
  public void commaAfterNumberIsNotNumber() {
    assertLexedTokensEquals("1,", et(NUMBER, "1", 1, 0), et(COMMA, ",", 1, 1));
  }

  @Test
  public void commaAfterDotCreatesSeparateTokens() {
    assertLexedTokensEquals(
        "1.2,3", et(NUMBER, "1.2", 1, 0), et(COMMA, ",", 1, 3), et(NUMBER, "3", 1, 4));
  }
}
