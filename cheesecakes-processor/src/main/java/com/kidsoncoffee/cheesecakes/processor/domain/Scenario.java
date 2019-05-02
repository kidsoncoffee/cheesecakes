package com.kidsoncoffee.cheesecakes.processor.domain;

import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
@Value.Style(builder = "scenario")
public interface Scenario {
  String getTestMethod();

  List<Parameter> getParameters();

  List<Example> getExamples();

  @Value.Default
  default List<Parameter> getRequisites() {
    return this.getParameters(com.kidsoncoffee.cheesecakes.Scenario.StepType.REQUISITE);
  }

  @Value.Default
  default List<Parameter> getExpectations() {
    return this.getParameters(com.kidsoncoffee.cheesecakes.Scenario.StepType.EXPECTATION);
  }

  @Value.Default
  default List<Parameter> getParameters(
      final com.kidsoncoffee.cheesecakes.Scenario.StepType stepType) {
    return this.getParameters().stream()
        .filter(p -> p.getStepType().equals(stepType))
        .collect(Collectors.toList());
  }
}
