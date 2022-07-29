package nl.jrdie.beancount.cli.picocli;

import java.io.UncheckedIOException;
import java.nio.file.NoSuchFileException;
import java.util.concurrent.CompletionException;
import nl.jrdie.beancount.language.SourceLocation;
import nl.jrdie.beancount.parser.InvalidSyntaxException;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class BeancountExecutionExceptionHandler implements IExecutionExceptionHandler {
  @Override
  public int handleExecutionException(
      final Exception e, final CommandLine commandLine, final ParseResult parseResult)
      throws Exception {
    Throwable toHandle = e;
    if (toHandle instanceof CompletionException) {
      toHandle = toHandle.getCause();
    }
    if (toHandle instanceof UncheckedIOException) {
      toHandle = toHandle.getCause();
    }
    if (toHandle instanceof NoSuchFileException noSuchFileException) {
      commandLine.getErr().println("File " + noSuchFileException.getFile() + " does not exist");
    } else if (toHandle instanceof InvalidSyntaxException invalidSyntaxException) {
      final SourceLocation sourceLocation = invalidSyntaxException.getSourceLocation();
      commandLine
          .getErr()
          .println(
              "Invalid syntax on line "
                  + sourceLocation.line()
                  + ", column "
                  + sourceLocation.column()
                  + " in source "
                  + sourceLocation.sourceName()
                  + ": "
                  + invalidSyntaxException.getMessage()
                  + "\n\n"
                  + invalidSyntaxException.getPreview());
    } else {
      e.printStackTrace();
    }
    return commandLine.getExitCodeExceptionMapper() != null
        ? commandLine.getExitCodeExceptionMapper().getExitCode(e)
        : commandLine.getCommandSpec().exitCodeOnExecutionException();
  }
}
