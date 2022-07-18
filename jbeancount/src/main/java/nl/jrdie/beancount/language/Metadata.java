package nl.jrdie.beancount.language;

import java.util.List;
import java.util.Objects;

public final class Metadata {

  private final List<MetadataLine> metadata;

  private Metadata(List<MetadataLine> metadata) {
    this.metadata = Objects.requireNonNull(metadata, "metadata");
  }

  public List<MetadataLine> metadata() {
    return metadata;
  }

  public static Builder newMetadata() {
    return new Builder();
  }

  public static final class Builder {
    private List<MetadataLine> metadata;

    private Builder() {}

    public Metadata build() {
      return new Metadata(metadata);
    }

    public List<MetadataLine> metadata() {
      return metadata;
    }

    public Builder metadata(List<MetadataLine> metadata) {
      this.metadata = metadata;
      return this;
    }
  }
}
