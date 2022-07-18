package nl.jrdie.beancount.language;

import java.math.BigDecimal;
import java.util.Objects;

public final class Amount implements MetadataValue {

  private final ArithmeticExpression expression;
  private final Commodity commodity;
  private final BigDecimal tolerance;

  private Amount(ArithmeticExpression expression, Commodity commodity, BigDecimal tolerance) {
    this.expression = Objects.requireNonNull(expression, "expression");
    this.commodity = Objects.requireNonNull(commodity, "commodity");
    this.tolerance = tolerance;
  }

  public ArithmeticExpression expression() {
    return expression;
  }

  public Commodity commodity() {
    return commodity;
  }

  public BigDecimal tolerance() {
    return tolerance;
  }

  public static Builder newAmount() {
    return new Builder();
  }

  public static final class Builder {
    private ArithmeticExpression expression;
    private Commodity commodity;
    private BigDecimal tolerance;

    private Builder() {}

    public Amount build() {
      return new Amount(expression, commodity, tolerance);
    }

    public ArithmeticExpression expression() {
      return expression;
    }

    public Builder expression(ArithmeticExpression expression) {
      this.expression = expression;
      return this;
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }

    public BigDecimal tolerance() {
      return tolerance;
    }

    public Builder tolerance(BigDecimal tolerance) {
      this.tolerance = tolerance;
      return this;
    }
  }
}
