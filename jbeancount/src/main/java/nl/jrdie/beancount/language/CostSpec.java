package nl.jrdie.beancount.language;

import java.util.List;
import java.util.Objects;

public final class CostSpec {

  private final List<CostCompValue> components;
  private final boolean doubleBraces;

  private CostSpec(List<CostCompValue> components, boolean doubleBraces) {
    this.components = Objects.requireNonNull(components, "components");
    this.doubleBraces = doubleBraces;
  }

  public boolean doubleBraces() {
    return doubleBraces;
  }

  public List<CostCompValue> components() {
    return components;
  }

  public static Builder newCostSpec() {
    return new Builder();
  }

  public static final class Builder {
    private List<CostCompValue> components;
    private boolean doubleBraces;

    private Builder() {}

    public CostSpec build() {
      return new CostSpec(components, doubleBraces);
    }

    public List<CostCompValue> components() {
      return components;
    }

    public Builder components(List<CostCompValue> components) {
      this.components = components;
      return this;
    }

    public boolean doubleBraces() {
      return doubleBraces;
    }

    public Builder doubleBraces(boolean doubleBraces) {
      this.doubleBraces = doubleBraces;
      return this;
    }
  }
}
