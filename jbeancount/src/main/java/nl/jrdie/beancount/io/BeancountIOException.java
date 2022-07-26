package nl.jrdie.beancount.io;

import nl.jrdie.beancount.BeancountException;

public class BeancountIOException extends BeancountException {

  public BeancountIOException() {}

  public BeancountIOException(String message) {
    super(message);
  }
}
