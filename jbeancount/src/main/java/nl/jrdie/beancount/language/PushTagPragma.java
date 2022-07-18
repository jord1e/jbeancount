package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class PushTagPragma extends AbstractPragmaNode<PushTagPragma, PushTagPragma.Builder> {
  PushTagPragma(SourceLocation sourceLocation) {
    super(sourceLocation);
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return null;
  }

  @Override
  public PushTagPragma transform(Consumer<Builder> builderConsumer) {
    return null;
  }

  public static final class Builder extends AbstractPragmaNode.Builder<PushTagPragma, Builder> {
    @Override
    public PushTagPragma build() {
      return null;
    }
  }
}
