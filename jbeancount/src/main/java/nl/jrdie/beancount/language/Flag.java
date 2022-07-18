package nl.jrdie.beancount.language;

public sealed interface Flag permits SymbolFlag, TxnFlag {

  String flag();

  boolean star();

  boolean exclamationMark();

  boolean txn();
}
