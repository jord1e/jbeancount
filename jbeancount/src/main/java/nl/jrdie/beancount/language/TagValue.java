package nl.jrdie.beancount.language;

import java.util.Objects;

public final class TagValue implements Tag, ScalarValue {

  private final String tag;

  private TagValue(String tag) {
    this.tag = Objects.requireNonNull(tag, "tag");
  }

  @Override
  public String tag() {
    return tag;
  }

  public static Builder newTagValue() {
    return new Builder();
  }

  public static final class Builder {
    private String tag;

    private Builder() {}

    public TagValue build() {
      return new TagValue(tag);
    }

    public String tag() {
      return tag;
    }

    public Builder tag(String tag) {
      this.tag = tag;
      return this;
    }
  }
}
