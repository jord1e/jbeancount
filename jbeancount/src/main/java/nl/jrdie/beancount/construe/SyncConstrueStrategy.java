package nl.jrdie.beancount.construe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import nl.jrdie.beancount.language.Journal;

public class SyncConstrueStrategy implements BeancountConstrueStrategy {
  @Override
  public CompletableFuture<Journal> construe(Supplier<Journal> journalSupplier) {
    try {
      final Journal journal = journalSupplier.get();
      return CompletableFuture.completedFuture(journal);
    } catch (Exception e) { // TODO Should this be Throwable?
      return CompletableFuture.failedFuture(e);
    }
  }
}
