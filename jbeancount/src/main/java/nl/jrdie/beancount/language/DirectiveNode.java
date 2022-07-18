package nl.jrdie.beancount.language;

import java.time.LocalDate;

public sealed interface DirectiveNode<T extends Node<T, B>, B extends Node.Builder<T, B>>
    extends Node<T, B>, JournalDeclaration<T, B>, LinkAndTagContainer
    permits AbstractDirectiveNode {

  LocalDate date();

  Metadata metadata();
}
