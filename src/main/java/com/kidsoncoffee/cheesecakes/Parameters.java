package com.kidsoncoffee.cheesecakes;

import org.apache.commons.lang3.tuple.Pair;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
// TODO fchovich parameterize the name of the builder field
public final class Parameters {

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Requisites {
    // TODO fchovich USE THIS TO BIND
    String value() default "";
  }

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Expectations {
    // TODO fchovich USE THIS TO BIND
    String value() default "";
  }

  public interface Binding {
    List<Pair<Class, String>> getParameterNames();
  }

  // BREAK

  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Scenario {
    String value() default "";
  }

  @Target({ElementType.METHOD, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ScenarioBinding {
    String value();
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface DataDriven {}
}
