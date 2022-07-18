package nl.jrdie.beancount.language;

import java.math.BigDecimal;
import java.util.Objects;

public final class ConstantExpression implements ArithmeticExpression {

  private final BigDecimal value;

  private ConstantExpression(BigDecimal value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public BigDecimal value() {
    return value;
  }

  public static Builder newConstantExpression() {
    return new Builder();
  }

  public static final class Builder {
    private BigDecimal value;

    private Builder() {}

    public ConstantExpression build() {
      return new ConstantExpression(value);
    }

    public BigDecimal value() {
      return value;
    }

    public Builder value(BigDecimal value) {
      this.value = value;
      return this;
    }
  }
}
