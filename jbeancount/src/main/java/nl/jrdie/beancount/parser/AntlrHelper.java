package nl.jrdie.beancount.parser;

import nl.jrdie.beancount.language.SourceLocation;
import org.antlr.v4.runtime.Token;

public final class AntlrHelper {

  private AntlrHelper() {}

  public static SourceLocation createSourceLocation(int line, int column) {
    // Lines in ANTLR start at zero, make sure the starting location of our file is (0, 0).
    return SourceLocation.of(line - 1, column);
  }

  public static SourceLocation createSourceLocation(Token token) {
    return createSourceLocation(token.getLine(), token.getCharPositionInLine());
  }
}
