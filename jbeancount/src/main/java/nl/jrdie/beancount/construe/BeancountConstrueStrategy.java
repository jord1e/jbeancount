package nl.jrdie.beancount.construe;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import nl.jrdie.beancount.language.Journal;

public interface BeancountConstrueStrategy {

  CompletableFuture<Journal> construe(Supplier<Journal> journalSupplier);
}
