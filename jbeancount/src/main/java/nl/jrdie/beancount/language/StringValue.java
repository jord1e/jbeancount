package nl.jrdie.beancount.language;

import java.util.Objects;

public final class StringValue implements CostCompValue, ScalarValue {

  private final String value;

  private StringValue(String value) {
    this.value = Objects.requireNonNull(value, "value");
  }

  public String value() {
    return value;
  }

  public static Builder newStringValue() {
    return new Builder();
  }

  public static final class Builder {
    private String value;

    private Builder() {}

    public StringValue build() {
      return new StringValue(value);
    }

    public String value() {
      return value;
    }

    public Builder value(String value) {
      this.value = value;
      return this;
    }
  }
}
