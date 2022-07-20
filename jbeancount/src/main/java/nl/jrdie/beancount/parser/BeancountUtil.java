package nl.jrdie.beancount.parser;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;

public final class BeancountUtil {

  private BeancountUtil() {}

  public static <T extends JournalDeclaration<?, ?>> List<T> findDeclarationsOfType(
      Journal journal, Class<T> type) {
    return journal.declarations().stream().filter(type::isInstance).map(type::cast).toList();
  }

  public static List<JournalDeclaration<?, ?>> findDeclarations(
      Journal journal, Predicate<JournalDeclaration<?, ?>> declarationPredicate) {
    return journalDeclarationStream(journal, declarationPredicate).toList();
  }

  public static <T extends JournalDeclaration<?, ?>> List<T> findDeclarations(
      Journal journal, Class<T> type, Predicate<T> declarationPredicate) {
    return journal.declarations().stream()
        .<T>mapMulti(
            (journalDeclaration, consumer) -> {
              if (type.isInstance(journalDeclaration)) {
                T t = type.cast(journalDeclaration);
                if (declarationPredicate.test(t)) {
                  consumer.accept(t);
                }
              }
            })
        .toList();
  }

  private static Stream<JournalDeclaration<?, ?>> journalDeclarationStream(
      Journal journal, Predicate<JournalDeclaration<?, ?>> declarationPredicate) {
    return journal.declarations().stream().filter(declarationPredicate);
  }
}
