package nl.jrdie.beancount.language;

public final class BooleanValue implements ScalarValue {

  private final boolean value;

  private BooleanValue(boolean value) {
    this.value = value;
  }

  public boolean value() {
    return value;
  }

  public static Builder newBooleanValue() {
    return new Builder();
  }

  public static final class Builder {
    private boolean value;

    private Builder() {}

    public BooleanValue build() {
      return new BooleanValue(value);
    }

    public boolean value() {
      return value;
    }

    public Builder value(boolean value) {
      this.value = value;
      return this;
    }
  }
}
