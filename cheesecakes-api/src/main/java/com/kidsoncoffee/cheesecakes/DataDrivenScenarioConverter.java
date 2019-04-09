package com.kidsoncoffee.cheesecakes;

import org.immutables.value.Value;

import java.util.function.Function;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface DataDrivenScenarioConverter<T, R> {
  @Value.Parameter(order = 2)
  Function<T, R> getConverter();

  @Value.Parameter(order = 0)
  Class<T> getBaseType();

  @Value.Parameter(order = 1)
  Class<R> getTargetType();

  @Value.Auxiliary
  default R convert(final T input) {
    return this.getConverter().apply(input);
  }

  @Value.Auxiliary
  default boolean test(final Object input, final Class outputClass) {
    return getBaseType().isInstance(input) && getTargetType().isAssignableFrom(outputClass);
  }
}
