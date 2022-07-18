package nl.jrdie.beancount.language;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import nl.jrdie.beancount.annotation.Beta;

public final class SymbolFlag implements Flag {

  private final Type type;

  private SymbolFlag(Type type) {
    this.type = Objects.requireNonNull(type, "type");
  }

  @Override
  public String flag() {
    return type.symbol();
  }

  @Override
  public boolean star() {
    return type == Type.ASTERISK;
  }

  @Override
  public boolean exclamationMark() {
    return type == Type.EXCLAMATION_MARK;
  }

  @Override
  public boolean txn() {
    return false;
  }

  public static Builder newSymbolFlag() {
    return new Builder();
  }

  public static final class Builder {
    private Type type;

    private Builder() {}

    public SymbolFlag build() {
      return new SymbolFlag(type);
    }

    public Type type() {
      return type;
    }

    public Builder type(Type type) {
      this.type = type;
      return this;
    }
  }

  @Beta
  public enum Type {
    ASTERISK("*"),
    HASH("#"),
    EXCLAMATION_MARK("!"),
    AMPERSAND("&"),
    QUESTION_MARK("?"),
    PERCENT("%");

    private static final Map<String, Type> TYPE_MAP;

    static {
      final Map<String, Type> typeMap = new HashMap<>();
      for (Type type : Type.values()) {
        typeMap.put(type.symbol(), type);
      }
      TYPE_MAP = Collections.unmodifiableMap(typeMap);
    }

    @Beta
    public static Type ofSymbol(String symbol) {
      return TYPE_MAP.get(symbol);
    }

    private final String symbol;

    Type(String symbol) {
      this.symbol = Objects.requireNonNull(symbol, "symbol");
    }

    public String symbol() {
      return symbol;
    }
  }
}
