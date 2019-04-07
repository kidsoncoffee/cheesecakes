package com.kidsoncoffee.cheesecakes.processor.domain;

import com.kidsoncoffee.cheesecakes.SpecificationStepType;
import org.immutables.value.Value;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface Scenario {
  String getTestMethod();

  List<Parameter> getParameters();

  List<Example> getExamples();

  @Value.Default
  default List<Parameter> getRequisites() {
    return this.getParameters(SpecificationStepType.REQUISITE);
  }

  @Value.Default
  default List<Parameter> getExpectations() {
    return this.getParameters(SpecificationStepType.EXPECTATION);
  }

  @Value.Default
  default List<Parameter> getParameters(final SpecificationStepType stepType) {
    return this.getParameters().stream()
        .filter(p -> p.getStepType().equals(stepType))
        .collect(Collectors.toList());
  }
}
