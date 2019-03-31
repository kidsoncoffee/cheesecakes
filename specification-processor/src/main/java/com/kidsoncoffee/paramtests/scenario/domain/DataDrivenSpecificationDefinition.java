package com.kidsoncoffee.paramtests.scenario.domain;

import org.immutables.value.Value;

import java.util.List;
import java.util.Map;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface DataDrivenSpecificationDefinition {
  String getTestMethodName();

  List<Map<String, String>> getScenarios();
}
