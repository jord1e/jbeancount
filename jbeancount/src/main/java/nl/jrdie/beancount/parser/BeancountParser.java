package nl.jrdie.beancount.parser;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrLexer;
import nl.jrdie.beancount.parser.antlr.BeancountAntlrParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class BeancountParser {

  private BeancountParser() {}

  public static BeancountParser newParser() {
    return new BeancountParser();
  }

  public Journal parseJournal(Path path) {
    return parseJournalImpl(path, CharStreams::fromPath);
  }

  public Journal parseJournal(Reader reader) {
    return parseJournalImpl(reader, CharStreams::fromReader);
  }

  public static AtomicLong ms = new AtomicLong(0);
  public static AtomicLong c = new AtomicLong(0);

  private <T> Journal parseJournalImpl(T t, CharStreamFunction<T> func) {
    final CharStream charStream;
    try {
      charStream = func.apply(t);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    final BeancountAntlrLexer lexer = new BeancountAntlrLexer(charStream);

    final CommonTokenStream tokens = new CommonTokenStream(lexer);

    final nl.jrdie.beancount.parser.antlr.BeancountAntlrParser antlrParser =
        new nl.jrdie.beancount.parser.antlr.BeancountAntlrParser(tokens);

    final BeancountAntlrToLanguage toLanguage = new BeancountAntlrToLanguage(tokens);

    long a = System.currentTimeMillis();
    final BeancountAntlrParser.JournalContext journalContext = antlrParser.journal();
    ms.addAndGet(System.currentTimeMillis() - a);
    c.incrementAndGet();

    //    String tokensDebug =
    //            tokens.getTokens().stream()
    //                    .mapToInt(Token::getType)
    //                    .mapToObj(BeancountAntlrParser.VOCABULARY::getSymbolicName)
    //                    .collect(Collectors.joining(" "));
    //
    //    System.out.println(tokensDebug);

    @SuppressWarnings("UnnecessaryLocalVariable")
    final Journal journal = toLanguage.createJournal(journalContext);

    return journal;
  }
}
