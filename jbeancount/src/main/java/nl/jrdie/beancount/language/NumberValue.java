package nl.jrdie.beancount.language;

import java.math.BigDecimal;
import java.util.Objects;

public final class NumberValue {

  private final BigDecimal value;

  private NumberValue(BigDecimal value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public BigDecimal value() {
    return value;
  }

  public static Builder newNumberValue() {
    return new Builder();
  }

  public static final class Builder {
    private BigDecimal value;

    private Builder() {}

    public NumberValue build() {
      return new NumberValue(value);
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
