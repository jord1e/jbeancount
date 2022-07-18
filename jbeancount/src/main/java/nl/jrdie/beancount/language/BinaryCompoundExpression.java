package nl.jrdie.beancount.language;

public final class BinaryCompoundExpression implements CompoundExpression {
  private final ArithmeticExpression leftExpression;
  private final ArithmeticExpression rightExpression;

  private BinaryCompoundExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    this.leftExpression = leftExpression;
    this.rightExpression = rightExpression;
  }

  public ArithmeticExpression leftExpression() {
    return leftExpression;
  }

  public ArithmeticExpression rightExpression() {
    return rightExpression;
  }

  public static Builder newBinaryCompoundExpression() {
    return new Builder();
  }

  public static final class Builder {
    private ArithmeticExpression leftExpression;
    private ArithmeticExpression rightExpression;

    private Builder() {}

    public BinaryCompoundExpression build() {
      return new BinaryCompoundExpression(leftExpression, rightExpression);
    }

    public ArithmeticExpression leftExpression() {
      return leftExpression;
    }

    public Builder leftExpression(ArithmeticExpression leftExpression) {
      this.leftExpression = leftExpression;
      return this;
    }

    public ArithmeticExpression rightExpression() {
      return rightExpression;
    }

    public Builder rightExpression(ArithmeticExpression rightExpression) {
      this.rightExpression = rightExpression;
      return this;
    }
  }
}
