package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;
import org.jetbrains.annotations.Nullable;

public final class Posting extends AbstractNode<Posting, Posting.Builder> implements WithComment {
  private final Flag flag;
  private final Account account;
  private final ArithmeticExpression amountExpression;
  private final Commodity commodity;
  private final CostSpec costSpec;
  private final PriceAnnotation priceAnnotation;
  private final Metadata metadata;
  private final Comment comment;

  private Posting(
      Flag flag,
      Account account,
      ArithmeticExpression amountExpression,
      Commodity commodity,
      CostSpec costSpec,
      PriceAnnotation priceAnnotation,
      Metadata metadata,
      SourceLocation sourceLocation,
      Comment comment) {
    super(sourceLocation);
    this.account = comment == null ? Objects.requireNonNull(account, "account") : account;
    this.metadata = Objects.requireNonNull(metadata, "metadata");
    this.flag = flag;
    this.amountExpression = amountExpression;
    this.commodity = commodity;
    this.costSpec = costSpec;
    this.priceAnnotation = priceAnnotation;
    this.comment = comment;
  }

  public Flag flag() {
    return flag;
  }

  public Account account() {
    return account;
  }

  public ArithmeticExpression amountExpression() {
    return amountExpression;
  }

  public Commodity commodity() {
    return commodity;
  }

  public CostSpec costSpec() {
    return costSpec;
  }

  public PriceAnnotation priceAnnotation() {
    return priceAnnotation;
  }

  public Metadata metadata() {
    return metadata;
  }

  public static Builder newPosting() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitPosting(this, context);
  }

  @Override
  public Posting transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(
            sourceLocation(),
            flag,
            account,
            amountExpression,
            commodity,
            costSpec,
            priceAnnotation,
            metadata,
            comment);
    return null;
  }

  @Override
  @Nullable
  public Comment comment() {
    return comment;
  }

  public static final class Builder extends AbstractNode.Builder<Posting, Builder> {
    private Flag flag;
    private Account account;
    private ArithmeticExpression amountExpression;
    private Commodity commodity;
    private CostSpec costSpec;
    private PriceAnnotation priceAnnotation;
    private Metadata metadata;
    private Comment comment;

    private Builder(
        SourceLocation sourceLocation,
        Flag flag,
        Account account,
        ArithmeticExpression amountExpression,
        Commodity commodity,
        CostSpec costSpec,
        PriceAnnotation priceAnnotation,
        Metadata metadata,
        Comment comment) {
      super(sourceLocation);
      this.flag = flag;
      this.account = account;
      this.amountExpression = amountExpression;
      this.commodity = commodity;
      this.costSpec = costSpec;
      this.priceAnnotation = priceAnnotation;
      this.metadata = metadata;
      this.comment = comment;
    }

    private Builder() {}

    public Posting build() {
      return new Posting(
          flag,
          account,
          amountExpression,
          commodity,
          costSpec,
          priceAnnotation,
          metadata,
          sourceLocation(),
          comment);
    }

    public Flag flag() {
      return flag;
    }

    public Builder flag(Flag flag) {
      this.flag = flag;
      return this;
    }

    public Account account() {
      return account;
    }

    public Builder account(Account account) {
      this.account = account;
      return this;
    }

    public ArithmeticExpression amountExpression() {
      return amountExpression;
    }

    public Builder amountExpression(ArithmeticExpression amountExpression) {
      this.amountExpression = amountExpression;
      return this;
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }

    public CostSpec costSpec() {
      return costSpec;
    }

    public Builder costSpec(CostSpec costSpec) {
      this.costSpec = costSpec;
      return this;
    }

    public PriceAnnotation priceAnnotation() {
      return priceAnnotation;
    }

    public Builder priceAnnotation(PriceAnnotation priceAnnotation) {
      this.priceAnnotation = priceAnnotation;
      return this;
    }

    public Metadata metadata() {
      return metadata;
    }

    public Builder metadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }

    public Comment comment() {
      return comment;
    }

    public Builder comment(Comment comment) {
      this.comment = comment;
      return this;
    }
  }
}
