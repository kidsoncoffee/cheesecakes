package com.kidsoncoffee.paramtests.scenario.domain;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioBlockDefinition {
  String getParameterName();

  TypeMirror getParameterType();

  int getOverallOrder();

  Class<? extends Annotation> getAnnotationType();
}
