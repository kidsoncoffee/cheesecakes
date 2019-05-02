package com.kidsoncoffee.cheesecakes;

import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.Function;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface Parameter {

  // TODO fchovich IS PARAMETER PART OF THE API?

  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Requisite {}

  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Expectation {}

  @Target({ElementType.PARAMETER})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Conversion {
    Class<? extends Converter> value();
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface SchemaSupplier {}

  @Value.Immutable
  @Value.Style(builder = "converter")
  interface Converter<T, R> {
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
      return true;
    }
  }

  @Value.Immutable
  @Value.Style(builder = "registrableConverter")
  interface RegistrableConverter<T, R> extends Converter<T, R> {
    @Value.Auxiliary
    default boolean test(final Object input, final Class outputClass) {
      return getBaseType().isInstance(input) && getTargetType().isAssignableFrom(outputClass);
    }
  }

  interface Schema {
    // TODO fchovich SHOULD THIS BE OPEN TO USER
    String getName();

    Class getType();

    Scenario.StepType getStep();

    int getOverallOrder();
  }

  interface SchemaSource {
    // MARKER INTERFACE
  }
}
