package nl.jrdie.beancount.language;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class Metadata {

  private final List<MetadataLine> metadata;

  private Metadata(List<MetadataLine> metadata) {
    this.metadata = Collections.unmodifiableList(Objects.requireNonNull(metadata, "metadata"));
  }

  public List<MetadataLine> metadata() {
    return metadata;
  }

  public static Builder newMetadata() {
    return new Builder();
  }

  public Metadata transform(Consumer<Builder> builderConsumer) {
    final Builder b = new Builder(metadata);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder {
    private List<MetadataLine> metadata;

    private Builder() {}

    private Builder(List<MetadataLine> metadata) {
      this.metadata = metadata;
    }

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
