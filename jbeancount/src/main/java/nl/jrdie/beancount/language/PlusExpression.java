package nl.jrdie.beancount.language;

public final class PlusExpression extends AbstractUnaryArithmeticExpression {

  private PlusExpression(ArithmeticExpression expression) {
    super(expression);
  }

  public static Builder newPlusExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractUnaryArithmeticExpression.Builder<PlusExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public PlusExpression build() {
      return new PlusExpression(expression);
    }
  }
}
