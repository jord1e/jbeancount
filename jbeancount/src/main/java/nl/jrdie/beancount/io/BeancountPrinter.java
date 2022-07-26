package nl.jrdie.beancount.io;

import nl.jrdie.beancount.language.Journal;
import org.jetbrains.annotations.NotNull;

public interface BeancountPrinter {

  @NotNull
  String print(@NotNull Journal journal) throws BeancountIOException;
}
