package nl.jrdie.beancount.language;

public final class SubtractionExpression extends AbstractBinaryArithmeticExpression {

  private SubtractionExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    super(leftExpression, rightExpression);
  }

  public static Builder newSubtractionExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractBinaryArithmeticExpression.Builder<SubtractionExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public SubtractionExpression build() {
      return new SubtractionExpression(leftExpression, rightExpression);
    }
  }
}
