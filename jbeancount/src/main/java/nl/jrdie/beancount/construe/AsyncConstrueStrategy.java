package nl.jrdie.beancount.construe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import nl.jrdie.beancount.language.Journal;

public class AsyncConstrueStrategy implements BeancountConstrueStrategy {
  @Override
  public CompletableFuture<Journal> construe(Supplier<Journal> journalSupplier) {
    return CompletableFuture.supplyAsync(journalSupplier);
  }
}
