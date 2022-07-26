package nl.jrdie.beancount;

public class BeancountException extends RuntimeException {
  public BeancountException() {}

  public BeancountException(String message) {
    super(message);
  }

  public BeancountException(String message, Throwable cause) {
    super(message, cause);
  }
}
