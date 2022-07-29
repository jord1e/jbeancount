package nl.jrdie.beancount.cli;

import java.nio.file.Path;
import nl.jrdie.beancount.cli.commands.CheckJournal;
import nl.jrdie.beancount.cli.commands.FormatJournal;
import nl.jrdie.beancount.cli.commands.IncludeTreeCommand;
import nl.jrdie.beancount.cli.commands.InternalCommand;
import nl.jrdie.beancount.cli.commands.MergeJournal;
import nl.jrdie.beancount.cli.commands.SortJournal;
import nl.jrdie.beancount.cli.commands.jordie.JordieCommand;
import nl.jrdie.beancount.cli.picocli.PathConverter;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;

@Command(
    name = "jbeancount",
    mixinStandardHelpOptions = true,
    version = {
      "JBeancount %1$s",
      "Running on Java version ${java.version} by ${java.vendor} (${java.vm.name} version ${java.vm.version})",
      "Running on ${os.name} version ${os.version} (${os.arch})"
    },
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

  private static final String VERSION = "prerelease";

  private BeancountCli() {}

  public static void main(String... args) {
    @SuppressWarnings("InstantiationOfUtilityClass")
    final CommandLine commandLine = new CommandLine(new BeancountCli());
    commandLine.printVersionHelp(System.out, Help.Ansi.AUTO, BeancountCli.VERSION);
    commandLine.registerConverter(Path.class, new PathConverter());
    final int exitCode = commandLine.execute(args);
    System.exit(exitCode);
  }
}
