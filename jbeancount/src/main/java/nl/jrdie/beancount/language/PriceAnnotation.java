package nl.jrdie.beancount.language;

public final class PriceAnnotation {

  private final ArithmeticExpression priceExpression;
  private final Commodity commodity;
  private final boolean totalCost;

  private PriceAnnotation(
      ArithmeticExpression priceExpression, Commodity commodity, boolean totalCost) {
    this.priceExpression = priceExpression;
    this.commodity = commodity;
    this.totalCost = totalCost;
  }

  public ArithmeticExpression priceExpression() {
    return priceExpression;
  }

  public Commodity commodity() {
    return commodity;
  }

  public boolean totalCost() {
    return totalCost;
  }

  public static Builder newPriceAnnotation() {
    return new Builder();
  }

  public static final class Builder {
    private ArithmeticExpression priceExpression;
    private Commodity commodity;
    private boolean totalCost;

    private Builder() {}

    public PriceAnnotation build() {
      return new PriceAnnotation(priceExpression, commodity, totalCost);
    }

    public ArithmeticExpression priceExpression() {
      return priceExpression;
    }

    public Builder priceExpression(ArithmeticExpression priceExpression) {
      this.priceExpression = priceExpression;
      return this;
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }

    public boolean totalCost() {
      return totalCost;
    }

    public Builder totalCost(boolean totalCost) {
      this.totalCost = totalCost;
      return this;
    }
  }
}
