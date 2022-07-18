package nl.jrdie.beancount.language;

public final class NilValue implements ScalarValue {

  private NilValue() {}

  @Override
  public boolean empty() {
    return true;
  }

  public static Builder newNilValue() {
    return new Builder();
  }

  public static final class Builder {
    private Builder() {}

    public NilValue build() {
      return new NilValue();
    }
  }
}
