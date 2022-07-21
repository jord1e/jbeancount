package nl.jrdie.beancount.language;

public sealed interface MetadataLine permits Comment, Link, MetadataItem, Tag {}
