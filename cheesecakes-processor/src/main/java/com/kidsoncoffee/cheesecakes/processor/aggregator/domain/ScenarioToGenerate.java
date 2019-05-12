package com.kidsoncoffee.cheesecakes.processor.aggregator.domain;

import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface ScenarioToGenerate {
  String getTestMethod();

  List<ParameterToGenerate> getParameters();

  List<ExampleToGenerate> getExamples();

  @Value.Default
  default List<ParameterToGenerate> getRequisites() {
    return this.getParameters(com.kidsoncoffee.cheesecakes.Scenario.StepType.REQUISITE);
  }

  @Value.Default
  default List<ParameterToGenerate> getExpectations() {
    return this.getParameters(com.kidsoncoffee.cheesecakes.Scenario.StepType.EXPECTATION);
  }

  @Value.Default
  default List<ParameterToGenerate> getParameters(
      final com.kidsoncoffee.cheesecakes.Scenario.StepType stepType) {
    return this.getParameters().stream()
        .filter(p -> p.getStepType().equals(stepType))
        .collect(Collectors.toList());
  }
}
