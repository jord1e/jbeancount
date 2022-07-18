package nl.jrdie.beancount.cli.commands;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.BeancountPrinter;
import nl.jrdie.beancount.language.Journal;
import picocli.CommandLine;

@CommandLine.Command(
    name = "format",
    description = "This command formats a journal (just like bean-format, but better)")
public class FormatJournal implements Callable<Integer> {

  @CommandLine.Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Override
  public Integer call() {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSync(file);
    BeancountPrinter beancountPrinter = BeancountPrinter.newPrinter();
    System.out.println(beancountPrinter.print(journal));
    return 0;
  }
}
