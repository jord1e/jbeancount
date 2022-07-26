package nl.jrdie.beancount.language;

import org.jetbrains.annotations.Nullable;

abstract sealed class AbstractPragmaNode<
        T extends Node<T, B>, B extends AbstractPragmaNode.Builder<T, B>>
    extends AbstractNode<T, B> implements Node<T, B>, PragmaNode<T, B>, JournalDeclaration<T, B>
    permits IncludePragma, OptionPragma, PluginPragma, PopTagPragma, PushTagPragma {

  private final Comment comment;

  protected AbstractPragmaNode(SourceLocation sourceLocation, Comment comment) {
    super(sourceLocation);
    this.comment = comment;
  }

  @Nullable
  @Override
  public Comment comment() {
    return comment;
  }

  abstract static sealed class Builder<
          T extends Node<T, B>, B extends AbstractPragmaNode.Builder<T, B>>
      extends AbstractNode.Builder<T, B>
      permits IncludePragma.Builder,
          OptionPragma.Builder,
          PluginPragma.Builder,
          PopTagPragma.Builder,
          PushTagPragma.Builder {
    private Comment comment;

    Builder() {}

    Builder(SourceLocation sourceLocation, Comment comment) {
      super(sourceLocation);
    }

    public Comment comment() {
      return comment;
    }

    public Builder<T, B> comment(Comment comment) {
      this.comment = comment;
      return this;
    }
  }
}
