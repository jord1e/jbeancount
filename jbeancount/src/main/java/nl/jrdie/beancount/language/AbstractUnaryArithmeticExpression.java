package nl.jrdie.beancount.language;

import java.util.Objects;

abstract sealed class AbstractUnaryArithmeticExpression implements ArithmeticExpression
    permits NegationExpression, ParenthesisedExpression, PlusExpression {
  private final ArithmeticExpression expression;

  protected AbstractUnaryArithmeticExpression(ArithmeticExpression expression) {
    this.expression = Objects.requireNonNull(expression, "expression");
  }

  public ArithmeticExpression expression() {
    return expression;
  }

  @SuppressWarnings("unchecked")
  protected abstract static sealed class Builder<
          T extends AbstractUnaryArithmeticExpression, B extends Builder<T, B>>
      permits NegationExpression.Builder, ParenthesisedExpression.Builder, PlusExpression.Builder {
    protected ArithmeticExpression expression;

    protected Builder() {}

    public abstract T build();

    public ArithmeticExpression expression() {
      return expression;
    }

    public B expression(ArithmeticExpression expression) {
      this.expression = expression;
      return (B) this;
    }
  }
}
