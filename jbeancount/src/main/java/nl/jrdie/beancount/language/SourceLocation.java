package nl.jrdie.beancount.language;

public final class SourceLocation {

  public static final SourceLocation EMPTY = new SourceLocation(-1, -1);

  private final int line;
  private final int column;

  private SourceLocation(int line, int column) {
    this.line = line;
    this.column = column;
  }

  public int line() {
    return line;
  }

  public int column() {
    return column;
  }

  public static SourceLocation of(int line, int column) {
    return new SourceLocation(line, column);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SourceLocation that = (SourceLocation) o;

    if (line != that.line) return false;
    return column == that.column;
  }

  @Override
  public int hashCode() {
    int result = line;
    result = 31 * result + column;
    return result;
  }

  @Override
  public String toString() {
    return "SourceLocation{" + "line=" + line + ", column=" + column + '}';
  }
}
