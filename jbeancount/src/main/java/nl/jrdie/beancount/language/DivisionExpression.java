package nl.jrdie.beancount.language;

public final class DivisionExpression extends AbstractBinaryArithmeticExpression {

  private DivisionExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    super(leftExpression, rightExpression);
  }

  public static Builder newDivisionExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractBinaryArithmeticExpression.Builder<DivisionExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public DivisionExpression build() {
      return new DivisionExpression(leftExpression, rightExpression);
    }
  }
}
