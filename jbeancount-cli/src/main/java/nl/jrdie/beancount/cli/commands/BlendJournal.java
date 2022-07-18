package nl.jrdie.beancount.cli.commands;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.BeancountPrinter;
import nl.jrdie.beancount.cli.internal.transformations.FlattenJournal;
import nl.jrdie.beancount.language.Journal;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "blend",
    description =
        "This command (recursively) aggregates all the journals included using include pragmas and creates one composite journal")
public class BlendJournal implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file at the root of your inclusion tree")
  private Path file;

  @Option(
      names = "-r",
      description =
          "Recursively aggregate (also flattens include pragmas within included journals)",
      defaultValue = "false")
  private boolean recurse;

  @Option(
      names = "--keep-include",
      description =
          "Keep the include pragmas present in the composite journal (just before the contents of said journal)",
      defaultValue = "false")
  private boolean keepIncludePragmas;

  @Override
  public Integer call() {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSync(file);
    journal = FlattenJournal.flattenJournal(journal, recurse, keepIncludePragmas);
    BeancountPrinter beancountPrinter = BeancountPrinter.newPrinter();
    System.out.println(beancountPrinter.print(journal));
    return 0;
  }
}
