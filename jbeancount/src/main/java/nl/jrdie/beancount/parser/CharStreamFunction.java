package nl.jrdie.beancount.parser;

import java.io.IOException;
import org.antlr.v4.runtime.CharStream;

@FunctionalInterface
public interface CharStreamFunction<T> {

  CharStream apply(T t) throws IOException;
}
