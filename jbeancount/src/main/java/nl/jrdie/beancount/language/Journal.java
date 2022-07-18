package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;
import nl.jrdie.beancount.language.tools.internal.NodeChildrenContainer;

public final class Journal extends AbstractNode<Journal, Journal.Builder> {

  public static final String CHILDREN_DECLARATIONS = "declarations";

  private final List<JournalDeclaration<?, ?>> declarations;

  private Journal(SourceLocation sourceLocation, List<JournalDeclaration<?, ?>> declarations) {
    super(sourceLocation);
    this.declarations = Objects.requireNonNull(declarations, "declarations");
  }

  public List<JournalDeclaration<?, ?>> declarations() {
    return declarations;
  }

  public static Builder newJournal() {
    return new Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitJournal(this, context);
  }

  @Override
  public Journal transform(Consumer<Builder> builderConsumer) {
    final Builder b = new Builder(sourceLocation(), declarations);
    builderConsumer.accept(b);
    return b.build();
  }

  @Override
  public NodeChildrenContainer getNamedChildren() {
    return NodeChildrenContainer.newNodeChildrenContainer()
        .children(CHILDREN_DECLARATIONS, declarations)
        .build();
  }

  @Override
  public Journal withNewChildren(NodeChildrenContainer newChildren) {
    return transform(
        builder -> builder.declarations(newChildren.getChildren(CHILDREN_DECLARATIONS)));
  }

  public static final class Builder extends AbstractNode.Builder<Journal, Builder> {
    private List<JournalDeclaration<?, ?>> declarations;

    private Builder() {}

    private Builder(SourceLocation sourceLocation, List<JournalDeclaration<?, ?>> declarations) {
      super(sourceLocation);
      this.declarations = declarations;
    }

    @Override
    public Journal build() {
      return new Journal(sourceLocation(), declarations);
    }

    public List<JournalDeclaration<?, ?>> declarations() {
      return declarations;
    }

    public Builder declarations(List<JournalDeclaration<?, ?>> declarations) {
      this.declarations = declarations;
      return this;
    }
  }
}
