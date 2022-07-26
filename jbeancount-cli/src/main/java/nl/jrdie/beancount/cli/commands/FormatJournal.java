package nl.jrdie.beancount.cli.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.cli.commands.mixin.SingleOutput;
import nl.jrdie.beancount.cli.internal.transformations.SortTransactions;
import nl.jrdie.beancount.io.SimpleBeancountPrinter;
import nl.jrdie.beancount.language.Journal;
import picocli.CommandLine;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@CommandLine.Command(
    name = "format",
    description = "This command formats a journal (just like bean-format, but better)")
public class FormatJournal implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Option(
      names = "--experimental-sort-transactions",
      description = "Also sorts the transactions in the file",
      defaultValue = "false")
  private boolean sortTransactions;

  @Mixin private SingleOutput output;

  @Override
  public Integer call() throws IOException {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSyncWithoutIncludes(file);
    if (sortTransactions) {
      journal = SortTransactions.sortTransactions(journal);
    }
    SimpleBeancountPrinter beancountPrinter = SimpleBeancountPrinter.newDefaultPrinter();
    final String journalAsString = beancountPrinter.print(journal);
    if (output.hasOutput()) {
      Files.writeString(output.outputFile(), journalAsString, StandardCharsets.UTF_8);
    } else {
      System.out.println(journalAsString);
    }
    return 0;
  }
}
