package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

// TODO - This is only used for double(+) consecutive newlines - should it really be here?
public final class EolNode extends AbstractNode<EolNode, EolNode.Builder>
    implements JournalDeclaration<EolNode, EolNode.Builder> {

  private EolNode(SourceLocation sourceLocation) {
    super(sourceLocation);
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return TraversalControl.CONTINUE;
  }

  @Override
  public EolNode transform(Consumer<Builder> builderConsumer) {
    final Builder b = new Builder(sourceLocation());
    builderConsumer.accept(b);
    return b.build();
  }

  public static Builder newEolNode() {
    return new Builder();
  }

  public static final class Builder extends AbstractNode.Builder<EolNode, Builder> {
    private Builder() {}

    private Builder(SourceLocation sourceLocation) {
      super(sourceLocation);
    }

    @Override
    public EolNode build() {
      return new EolNode(sourceLocation());
    }
  }
}
