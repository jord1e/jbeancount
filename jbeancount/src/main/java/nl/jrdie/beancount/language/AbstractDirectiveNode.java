package nl.jrdie.beancount.language;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

abstract sealed class AbstractDirectiveNode<
        T extends Node<T, B>, B extends AbstractDirectiveNode.Builder<T, B>>
    extends AbstractNode<T, B>
    implements Node<T, B>, DirectiveNode<T, B>, JournalDeclaration<T, B>, LinkAndTagContainer
    permits BalanceDirective,
        CloseDirective,
        CommodityDirective,
        CustomDirective,
        DocumentDirective,
        EventDirective,
        NoteDirective,
        OpenDirective,
        PadDirective,
        PriceDirective,
        QueryDirective,
        TransactionDirective {

  private final LocalDate date;
  private final List<TagOrLink> tagsAndLinks;
  private final Metadata metadata;
  private Collection<Link> links;
  private Collection<Tag> tags;

  AbstractDirectiveNode(
      SourceLocation sourceLocation,
      LocalDate date,
      List<TagOrLink> tagsAndLinks,
      Metadata metadata) {
    super(sourceLocation);
    this.date = Objects.requireNonNull(date, "date");
    this.tagsAndLinks = Objects.requireNonNull(tagsAndLinks, "tagsAndLinks");
    this.metadata = Objects.requireNonNull(metadata, "metadata");
  }

  @Override
  public LocalDate date() {
    return date;
  }

  @Override
  public Metadata metadata() {
    return metadata;
  }

  @Override
  public List<TagOrLink> tagsAndLinks() {
    return tagsAndLinks;
  }

  @Override
  public Collection<Link> links() {
    if (this.links != null) {
      return this.links;
    }
    List<Link> links = new ArrayList<>();
    for (TagOrLink i : tagsAndLinks) {
      if (i instanceof Link link) {
        links.add(link);
      }
    }
    return this.links = Collections.unmodifiableCollection(links);
  }

  @Override
  public Collection<Tag> tags() {
    if (this.tags != null) {
      return this.tags;
    }
    List<Tag> tags = new ArrayList<>();
    for (TagOrLink i : tagsAndLinks) {
      if (i instanceof Tag tag) {
        tags.add(tag);
      }
    }
    return this.tags = Collections.unmodifiableCollection(tags);
  }

  public abstract static sealed class Builder<
          T extends Node<T, B>, B extends AbstractDirectiveNode.Builder<T, B>>
      extends AbstractNode.Builder<T, B>
      permits BalanceDirective.Builder,
          CloseDirective.Builder,
          CommodityDirective.Builder,
          CustomDirective.Builder,
          DocumentDirective.Builder,
          EventDirective.Builder,
          NoteDirective.Builder,
          OpenDirective.Builder,
          PadDirective.Builder,
          PriceDirective.Builder,
          QueryDirective.Builder,
          TransactionDirective.Builder {
    private LocalDate date;
    private List<TagOrLink> tagsAndLinks;
    private Metadata metadata;

    Builder() {}

    Builder(
        SourceLocation sourceLocation,
        LocalDate date,
        List<TagOrLink> tagsAndLinks,
        Metadata metadata) {
      super(sourceLocation);
      this.date = date;
      this.tagsAndLinks = tagsAndLinks;
      this.metadata = metadata;
    }

    public LocalDate date() {
      return date;
    }

    public Builder<T, B> date(LocalDate date) {
      this.date = date;
      return this;
    }

    public List<TagOrLink> tagsAndLinks() {
      return tagsAndLinks;
    }

    public Builder<T, B> tagsAndLinks(List<TagOrLink> tagsAndLinks) {
      this.tagsAndLinks = tagsAndLinks;
      return this;
    }

    public Metadata metadata() {
      return metadata;
    }

    public Builder<T, B> metadata(Metadata metadata) {
      this.metadata = metadata;
      return this;
    }
  }
}
