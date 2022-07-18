package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;
import nl.jrdie.beancount.language.tools.internal.NodeChildrenContainer;

public sealed interface Node<T extends Node<T, B>, B extends Node.Builder<T, B>>
    permits AbstractDirectiveNode,
        AbstractNode,
        AbstractPragmaNode,
        DirectiveNode,
        JournalDeclaration,
        PragmaNode {

  SourceLocation sourceLocation();

  TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor);

  T transform(Consumer<B> builderConsumer);

  NodeChildrenContainer getNamedChildren();

  T withNewChildren(NodeChildrenContainer newChildren);

  interface Builder<T extends Node<T, B>, B extends Node.Builder<T, B>> {
    T build();
  }
}
