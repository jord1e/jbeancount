package nl.jrdie.beancount.language;

import nl.jrdie.beancount.annotation.Beta;

@Beta("Naming uncertain")
public final class ParenthesisedExpression extends AbstractUnaryArithmeticExpression {

  private ParenthesisedExpression(ArithmeticExpression expression) {
    super(expression);
  }

  public static Builder newParenthesisedExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractUnaryArithmeticExpression.Builder<ParenthesisedExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public ParenthesisedExpression build() {
      return new ParenthesisedExpression(expression);
    }
  }
}
