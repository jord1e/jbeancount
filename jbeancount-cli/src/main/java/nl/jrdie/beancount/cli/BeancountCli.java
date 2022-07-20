package nl.jrdie.beancount.cli;

import nl.jrdie.beancount.cli.commands.CheckJournal;
import nl.jrdie.beancount.cli.commands.CombineJournal;
import nl.jrdie.beancount.cli.commands.FormatJournal;
import nl.jrdie.beancount.cli.commands.InternalCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "jbeancount",
    mixinStandardHelpOptions = true,
    version = "jbeancount 0.1",
    description = "Extension utilities for the Beancount plain text accounting tool",
    subcommands = {
      CombineJournal.class,
      FormatJournal.class,
      CheckJournal.class,
      InternalCommand.class
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
