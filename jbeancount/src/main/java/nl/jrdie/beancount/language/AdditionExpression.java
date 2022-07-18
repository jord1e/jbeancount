package nl.jrdie.beancount.language;

public final class AdditionExpression extends AbstractBinaryArithmeticExpression {

  private AdditionExpression(
      ArithmeticExpression leftExpression, ArithmeticExpression rightExpression) {
    super(leftExpression, rightExpression);
  }

  public static Builder newAdditionExpression() {
    return new Builder();
  }

  public static final class Builder
      extends AbstractBinaryArithmeticExpression.Builder<AdditionExpression, Builder> {

    private Builder() {
      super();
    }

    @Override
    public AdditionExpression build() {
      return new AdditionExpression(leftExpression, rightExpression);
    }
  }
}
