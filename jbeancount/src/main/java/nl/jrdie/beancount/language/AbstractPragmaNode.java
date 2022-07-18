package nl.jrdie.beancount.language;

abstract sealed class AbstractPragmaNode<
        T extends Node<T, B>, B extends AbstractPragmaNode.Builder<T, B>>
    extends AbstractNode<T, B> implements Node<T, B>, PragmaNode<T, B>, JournalDeclaration<T, B>
    permits IncludePragma, OptionPragma, PluginPragma, PopTagPragma, PushTagPragma {

  protected AbstractPragmaNode(SourceLocation sourceLocation) {
    super(sourceLocation);
  }

  public abstract static sealed class Builder<
          T extends Node<T, B>, B extends AbstractPragmaNode.Builder<T, B>>
      extends AbstractNode.Builder<T, B>
      permits IncludePragma.Builder,
          OptionPragma.Builder,
          PluginPragma.Builder,
          PopTagPragma.Builder,
          PushTagPragma.Builder {
    Builder() {}

    Builder(SourceLocation sourceLocation) {
      super(sourceLocation);
    }
  }
}
