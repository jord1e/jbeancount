package nl.jrdie.beancount.language;

import graphql.util.TraversalControl;
import graphql.util.TraverserContext;
import java.util.Objects;
import java.util.function.Consumer;
import nl.jrdie.beancount.language.tools.NodeVisitor;

public final class PluginPragma extends AbstractPragmaNode<PluginPragma, PluginPragma.Builder> {
  private final String name;
  private final String config;

  private PluginPragma(SourceLocation sourceLocation, String name, String config) {
    super(sourceLocation);
    this.name = Objects.requireNonNull(name, "name");
    this.config = config;
  }

  public String name() {
    return name;
  }

  public String config() {
    return config;
  }

  public static PluginPragma.Builder newPluginPragma() {
    return new PluginPragma.Builder();
  }

  @Override
  public TraversalControl accept(TraverserContext<Node<?, ?>> context, NodeVisitor visitor) {
    return visitor.visitPluginPragma(this, context);
  }

  @Override
  public PluginPragma transform(Consumer<Builder> builderConsumer) {
    Builder b = new Builder(sourceLocation(), name, config);
    builderConsumer.accept(b);
    return b.build();
  }

  public static final class Builder extends AbstractPragmaNode.Builder<PluginPragma, Builder> {
    private String name;
    private String config;

    private Builder() {}

    private Builder(SourceLocation sourceLocation, String name, String config) {
      super(sourceLocation);
      this.name = name;
      this.config = config;
    }

    @Override
    public PluginPragma build() {
      return new PluginPragma(sourceLocation(), name, config);
    }

    public String name() {
      return name;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public String config() {
      return config;
    }

    public Builder config(String config) {
      this.config = config;
      return this;
    }
  }
}
