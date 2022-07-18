package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class PadDirective extends AbstractDirectiveNode<PadDirective, PadDirective.Builder> {
  private final Account sourceAccount;
  private final Account targetAccount;

  private PadDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account sourceAccount,
      Account targetAccount,
      Metadata metadata) {
    super(sourceLocation, date, tagsAndLinks, metadata);
    this.sourceAccount = Objects.requireNonNull(sourceAccount, "sourceAccount");
    this.targetAccount = Objects.requireNonNull(targetAccount, "targetAccount");
  }

  public Account sourceAccount() {
    return sourceAccount;
  }

  public Account targetAccount() {
    return targetAccount;
  }

  public static Builder newPadDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitPadDirective(this, context);
  }

  @Override
  public PadDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(
            sourceLocation(), date(), tagsAndLinks(), metadata(), sourceAccount, targetAccount);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<PadDirective, Builder> {
    private Account sourceAccount;
    private Account targetAccount;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Account sourceAccount,
        Account targetAccount) {
      super(sourceLocation, date, tagsAndLinks, metadata);
      this.sourceAccount = sourceAccount;
      this.targetAccount = targetAccount;
    }

    @Override
    public PadDirective build() {
      return new PadDirective(
          sourceLocation(), date(), tagsAndLinks(), sourceAccount, targetAccount, metadata());
    }

    public Account sourceAccount() {
      return sourceAccount;
    }

    public Builder sourceAccount(Account sourceAccount) {
      this.sourceAccount = sourceAccount;
      return this;
    }

    public Account targetAccount() {
      return targetAccount;
    }

    public Builder targetAccount(Account targetAccount) {
      this.targetAccount = targetAccount;
      return this;
    }
  }
}
