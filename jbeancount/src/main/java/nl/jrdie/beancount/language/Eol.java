package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

// TODO - This is only used for double(+) consecutive newlines - should it really be here?
public final class Eol extends AbstractNode<Eol, Eol.Builder>
    implements JournalDeclaration<Eol, Eol.Builder> {

  private Eol(SourceLocation sourceLocation) {
    super(sourceLocation);
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public Eol transform(Consumer<Builder> builderConsumer) {
    final Builder b = new Builder(sourceLocation());
    builderConsumer.accept(b);
    return b.build();
  }

  public static Builder newEol() {
    return new Builder();
  }

  public static final class Builder extends AbstractNode.Builder<Eol, Builder> {
    private Builder() {}

    private Builder(SourceLocation sourceLocation) {
      super(sourceLocation);
    }

    @Override
    public Eol build() {
      return new Eol(sourceLocation());
    }
  }
}
