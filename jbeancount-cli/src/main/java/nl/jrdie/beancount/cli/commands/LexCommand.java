package nl.jrdie.beancount.cli.commands;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Token;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "lex", description = "This command dumps the lexer output")
public class LexCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Option(
      names = "--no-header",
      description = "Do not display the descriptive header",
      defaultValue = "false")
  private boolean noHeader;

  @Override
  public Integer call() throws Exception {
    final CharStream charStream = CharStreams.fromPath(file, StandardCharsets.UTF_8);
    final BeancountAntlrLexer lexer = new BeancountAntlrLexer(charStream);
    final List<? extends Token> tokens = lexer.getAllTokens();
    final StringBuilder sb = new StringBuilder();
    int maxLineNumLength = 0;
    int maxColNumLength = 0;
    int maxTokenNameLength = 0;
    int maxSymbolNameLength = 0;
    for (Token token : tokens) {
      maxTokenNameLength = Math.max(tokenName(token).length(), maxTokenNameLength);
      maxSymbolNameLength = Math.max(symbolicName(token).length(), maxSymbolNameLength);
      maxLineNumLength = Math.max(String.valueOf(token.getLine()).length(), maxLineNumLength);
      maxColNumLength =
          Math.max(String.valueOf(token.getCharPositionInLine()).length(), maxColNumLength);
    }
    if (!noHeader) {
      maxTokenNameLength = Math.max("Class".length(), maxTokenNameLength);
      maxSymbolNameLength = Math.max("Symbol".length(), maxSymbolNameLength);
      maxLineNumLength = Math.max("Line".length(), maxLineNumLength);
      maxColNumLength = Math.max("Col".length(), maxColNumLength);
    }
    final String format =
        "%"
            + maxTokenNameLength
            + "s %"
            + maxSymbolNameLength
            + "s %"
            + maxLineNumLength
            + "s:%-"
            + maxColNumLength
            + "s \"%s\"\n";
    if (!noHeader) {
      sb.append(String.format(format, "Class", "Symbol", "Line", "Col", "Text"));
    }
    for (Token token : tokens) {
      final String tokenName = tokenName(token);
      sb.append(
          String.format(
              format,
              tokenName,
              symbolicName(token),
              token.getLine(),
              token.getCharPositionInLine(),
              token.getText().replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r")));
    }
    System.out.println(sb);
    return 0;
  }

  private static String symbolicName(Token token) {
    final String symbolicName = BeancountAntlrLexer.VOCABULARY.getSymbolicName(token.getType());
    return symbolicName == null ? String.valueOf(token.getType()) : symbolicName;
  }

  private static String tokenName(Token token) {
    return switch (token.getClass().getSimpleName()) {
      case "FakeCommentBeforeEolToken" -> "CommentBefEol";
      case "FakeEolAfterCommentToken" -> "EolAftComment";
      default -> token.getClass().getSimpleName();
    };
  }
}
