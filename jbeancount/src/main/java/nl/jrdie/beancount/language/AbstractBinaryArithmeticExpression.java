package nl.jrdie.beancount.language;

import java.util.Objects;

abstract sealed class AbstractBinaryArithmeticExpression implements ArithmeticExpression
    permits AdditionExpression,
        DivisionExpression,
        MultiplicationExpression,
        SubtractionExpression {
  private final ArithmeticExpression leftExpression;
  private final ArithmeticExpression rightExpression;

  protected AbstractBinaryArithmeticExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    this.leftExpression = Objects.requireNonNull(leftExpression, "leftExpression");
    this.rightExpression = Objects.requireNonNull(rightExpression, "rightExpression");
  }

  public ArithmeticExpression leftExpression() {
    return leftExpression;
  }

  public ArithmeticExpression rightExpression() {
    return rightExpression;
  }

  @SuppressWarnings("unchecked")
  protected abstract static sealed class Builder<
          T extends AbstractBinaryArithmeticExpression, B extends Builder<T, B>>
      permits AdditionExpression.Builder,
          DivisionExpression.Builder,
          MultiplicationExpression.Builder,
          SubtractionExpression.Builder {
    protected ArithmeticExpression leftExpression;
    protected ArithmeticExpression rightExpression;

    protected Builder() {}

    public abstract T build();

    public ArithmeticExpression leftExpression() {
      return leftExpression;
    }

    public B leftExpression(ArithmeticExpression leftExpression) {
      this.leftExpression = leftExpression;
      return (B) this;
    }

    public ArithmeticExpression rightExpression() {
      return rightExpression;
    }

    public B rightExpression(ArithmeticExpression rightExpression) {
      this.rightExpression = rightExpression;
      return (B) this;
    }
  }
}
