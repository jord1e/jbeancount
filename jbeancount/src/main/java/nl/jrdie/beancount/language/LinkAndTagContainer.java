package nl.jrdie.beancount.language;

import java.util.Collection;
import java.util.List;
import nl.jrdie.beancount.annotation.Beta;

public sealed interface LinkAndTagContainer permits AbstractDirectiveNode, DirectiveNode {

  // Ordered
  @Beta("Naming uncertain")
  List<TagOrLink> tagsAndLinks();

  Collection<Link> links();

  Collection<Tag> tags();
}
