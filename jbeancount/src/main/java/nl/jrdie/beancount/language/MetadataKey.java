package nl.jrdie.beancount.language;

import java.util.Objects;

public final class MetadataKey {

  private final String key;

  private MetadataKey(String key) {
    this.key = Objects.requireNonNull(key, "key");
  }

  public String key() {
    return key;
  }

  public static Builder newMetadataKey() {
    return new Builder();
  }

  public static final class Builder {
    private String key;

    private Builder() {}

    public MetadataKey build() {
      return new MetadataKey(key);
    }

    public String key() {
      return key;
    }

    public Builder key(String key) {
      this.key = key;
      return this;
    }
  }
}
