package nl.jrdie.beancount.language;

public sealed interface ScalarValue extends MetadataValue
    permits Account,
        ArithmeticExpression,
        BooleanValue,
        Commodity,
        DateValue,
        LinkValue,
        NilValue,
        StringValue,
        TagValue {}
