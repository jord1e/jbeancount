package nl.jrdie.beancount.language;

import org.jetbrains.annotations.Nullable;

public sealed interface WithComment permits DirectiveNode, Posting, PragmaNode {

  @Nullable
  Comment comment();
}
