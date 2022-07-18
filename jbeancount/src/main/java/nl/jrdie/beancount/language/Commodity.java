package nl.jrdie.beancount.language;

import java.util.Objects;

public final class Commodity implements ScalarValue {

  private final String commodity;

  private Commodity(String commodity) {
    this.commodity = Objects.requireNonNull(commodity, "commodity");
  }

  public String commodity() {
    return commodity;
  }

  public static Builder newCommodity() {
    return new Builder();
  }

  public static final class Builder {
    private String commodity;

    private Builder() {}

    public Commodity build() {
      return new Commodity(commodity);
    }

    public String commodity() {
      return commodity;
    }

    public Builder commodity(String commodity) {
      this.commodity = commodity;
      return this;
    }
  }
}
