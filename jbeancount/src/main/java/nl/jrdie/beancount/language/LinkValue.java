package nl.jrdie.beancount.language;

import java.util.Objects;

public final class LinkValue implements Link, ScalarValue {

  private final String link;

  private LinkValue(String link) {
    this.link = Objects.requireNonNull(link, "link");
  }

  @Override
  public String link() {
    return link;
  }

  public static Builder newLinkValue() {
    return new Builder();
  }

  public static final class Builder {
    private String link;

    private Builder() {}

    public LinkValue build() {
      return new LinkValue(link);
    }

    public String link() {
      return link;
    }

    public Builder link(String link) {
      this.link = link;
      return this;
    }
  }
}
