package nl.jrdie.beancount.language;

public sealed interface ArithmeticExpression extends ScalarValue
    permits ConstantExpression,
        AbstractBinaryArithmeticExpression,
        AbstractUnaryArithmeticExpression {}
