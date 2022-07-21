package nl.jrdie.beancount.cli.internal.transformations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.TransactionDirective;

public final class SortTransactions {

  private SortTransactions() {}

  public static Journal sortTransactions(Journal journal) {
    final List<JournalDeclaration<?, ?>> declarations = journal.declarations();
    final List<JournalDeclaration<?, ?>> sortedDeclarations = new ArrayList<>(declarations);
    sortedDeclarations.sort(new TransactionComparator());
    return journal.transform(builder -> builder.declarations(sortedDeclarations));
  }

  private static final class TransactionComparator implements Comparator<JournalDeclaration<?, ?>> {
    @Override
    public int compare(JournalDeclaration<?, ?> o1, JournalDeclaration<?, ?> o2) {
      if (!(o1 instanceof TransactionDirective) || !(o2 instanceof TransactionDirective)) {
        return 0;
      }
      return Comparator.<JournalDeclaration<?, ?>, LocalDate>comparing(
              journalDeclaration -> ((TransactionDirective) journalDeclaration).date())
          .thenComparingInt(journalDeclaration -> journalDeclaration.sourceLocation().line())
          .compare(o1, o2);
    }
  }
}
