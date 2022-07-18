package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class Comment extends AbstractNode<Comment, Comment.Builder> {
  private final String comment;

  private Comment(SourceLocation sourceLocation, String comment) {
    super(sourceLocation);
    this.comment = Objects.requireNonNull(comment, "comment");
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitComment(this, context);
  }

  @Override
  public Comment transform(Consumer<Builder> builderConsumer) {
    final Builder b = new Builder(sourceLocation(), comment);
    builderConsumer.accept(b);
    return b.build();
  }

  public static Builder newComment() {
    return new Builder();
  }

  public static final class Builder extends AbstractNode.Builder<Comment, Builder> {
    private String comment;

    private Builder() {}

    private Builder(SourceLocation sourceLocation, String comment) {
      super(sourceLocation);
      this.comment = comment;
    }

    @Override
    public Comment build() {
      return new Comment(sourceLocation(), comment);
    }

    public String comment() {
      return comment;
    }

    public Builder comment(String comment) {
      this.comment = comment;
      return this;
    }
  }
}
