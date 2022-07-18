package nl.jrdie.beancount.language;

public sealed interface Tag extends TagOrLink, MetadataLine permits TagValue {

  String tag();
}
