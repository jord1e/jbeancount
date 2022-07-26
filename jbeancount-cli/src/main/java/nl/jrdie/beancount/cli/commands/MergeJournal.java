package nl.jrdie.beancount.cli.commands;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.BeancountInvalidStateException;
import nl.jrdie.beancount.cli.commands.mixin.SingleOutput;
import nl.jrdie.beancount.cli.internal.transformations.FlattenJournal;
import nl.jrdie.beancount.io.SimpleBeancountPrinter;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.SourceLocation;
import nl.jrdie.beancount.util.ImmutableKit;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "merge",
    description =
        "This command (recursively) merges all journals included using include pragmas, and creates one composite journal")
public class MergeJournal implements Callable<Integer> {

  @Parameters(
      index = "0",
      arity = "1..*",
      description = "The Beancount file(s) at the root(s) of your inclusion tree(s)")
  private List<Path> files;

  @Option(
      names = "-r",
      description =
          "Recursively aggregate (also flattens include pragmas within included journals)",
      defaultValue = "false")
  private boolean recurse;

  @Option(
      names = "--keep-include-pragmas",
      description =
          "Keep the include pragmas present in the composite journal (just before the contents of said journal)",
      defaultValue = "false")
  private boolean keepIncludePragmas;

  @Mixin private SingleOutput output;

  @Override
  public Integer call() throws IOException {
    Beancount beancount = Beancount.newBeancount().build();
    if (files.isEmpty()) {
      throw new BeancountInvalidStateException();
    }
    final Journal masterJournal =
        files.stream()
            .map(beancount::createJournalSync)
            .reduce(
                Journal.newJournal()
                    .declarations(ImmutableKit.emptyList())
                    .sourceLocation(SourceLocation.EMPTY)
                    .build(),
                (journal, journal2) ->
                    FlattenJournal.flattenJournal(journal, recurse, keepIncludePragmas)
                        .transform(
                            builder -> {
                              List<JournalDeclaration<?, ?>> combinedDeclarations =
                                  new ArrayList<>(builder.declarations());
                              combinedDeclarations.addAll(
                                  FlattenJournal.flattenJournal(
                                          journal2, recurse, keepIncludePragmas)
                                      .declarations());
                              builder.declarations(combinedDeclarations);
                            }));
    SimpleBeancountPrinter beancountPrinter = SimpleBeancountPrinter.newDefaultPrinter();
    final String journalAsString = beancountPrinter.print(masterJournal);
    if (output.hasOutput()) {
      Files.writeString(output.outputFile(), journalAsString, StandardCharsets.UTF_8);
    } else {
      System.out.println(journalAsString);
    }
    return 0;
  }
}
