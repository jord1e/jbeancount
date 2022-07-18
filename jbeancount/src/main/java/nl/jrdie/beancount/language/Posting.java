package nl.jrdie.beancount.language;

import java.util.Objects;

public final class Posting {
  private final Flag flag;
  private final Account account;
  private final ArithmeticExpression amountExpression;
  private final Commodity commodity;
  private final CostSpec costSpec;
  private final PriceAnnotation priceAnnotation;
  private final Metadata metadata;

  private Posting(
      Flag flag,
      Account account,
      ArithmeticExpression amountExpression,
      Commodity commodity,
      CostSpec costSpec,
      PriceAnnotation priceAnnotation,
      Metadata metadata) {
    this.account = Objects.requireNonNull(account, "account");
    this.metadata = Objects.requireNonNull(metadata, "metadata");
    this.flag = flag;
    this.amountExpression = amountExpression;
    this.commodity = commodity;
    this.costSpec = costSpec;
    this.priceAnnotation = priceAnnotation;
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

  public static final class Builder {
    private Flag flag;
    private Account account;
    private ArithmeticExpression amountExpression;
    private Commodity commodity;
    private CostSpec costSpec;
    private PriceAnnotation priceAnnotation;
    private Metadata metadata;

    private Builder() {}

    public Posting build() {
      return new Posting(
          flag, account, amountExpression, commodity, costSpec, priceAnnotation, metadata);
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
  }
}
