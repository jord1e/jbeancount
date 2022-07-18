package nl.jrdie.beancount.language;

public final class CompoundAmount implements CostCompValue {
  private final Commodity commodity;
  private final CompoundExpression compoundExpression;

  private CompoundAmount(Commodity commodity, CompoundExpression compoundExpression) {
    this.commodity = commodity;
    this.compoundExpression = compoundExpression;
  }

  public Commodity commodity() {
    return commodity;
  }

  public CompoundExpression compoundExpression() {
    return compoundExpression;
  }

  public static Builder newCompoundAmount() {
    return new Builder();
  }

  public static final class Builder {
    private Commodity commodity;
    private CompoundExpression compoundExpression;

    private Builder() {}

    public CompoundAmount build() {
      return new CompoundAmount(commodity, compoundExpression);
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }

    public CompoundExpression compoundExpression() {
      return compoundExpression;
    }

    public Builder compoundExpression(CompoundExpression compoundExpression) {
      this.compoundExpression = compoundExpression;
      return this;
    }
  }
}
