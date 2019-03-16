package com.kidsoncoffe.paramtests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public @interface BDDParameters {

  @Target(ElementType.PARAMETER)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface Requisites {}
}
