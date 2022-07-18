package nl.jrdie.beancount.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ImmutableKit {

  private ImmutableKit() {}

  public static <T> List<T> emptyList() {
    return Collections.emptyList();
  }

  public static <T, R> List<R> map(
      Collection<? extends T> collection, Function<? super T, ? extends R> mapper) {
    // TODO Optimise
    List<R> list = new ArrayList<>();
    for (T t : collection) {
      R r = mapper.apply(t);
      list.add(r);
    }
    return Collections.unmodifiableList(list);
  }
}
