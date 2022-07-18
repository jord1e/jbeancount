package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class PriceDirective
    extends AbstractDirectiveNode<PriceDirective, PriceDirective.Builder> {
  private final Commodity commodity;
  private final Amount price;

  private PriceDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Commodity commodity,
      Amount price,
      Metadata metadata) {
    super(sourceLocation, date, tagsAndLinks, metadata);
    this.commodity = commodity;
    this.price = price;
  }

  public Commodity commodity() {
    return commodity;
  }

  public Amount price() {
    return price;
  }

  public static Builder newPriceDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitPriceDirective(this, context);
  }

  @Override
  public PriceDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), commodity, price);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<PriceDirective, Builder> {
    private Commodity commodity;
    private Amount price;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Commodity commodity,
        Amount price) {
      super(sourceLocation, date, tagsAndLinks, metadata);
      this.commodity = commodity;
      this.price = price;
    }

    @Override
    public PriceDirective build() {
      return new PriceDirective(
          sourceLocation(), date(), tagsAndLinks(), commodity, price, metadata());
    }

    public Commodity commodity() {
      return commodity;
    }

    public Builder commodity(Commodity commodity) {
      this.commodity = commodity;
      return this;
    }

    public Amount price() {
      return price;
    }

    public Builder price(Amount price) {
      this.price = price;
      return this;
    }
  }
}
