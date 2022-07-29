package nl.jrdie.beancount.parser;

import java.util.List;
import nl.jrdie.beancount.language.SourceLocation;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Interval;

public final class AntlrHelper {

  private AntlrHelper() {}

  public static SourceLocation createSourceLocation(int line, int column, String sourceName) {
    return SourceLocation.of(line, column, sourceName);
  }

  public static SourceLocation createSourceLocation(Token token, String sourceName) {
    return createSourceLocation(token.getLine(), token.getCharPositionInLine(), sourceName);
  }

  public static String createPreview(
      CharStream charStream, int line, int offendingStart, int offendingEnd) {
    StringBuilder sb = new StringBuilder();
    int startLine = line - 3;
    int endLine = line + 3;
    // TODO Overflow handling
    int lineMaxSize = String.valueOf(Math.max(startLine + 1, endLine + 1)).length();
    final String indent = " ".repeat(lineMaxSize + 3 + offendingStart);
    List<String> lines = charStream.getText(Interval.of(0, charStream.size())).lines().toList();
    for (int i = 0; i < lines.size(); i++) {
      if (i >= startLine && i <= endLine) {
        sb.append(String.format("%" + lineMaxSize + "s | ", i + 1))
            .append(lines.get(i))
            .append('\n');
        if (i == line - 1) {
          sb.append(indent)
              .append("^".repeat(offendingEnd - offendingStart))
              .append(" error location")
              .append('\n');
        }
      }
    }
    return sb.toString();
  }
}
