package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class CommodityDirective
    extends AbstractDirectiveNode<CommodityDirective, CommodityDirective.Builder> {
  private final Commodity commodity;

  private CommodityDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Commodity commodity,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.commodity = Objects.requireNonNull(commodity, "commodity");
  }

  public Commodity commodity() {
    return commodity;
  }

  public static Builder newCommodityDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitCommodityDirective(this, context);
  }

  @Override
  public CommodityDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), commodity, comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder
      extends AbstractDirectiveNode.Builder<CommodityDirective, Builder> {
    private Commodity commodity;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Commodity commodity,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.commodity = commodity;
    }

    public CommodityDirective build() {
      return new CommodityDirective(
          sourceLocation(), date(), tagsAndLinks(), commodity, metadata(), comment());
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }
  }
}
