package nl.jrdie.beancount.cli;

import nl.jrdie.beancount.cli.commands.BlendJournal;
import nl.jrdie.beancount.cli.commands.FormatJournal;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ScopeType;

@Command(
    name = "jbeancount",
    mixinStandardHelpOptions = true,
    version = "jbeancount 0.1",
    description = "Extension utilities for the Beancount plain text accounting tool",
    subcommands = {BlendJournal.class, FormatJournal.class},
    scope = ScopeType.INHERIT)
class BeancountCli {

  public static void main(String... args) {
    @SuppressWarnings("InstantiationOfUtilityClass")
    int exitCode = new CommandLine(new BeancountCli()).execute(args);
    System.exit(exitCode);
  }
}
