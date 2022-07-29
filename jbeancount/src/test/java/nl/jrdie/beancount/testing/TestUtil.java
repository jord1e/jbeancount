package nl.jrdie.beancount.testing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import java.io.StringReader;
import java.util.List;
import java.util.function.Function;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.parser.BeancountAntlrToLanguage;
import nl.jrdie.beancount.parser.BeancountParser;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Token;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.ObjectAssert;

public final class TestUtil {

  private TestUtil() {}

  public static Journal parse(String journalString) {
    return BeancountParser.newParser().parseJournal(new StringReader(journalString));
  }

  public static List<? extends Token> lex(String tokenizationString) {
    CharStream cs = CharStreams.fromString(tokenizationString);
    BeancountAntlrLexer lexer = new BeancountAntlrLexer(cs);
    return lexer.getAllTokens();
  }

  public static void assertLexedTokensEquals(String tokenizationString, Integer... types) {
    assertThat(lex(tokenizationString)).map(Token::getType).containsExactly(types);
  }

  public static void assertLexedTokensEquals(String tokenizationString, ExpectedToken... types) {
    assertThat(lex(tokenizationString))
        .filteredOn(token -> token.getType() != BeancountAntlrParser.EOL) // Hmmm
        .map(
            token ->
                new ExpectedToken(
                    BeancountAntlrLexer.VOCABULARY.getSymbolicName(token.getType()),
                    token.getText(),
                    token.getLine(),
                    token.getCharPositionInLine()))
        .containsExactly(types);
  }

  public static ExpectedToken et(int type, String text, int line, int col) {
    return new ExpectedToken(BeancountAntlrLexer.VOCABULARY.getSymbolicName(type), text, line, col);
  }

  public static Token t(int type, String text) {
    return new CommonToken(type, text);
  }

  public static void assertLexicalFailure(String tokenizationString) {
    CharStream cs = CharStreams.fromString(tokenizationString);
    BeancountAntlrLexer lexer = new BeancountAntlrLexer(cs);
    assertThat(lexer.getAllTokens()).isEmpty();
  }

  public static <T> ObjectAssert<T> assertToLanguage(
      Function<BeancountAntlrToLanguage, T> toLanguageFunction) {
    return assertThat(
        toLanguageFunction.apply(
            new BeancountAntlrToLanguage(null, "TestUtil.assertToLanguage(Function)")));
  }

  public static <T> AbstractThrowableAssert<?, ? extends Throwable> assertToLanguageThrows(
      Function<BeancountAntlrToLanguage, T> toLanguageFunction) {
    return assertThatCode(
        () ->
            toLanguageFunction.apply(
                new BeancountAntlrToLanguage(null, "TestUtil.assertToLanguageThrows(Function)")));
  }
}
