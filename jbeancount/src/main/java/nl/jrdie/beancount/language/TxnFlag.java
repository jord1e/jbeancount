package nl.jrdie.beancount.language;

public final class TxnFlag implements Flag {

  private TxnFlag() {}

  @Override
  public String flag() {
    return "txn";
  }

  @Override
  public boolean star() {
    return false;
  }

  @Override
  public boolean exclamationMark() {
    return false;
  }

  @Override
  public boolean txn() {
    return true;
  }

  public static Builder newTxnFlag() {
    return new Builder();
  }

  public static final class Builder {
    private Builder() {}

    public TxnFlag build() {
      return new TxnFlag();
    }
  }
}
