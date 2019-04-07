package com.kidsoncoffee.cheesecakes;

import org.apache.commons.lang3.tuple.Pair;

import javax.swing.text.html.Option;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Optional;

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

  // TODO fchovich IS THIS IS A USER INTERFACE
  @Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface ScenarioBinding {
    Class testClass();

    String testMethod();
  }

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface DataDriven {
    String value();
  }
}
