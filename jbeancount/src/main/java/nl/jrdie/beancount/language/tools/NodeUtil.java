package nl.jrdie.beancount.language.tools;

import static graphql.util.FpKit.mergeFirst;

import graphql.language.NamedNode;
import graphql.util.FpKit;
import graphql.util.NodeLocation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import nl.jrdie.beancount.language.Node;
import nl.jrdie.beancount.language.tools.internal.NodeChildrenContainer;

public class NodeUtil {

  public static <T extends NamedNode<T>> T findNodeByName(List<T> namedNodes, String name) {
    for (T namedNode : namedNodes) {
      if (Objects.equals(namedNode.getName(), name)) {
        return namedNode;
      }
    }
    return null;
  }

  public static <T extends NamedNode<T>> Map<String, T> nodeByName(List<T> nameNode) {
    return FpKit.getByName(nameNode, NamedNode::getName, mergeFirst());
  }

  public static void assertNewChildrenAreEmpty(NodeChildrenContainer newChildren) {
    if (!newChildren.isEmpty()) {
      throw new IllegalArgumentException(
          "Cannot pass non-empty newChildren to Node that doesn't hold children");
    }
  }

  public static Node<?, ?> removeChild(Node<?, ?> node, NodeLocation childLocationToRemove) {
    NodeChildrenContainer namedChildren = node.getNamedChildren();
    NodeChildrenContainer newChildren =
        namedChildren.transform(
            builder ->
                builder.removeChild(
                    childLocationToRemove.getName(), childLocationToRemove.getIndex()));
    return node.withNewChildren(newChildren);
  }
}
