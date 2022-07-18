package nl.jrdie.beancount.language;

import java.util.Objects;

public final class MetadataItem implements MetadataLine {

  private final MetadataKey key;
  private final MetadataValue value;

  private MetadataItem(MetadataKey key, MetadataValue value) {
    this.key = Objects.requireNonNull(key, "key");
    this.value = value;
  }

  public MetadataKey key() {
    return key;
  }

  public MetadataValue value() {
    return value;
  }

  public static Builder newMetadataItem() {
    return new Builder();
  }

  public static final class Builder {
    private MetadataKey key;
    private MetadataValue value;

    private Builder() {}

    public MetadataItem build() {
      return new MetadataItem(key, value);
    }

    public MetadataKey key() {
      return key;
    }

    public Builder key(MetadataKey key) {
      this.key = key;
      return this;
    }

    public MetadataValue value() {
      return value;
    }

    public Builder value(MetadataValue value) {
      this.value = value;
      return this;
    }
  }
}
