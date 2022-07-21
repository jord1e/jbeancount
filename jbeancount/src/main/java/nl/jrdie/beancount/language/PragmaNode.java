package nl.jrdie.beancount.language;

public sealed interface PragmaNode<T extends Node<T, B>, B extends Node.Builder<T, B>>
    extends Node<T, B>, JournalDeclaration<T, B>, WithComment permits AbstractPragmaNode {}
