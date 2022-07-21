package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.annotation.Beta;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class OpenDirective
    extends AbstractDirectiveNode<OpenDirective, OpenDirective.Builder> {
  private final Account account;
  private final List<Commodity> commodities;
  @Beta private final String bookingMethod;

  private OpenDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Account account,
      List<Commodity> commodities,
      @Beta String bookingMethod,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.account = Objects.requireNonNull(account, "account");
    this.commodities = Objects.requireNonNull(commodities, "commodities");
    this.bookingMethod = bookingMethod;
  }

  public Account account() {
    return account;
  }

  public List<Commodity> commodities() {
    return commodities;
  }

  @Beta
  public String bookingMethod() {
    return bookingMethod;
  }

  public static Builder newOpenDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitOpenDirective(this, context);
  }

  @Override
  public OpenDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(
            sourceLocation(),
            date(),
            tagsAndLinks(),
            metadata(),
            account,
            commodities,
            bookingMethod,
            comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<OpenDirective, Builder> {
    private Account account;
    private List<Commodity> commodities;
    @Beta private String bookingMethod;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        Account account,
        List<Commodity> commodities,
        String bookingMethod,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.account = account;
      this.commodities = commodities;
      this.bookingMethod = bookingMethod;
    }

    @Override
    public OpenDirective build() {
      return new OpenDirective(
          sourceLocation(),
          date(),
          tagsAndLinks(),
          account,
          commodities,
          bookingMethod,
          metadata(),
          comment());
    }

    public Account account() {
      return account;
    }

    public Builder account(Account account) {
      this.account = account;
      return this;
    }

    public List<Commodity> commodities() {
      return commodities;
    }

    public Builder commodities(List<Commodity> commodities) {
      this.commodities = commodities;
      return this;
    }

    @Beta
    public String bookingMethod() {
      return bookingMethod;
    }

    @Beta
    public Builder bookingMethod(String bookingMethod) {
      this.bookingMethod = bookingMethod;
      return this;
    }
  }
}
