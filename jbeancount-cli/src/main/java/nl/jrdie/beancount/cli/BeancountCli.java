package nl.jrdie.beancount.cli;

import nl.jrdie.beancount.cli.commands.CheckJournal;
import nl.jrdie.beancount.cli.commands.FormatJournal;
import nl.jrdie.beancount.cli.commands.IncludeTreeCommand;
import nl.jrdie.beancount.cli.commands.InternalCommand;
import nl.jrdie.beancount.cli.commands.MergeJournal;
import nl.jrdie.beancount.cli.commands.SortJournal;
import nl.jrdie.beancount.cli.commands.jordie.JordieCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "jbeancount",
    mixinStandardHelpOptions = true,
    version = "jbeancount 0.1",
    description = "Extension utilities for the Beancount plain text accounting tool",
    subcommands = {
      JordieCommand.class,
      MergeJournal.class,
      FormatJournal.class,
      CheckJournal.class,
      IncludeTreeCommand.class,
      InternalCommand.class,
      SortJournal.class
    },
    scope = CommandLine.ScopeType.INHERIT)
public final class BeancountCli {

  private BeancountCli() {}

  public static void main(String... args) {
    @SuppressWarnings("InstantiationOfUtilityClass")
    final int exitCode = new CommandLine(new BeancountCli()).execute(args);
    System.exit(exitCode);
  }
}
