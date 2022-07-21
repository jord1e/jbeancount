package nl.jrdie.beancount.language;

public final class SourceLocation {

  public static final SourceLocation EMPTY = new SourceLocation(-1, -1, null);

  private final int line;
  private final int column;
  private final String sourceName;

  private SourceLocation(int line, int column, String sourceName) {
    this.line = line;
    this.column = column;
    this.sourceName = sourceName;
  }

  public int line() {
    return line;
  }

  public int column() {
    return column;
  }

  public String sourceName() {
    return sourceName;
  }

  public static SourceLocation of(int line, int column, String sourceName) {
    return new SourceLocation(line, column, sourceName);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SourceLocation that = (SourceLocation) o;

    if (line != that.line) return false;
    if (column != that.column) return false;
    return sourceName.equals(that.sourceName);
  }

  @Override
  public int hashCode() {
    int result = line;
    result = 31 * result + column;
    result = 31 * result + sourceName.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "SourceLocation{" +
            "line=" + line +
            ", column=" + column +
            ", sourceName='" + sourceName + '\'' +
            '}';
  }
}
