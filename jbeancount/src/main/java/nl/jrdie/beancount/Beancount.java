package nl.jrdie.beancount;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import nl.jrdie.beancount.annotation.Beta;
import nl.jrdie.beancount.construe.BeancountConstrueStrategy;
import nl.jrdie.beancount.construe.SyncConstrueStrategy;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.tools.NestJournalNodeInIncludePragmaTransformer;
import nl.jrdie.beancount.parser.BeancountParser;

public final class Beancount {

  private final BeancountConstrueStrategy construeStrategy;

  private Beancount() {
    this.construeStrategy = new SyncConstrueStrategy();
  }

  public Journal createJournalSync(Path path) {
    return createJournal(path).join();
  }

  public CompletableFuture<Journal> createJournal(Path path) {
    return createJournal(path, BeancountParser.newParser(), true);
  }

  @Beta
  public Journal createJournalSyncWithoutIncludes(Path path) {
    return createJournalWithoutIncludes(path).join();
  }

  @Beta
  public CompletableFuture<Journal> createJournalWithoutIncludes(Path path) {
    return createJournal(path, BeancountParser.newParser(), false);
  }

  private CompletableFuture<Journal> createJournal(
      Path path, BeancountParser beancountParser, boolean resolveIncludePragmas) {
    final CompletableFuture<Journal> rootJournal =
        construeStrategy.construe(() -> beancountParser.parseJournal(path));
    if (!resolveIncludePragmas) {
      return rootJournal;
    }
    return rootJournal.thenCompose(
        journal ->
            resolveIncludePragmas(path, journal, beancountParser)
                .thenApply(
                    map -> NestJournalNodeInIncludePragmaTransformer.transform(journal, map)));
  }

  private CompletableFuture<Map<IncludePragma, Journal>> resolveIncludePragmas(
      Path theJournalPath, Journal theJournal, BeancountParser beancountParser) {
    List<IncludePragma> includePragmas = new ArrayList<>();
    for (JournalDeclaration<?, ?> declaration : theJournal.declarations()) {
      if (declaration instanceof IncludePragma includePragma) {
        includePragmas.add(includePragma);
      }
    }
    // TODO: new CompletableFuture<>[includePragmas.size()] not seen as an error by IDEA, report on
    // tracker
    @SuppressWarnings("unchecked")
    CompletableFuture<Journal>[] includes =
        (CompletableFuture<Journal>[]) new CompletableFuture[includePragmas.size()];
    for (int i = 0; i < includePragmas.size(); i++) {
      IncludePragma includePragma = includePragmas.get(i);
      Path includePath = theJournalPath.getParent().resolve(includePragma.filename());
      includes[i] = createJournal(includePath, beancountParser, true);
    }

    CompletableFuture<Map<IncludePragma, Journal>> result = new CompletableFuture<>();

    CompletableFuture.allOf(includes)
        .whenComplete(
            (nil, t) -> {
              if (t != null) {
                result.completeExceptionally(t);
                return;
              }
              Map<IncludePragma, Journal> resolved = new HashMap<>();
              for (int i = 0; i < includePragmas.size(); i++) {
                resolved.put(includePragmas.get(i), includes[i].join());
              }
              result.complete(resolved);
            });

    return result;
  }

  public static Builder newBeancount() {
    return new Builder();
  }

  public static class Builder {
    private Builder() {}

    public Beancount build() {
      return new Beancount();
    }
  }
}
