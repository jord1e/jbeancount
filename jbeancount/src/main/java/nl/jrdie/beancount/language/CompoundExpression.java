package nl.jrdie.beancount.language;

public sealed interface CompoundExpression
    permits BinaryCompoundExpression, UnaryCompoundExpression {}
