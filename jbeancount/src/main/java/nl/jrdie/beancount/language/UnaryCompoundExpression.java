package nl.jrdie.beancount.language;

import java.util.Objects;

public final class UnaryCompoundExpression implements CompoundExpression {
  private final ArithmeticExpression expression;

  public UnaryCompoundExpression(ArithmeticExpression expression) {
    this.expression = Objects.requireNonNull(expression, "expression");
  }

  public ArithmeticExpression expression() {
    return expression;
  }

  public static Builder newUnaryCompoundExpression() {
    return new Builder();
  }

  public static final class Builder {
    private ArithmeticExpression expression;

    private Builder() {}

    public UnaryCompoundExpression build() {
      return new UnaryCompoundExpression(expression);
    }

    public ArithmeticExpression expression() {
      return expression;
    }

    public Builder expression(ArithmeticExpression expression) {
      this.expression = expression;
      return this;
    }
  }
}
