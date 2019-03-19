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
public final class BDDParameters {

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Requisites {}

  @Target({ElementType.PARAMETER, ElementType.FIELD})
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Expectations {}
}
