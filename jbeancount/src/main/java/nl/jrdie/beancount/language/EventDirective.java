package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class EventDirective
    extends AbstractDirectiveNode<EventDirective, EventDirective.Builder> {

  private final String type;
  private final String description;

  private EventDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      String type,
      String description,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.type = Objects.requireNonNull(type, "type");
    this.description = Objects.requireNonNull(description, "description");
  }

  public String type() {
    return type;
  }

  public String description() {
    return description;
  }

  public static EventDirective.Builder newEventDirective() {
    return new EventDirective.Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitEventDirective(this, context);
  }

  @Override
  public EventDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(
            sourceLocation(), date(), tagsAndLinks(), metadata(), type, description, comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<EventDirective, Builder> {
    private String type;
    private String description;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String type,
        String description,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.type = type;
      this.description = description;
    }

    @Override
    public EventDirective build() {
      return new EventDirective(
          sourceLocation(), date(), tagsAndLinks(), type, description, metadata(), comment());
    }

    public String type() {
      return type;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public String description() {
      return description;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }
  }
}
