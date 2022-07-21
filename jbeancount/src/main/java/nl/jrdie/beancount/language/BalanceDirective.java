package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class BalanceDirective
    extends AbstractDirectiveNode<BalanceDirective, BalanceDirective.Builder> {
  private final Account account;
  private final Amount amount;

  public BalanceDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account account,
      Amount amount,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.account = Objects.requireNonNull(account, "account");
    this.amount = Objects.requireNonNull(amount, "amount");
  }

  public Account account() {
    return account;
  }

  public Amount amount() {
    return amount;
  }

  public static Builder newBalanceDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitBalanceDirective(this, context);
  }

  @Override
  public BalanceDirective transform(Consumer<Builder> builderConsumer) {
    Builder builder =
        new Builder(
            sourceLocation(), date(), tagsAndLinks(), metadata(), account, amount, comment());
    builderConsumer.accept(builder);
    return builder.build();
  }

  public static final class Builder
      extends AbstractDirectiveNode.Builder<BalanceDirective, Builder> {
    private Account account;
    private Amount amount;

    private Builder() {
      super();
    }

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Account account,
        Amount amount,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.account = account;
      this.amount = amount;
    }

    @Override
    public BalanceDirective build() {
      return new BalanceDirective(
          sourceLocation(), date(), tagsAndLinks(), account, amount, metadata(), comment());
    }

    public Account account() {
      return account;
    }

    public Builder account(Account account) {
      this.account = account;
      return this;
    }

    public Amount amount() {
      return amount;
    }

    public Builder amount(Amount amount) {
      this.amount = amount;
      return this;
    }
  }
}
