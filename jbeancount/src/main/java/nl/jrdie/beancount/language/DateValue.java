package nl.jrdie.beancount.language;

import java.time.LocalDate;
import java.util.Objects;

public final class DateValue implements CostCompValue, ScalarValue {

  private final LocalDate date;

  private DateValue(LocalDate date) {
    this.date = Objects.requireNonNull(date, "date");
  }

  public LocalDate date() {
    return date;
  }

  public static Builder newDateValue() {
    return new Builder();
  }

  public static final class Builder {
    private LocalDate date;

    private Builder() {}

    public DateValue build() {
      return new DateValue(date);
    }

    public LocalDate date() {
      return date;
    }

    public Builder date(LocalDate date) {
      this.date = date;
      return this;
    }
  }
}
