package nl.jrdie.beancount.language;

public sealed interface Link extends TagOrLink, MetadataLine permits LinkValue {

  String link();
}
