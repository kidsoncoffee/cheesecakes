package com.kidsoncoffee.paramtests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fernando.chovich
 * @since 1.0
 */
// TODO fchovich parameterize the name of the builder field
public final class Parameters {

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Requisites {}

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Expectations {}

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
