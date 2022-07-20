package nl.jrdie.beancount.cli.commands;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.language.Journal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "check", description = "Checks the beancount file for errors")
public class CheckJournal implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Override
  public Integer call() {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSync(file);
    return 0;
  }
}
