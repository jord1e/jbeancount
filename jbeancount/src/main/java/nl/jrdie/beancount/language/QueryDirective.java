package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class QueryDirective
    extends AbstractDirectiveNode<QueryDirective, QueryDirective.Builder> {
  private final String name;
  private final String sql;

  public QueryDirective(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      String name,
      String sql,
      Metadata metadata,
      Comment comment) {
    super(sourceLocation, date, tagsAndLinks, metadata, comment);
    this.name = Objects.requireNonNull(name, "name");
    this.sql = Objects.requireNonNull(sql, "sql");
  }

  public String name() {
    return name;
  }

  public String sql() {
    return sql;
  }

  public static Builder newQueryDirective() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitQueryDirective(this, context);
  }

  @Override
  public QueryDirective transform(Consumer<Builder> builderConsumer) {
    final Builder b =
        new Builder(sourceLocation(), date(), tagsAndLinks(), metadata(), name, sql, comment());
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractDirectiveNode.Builder<QueryDirective, Builder> {
    private String name;
    private String sql;

    private Builder() {}

    private Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata,
        String name,
        String sql,
        Comment comment) {
      super(sourceLocation, date, tagsAndLinks, metadata, comment);
      this.name = name;
      this.sql = sql;
    }

    @Override
    public QueryDirective build() {
      return new QueryDirective(
          sourceLocation(), date(), tagsAndLinks(), name, sql, metadata(), comment());
    }

    public String name() {
      return name;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public String sql() {
      return sql;
    }

    public Builder sql(String sql) {
      this.sql = sql;
      return this;
    }
  }
}
