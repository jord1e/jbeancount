package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class DocumentDirective
    extends AbstractDirectiveNode<DocumentDirective, DocumentDirective.Builder> {
  private final String filename;
  private final Account account;

  private DocumentDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      String filename,
      Account account,
      Metadata metadata) {
    super(sourceLocation, date, tagsAndLinks, metadata);
    this.filename = Objects.requireNonNull(filename, "filename");
    this.account = Objects.requireNonNull(account, "account");
  }

  public String filename() {
    return filename;
  }

  public Account account() {
    return account;
  }

  public static Builder newDocumentDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitDocumentDirective(this, context);
  }

  @Override
  public DocumentDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), filename, account);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder
      extends AbstractDirectiveNode.Builder<DocumentDirective, Builder> {
    private String filename;
    private Account account;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String filename,
        Account account) {
      super(sourceLocation, date, tagsAndLinks, metadata);
      this.filename = filename;
      this.account = account;
    }

    public DocumentDirective build() {
      return new DocumentDirective(
          sourceLocation(), date(), tagsAndLinks(), filename, account, metadata());
    }

    public String filename() {
      return filename;
    }

    public Builder filename(String filename) {
      this.filename = filename;
      return this;
    }

    public Account account() {
      return account;
    }

    public Builder account(Account account) {
      this.account = account;
      return this;
    }
  }
}
