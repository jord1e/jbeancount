package nl.jrdie.beancount.language.tools;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import graphql.util.TraverserVisitor;
import graphql.util.TreeTransformer;
import java.util.Objects;
import nl.jrdie.beancount.language.Node;
import nl.jrdie.beancount.language.tools.internal.AstNodeAdapter;

public final class AstTransformer {

  private AstTransformer() {}

  public static Node<?, ?> transform(Node<?, ?> root, NodeVisitor nodeVisitor) {
    Objects.requireNonNull(root, "root");
    Objects.requireNonNull(nodeVisitor, "nodeVisitor");

    TraverserVisitor<Node<?, ?>> traverserVisitor = getNodeTraverserVisitor(nodeVisitor);
    TreeTransformer<Node<?, ?>> treeTransformer =
        new TreeTransformer<>(AstNodeAdapter.AST_NODE_ADAPTER);
    return treeTransformer.transform(root, traverserVisitor);
  }

  private static TraverserVisitor<Node<?, ?>> getNodeTraverserVisitor(NodeVisitor nodeVisitor) {
    return new TraverserVisitor<>() {
      @Override
      public TraversalControl enter(graphql.util.TraverserContext<Node<?, ?>> context) {
        return context.thisNode().accept(context, nodeVisitor);
      }

      @Override
      public TraversalControl leave(TraverserContext<Node<?, ?>> context) {
        return TraversalControl.CONTINUE;
      }
    };
  }
}
