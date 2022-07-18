package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class IncludePragma extends AbstractPragmaNode<IncludePragma, IncludePragma.Builder> {
  private final String filename;
  private final Journal journal;

  private IncludePragma(SourceLocation sourceLocation, String filename, Journal journal) {
    super(sourceLocation);
    this.filename = Objects.requireNonNull(filename, "filename");
    this.journal = journal;
  }

  public String filename() {
    return filename;
  }

  public Journal journal() {
    return journal;
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitIncludePragma(this, context);
  }

  @Override
  public IncludePragma transform(Consumer<Builder> builderConsumer) {
    Builder b = new Builder(sourceLocation(), filename, journal);
    builderConsumer.accept(b);
    return b.build();
  }

  public static Builder newIncludePragma() {
    return new Builder();
  }

  public static final class Builder extends AbstractPragmaNode.Builder<IncludePragma, Builder> {
    private String filename;
    private Journal journal;

    private Builder() {}

    private Builder(SourceLocation sourceLocation, String filename, Journal journal) {
      super(sourceLocation);
      this.filename = filename;
      this.journal = journal;
    }

    @Override
    public IncludePragma build() {
      return new IncludePragma(sourceLocation(), filename, journal);
    }

    public String filename() {
      return filename;
    }

    public Builder filename(String filename) {
      this.filename = filename;
      return this;
    }

    public Journal journal() {
      return journal;
    }

    public Builder journal(Journal journal) {
      this.journal = journal;
      return this;
    }
  }
}
