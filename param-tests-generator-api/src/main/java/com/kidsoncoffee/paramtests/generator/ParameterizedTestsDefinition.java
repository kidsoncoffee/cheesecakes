package com.kidsoncoffee.paramtests.generator;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ParameterizedTestsDefinition {
  String getTestClassName();

  String getTestClassPackage();

  String getTestMethodName();

  List<ParameterizedTestsBlockDefinition> getRequisites();

  List<ParameterizedTestsBlockDefinition> getExpectations();
}
