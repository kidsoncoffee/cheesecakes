package com.kidsoncoffee.paramtests.generator;

import org.immutables.value.Value;

import javax.lang.model.type.TypeMirror;
import java.util.Map;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterizedTestsDefinition {
  String getTestClassName();

  String getTestClassPackage();

  Map<String, TypeMirror> getRequisites();

  Map<String, TypeMirror> getExpectations();
}
