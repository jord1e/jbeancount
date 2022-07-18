package nl.jrdie.beancount;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import nl.jrdie.beancount.construe.BeancountConstrueStrategy;
import nl.jrdie.beancount.construe.SyncConstrueStrategy;
import nl.jrdie.beancount.language.IncludePragma;
import nl.jrdie.beancount.language.Journal;
import nl.jrdie.beancount.language.JournalDeclaration;
import nl.jrdie.beancount.language.tools.AstTransformer;
import nl.jrdie.beancount.language.tools.IncludeJournalTransformer;
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
    final CompletableFuture<Journal> rootJournal =
        construeStrategy.construe(() -> BeancountParser.newParser().parseJournal(path));
    return rootJournal.thenCompose(
        journal ->
            resolveIncludePragmas(path, journal)
                .thenApply(
                    map ->
                        (Journal)
                            AstTransformer.transform(journal, new IncludeJournalTransformer(map))));
  }

  private CompletableFuture<Map<IncludePragma, Journal>> resolveIncludePragmas(
      Path rootPath, Journal theJournal) {
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
      Path includePath = rootPath.getParent().resolve(includePragma.filename());
      includes[i] = createJournal(includePath);
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
