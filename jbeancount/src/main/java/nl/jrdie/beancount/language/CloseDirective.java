package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class CloseDirective
    extends AbstractDirectiveNode<CloseDirective, CloseDirective.Builder> {
  private final Account account;

  private CloseDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account account,
      Metadata metadata) {
    super(sourceLocation, date, tagsAndLinks, metadata);
    this.account = Objects.requireNonNull(account, "account");
  }

  public Account account() {
    return account;
  }

  public static Builder newCloseDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitCloseDirective(this, context);
  }

  @Override
  public CloseDirective transform(Consumer<Builder> builderConsumer) {
    Builder b = new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), account);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<CloseDirective, Builder> {
    private Account account;

    private Builder() {}

    public Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Account account) {
      super(sourceLocation, date, tagsAndLinks, metadata);
      this.account = account;
    }

    @Override
    public CloseDirective build() {
      return new CloseDirective(sourceLocation(), date(), tagsAndLinks(), account, metadata());
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
