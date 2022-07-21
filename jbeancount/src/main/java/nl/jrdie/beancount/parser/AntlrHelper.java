package nl.jrdie.beancount.parser;

import nl.jrdie.beancount.language.SourceLocation;
import org.antlr.v4.runtime.Token;

public final class AntlrHelper {

  private AntlrHelper() {}

  public static SourceLocation createSourceLocation(int line, int column, String sourceName) {
    return SourceLocation.of(line, column, sourceName);
  }

  public static SourceLocation createSourceLocation(Token token, String sourceName) {
    return createSourceLocation(token.getLine(), token.getCharPositionInLine(), sourceName);
  }
}
