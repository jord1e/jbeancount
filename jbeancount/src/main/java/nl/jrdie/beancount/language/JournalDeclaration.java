package nl.jrdie.beancount.language;

public sealed interface JournalDeclaration<T extends Node<T, B>, B extends Node.Builder<T, B>>
    extends Node<T, B>
    permits AbstractDirectiveNode, AbstractPragmaNode, DirectiveNode, EolNode, PragmaNode {}
