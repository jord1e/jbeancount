package nl.jrdie.beancount.util;

public final class Assert {
  private Assert() {}

  public static void shouldNeverHappen() {
    throw new RuntimeException("Should never happen");
  }
}
