package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;
import nl.jrdie.beancount.util.ImmutableKit;

public final class CustomDirective
    extends AbstractDirectiveNode<CustomDirective, CustomDirective.Builder> {

  private final String name;
  private final List<ScalarValue> values;

  private CustomDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      String name,
      List<ScalarValue> values,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, ImmutableKit.emptyList(), metadata, comment);
    this.name = Objects.requireNonNull(name, "name");
    this.values = Objects.requireNonNull(values, "values");
  }

  @Override
  public List<TagOrLink> tagsAndLinks() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Link> links() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<Tag> tags() {
    throw new UnsupportedOperationException();
  }

  public String name() {
    return name;
  }

  public List<ScalarValue> values() {
    return values;
  }

  public static Builder newCustomDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitCustomDirective(this, context);
  }

  @Override
  public CustomDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), name, values, comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder
      extends AbstractDirectiveNode.Builder<CustomDirective, Builder> {
    private String name;
    private List<ScalarValue> values;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String name,
        List<ScalarValue> values,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.name = name;
      this.values = values;
    }

    @Override
    public CustomDirective build() {
      return new CustomDirective(sourceLocation(), date(), name, values, metadata(), comment());
    }

    public String name() {
      return name;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public List<ScalarValue> values() {
      return values;
    }

    public Builder values(List<ScalarValue> values) {
      this.values = values;
      return this;
    }
  }
}
