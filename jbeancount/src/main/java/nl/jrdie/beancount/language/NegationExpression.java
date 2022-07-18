package nl.jrdie.beancount.language;

public final class NegationExpression extends AbstractUnaryArithmeticExpression {

  private NegationExpression(ArithmeticExpression expression) {
    super(expression);
  }

  public static Builder newNegationExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractUnaryArithmeticExpression.Builder<NegationExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public NegationExpression build() {
      return new NegationExpression(expression);
    }
  }
}
