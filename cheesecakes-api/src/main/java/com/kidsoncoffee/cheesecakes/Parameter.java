package com.kidsoncoffee.cheesecakes;

import org.immutables.value.Value;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public @interface Parameter {

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
  interface Converter<R> extends Predicate<Class> {

    @Value.Parameter(order = 0)
    Class<R> getTargetType();

    @Value.Parameter(order = 1)
    Function<ConvertableParameter, R> getConverter();

    @Value.Auxiliary
    default R convert(final ConvertableParameter input) {
      return this.getConverter().apply(input);
    }

    @Value.Auxiliary
    default boolean test(final Class outputClass) {
      return true;
    }
  }

  @Value.Immutable
  @Value.Style(builder = "registrableConverter")
  interface RegistrableConverter<R> extends Converter<R> {

    @Value.Auxiliary
    default boolean test(final Class outputClass) {
      return getTargetType().isAssignableFrom(outputClass);
    }
  }

  @Value.Immutable
  @Value.Style(builder = "convertableParameter")
  interface ConvertableParameter {
    Method getMethod();

    Parameter.Schema getSchema();

    String getValue();
  }

  @Value.Immutable
  @Value.Style(builder = "schema")
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
