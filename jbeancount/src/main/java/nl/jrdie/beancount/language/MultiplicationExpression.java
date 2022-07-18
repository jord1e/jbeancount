package nl.jrdie.beancount.language;

public final class MultiplicationExpression extends AbstractBinaryArithmeticExpression {

  private MultiplicationExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    super(leftExpression, rightExpression);
  }

  public static Builder newMultiplicationExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractBinaryArithmeticExpression.Builder<MultiplicationExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public MultiplicationExpression build() {
      return new MultiplicationExpression(leftExpression, rightExpression);
    }
  }
}
