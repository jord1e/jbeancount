package nl.jrdie.beancount.language;

import java.util.Objects;

public final class Account implements ScalarValue {

  private final String account;

  private Account(String account) {
    this.account = Objects.requireNonNull(account, "account");
  }

  public String account() {
    return account;
  }

  public static Builder newAccount() {
    return new Builder();
  }

  public static final class Builder {
    private String account;

    private Builder() {}

    public Account build() {
      return new Account(account);
    }

    public String account() {
      return account;
    }

    public Builder account(String account) {
      this.account = account;
      return this;
    }
  }
}
