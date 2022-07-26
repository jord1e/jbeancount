package nl.jrdie.beancount.cli.commands.jordie;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import graphql.util.TreeTransformerUtil;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nl.jrdie.beancount.Beancount;
import nl.jrdie.beancount.cli.commands.mixin.SingleOutput;
import nl.jrdie.beancount.io.SimpleBeancountPrinter;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.Node;
import nl.jrdie.beancount.language.TransactionDirective;
import nl.jrdie.beancount.language.tools.AstTransformer;
import nl.jrdie.beancount.language.tools.NodeVisitorStub;
import nl.jrdie.beancount.parser.BeancountUtil;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ScopeType;

@Command(name = "fixjordie", scope = ScopeType.LOCAL)
public class FixStuffCommand implements Callable<Integer> {

  @Parameters(index = "0", description = "The Beancount file")
  private Path file;

  @Mixin private SingleOutput output;

  @Override
  public Integer call() throws Exception {
    Beancount beancount = Beancount.newBeancount().build();
    Journal journal = beancount.createJournalSyncWithoutIncludes(file);
    journal =
        (Journal)
            AstTransformer.transform(
                journal,
                new NodeVisitorStub() {
                  @Override
                  public TraversalControl visitTransactionDirective(
                      TransactionDirective td, TraverserContext<Node<?, ?>> data) {
                    final String regex =
                        "^Betaalautomaat ([0-9][0-9]?:[0-9][0-9]?) pasnr\\. [0-9][0-9][0-9]$";
                    if (td.narration().matches(regex)
                        && !BeancountUtil.hasMetadataWithKey(td.metadata(), "time")
                        && !BeancountUtil.hasMetadataWithKey(td.metadata(), "desc")) {
                      Matcher matcher = Pattern.compile(regex).matcher(td.narration());
                      matcher.matches();
                      TransactionDirective newtd =
                          td.transform(
                              transactionDirectiveBuilder ->
                                  transactionDirectiveBuilder
                                      .narration(null)
                                      .metadata(
                                          BeancountUtil.addMetadataAtStart(
                                              transactionDirectiveBuilder.metadata(),
                                              BeancountUtil.newMetadataItem(
                                                  "time", matcher.toMatchResult().group(1)),
                                              BeancountUtil.newMetadataItem(
                                                  "desc", td.narration()))));
                      return TreeTransformerUtil.changeNode(data, newtd);
                    }
                    return TraversalControl.CONTINUE;
                  }
                });

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
