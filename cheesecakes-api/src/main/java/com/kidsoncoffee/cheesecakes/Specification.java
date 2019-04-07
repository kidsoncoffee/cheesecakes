package com.kidsoncoffee.cheesecakes;

import com.kidsoncoffee.cheesecakes.frosting.Spec;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
//TODO fchovich SEPARATE THE RUNNER API FROM THE ANNOTATIONS
public class Specification implements Spec {

  private final List<TestCaseParameterSchema> schema;

  private final Map<String, Object> values;

  public Specification(final List<TestCaseParameterSchema> schema) {
    this.schema = schema;
    this.values = new HashMap<>();
  }

  public List<TestCaseParameterSchema> getSchema() {
    return this.schema;
  }

  public List<TestCaseParameterSchema> getRequisites() {
    return this.getParameters(SpecificationStepType.REQUISITE);
  }

  public List<TestCaseParameterSchema> getExpectations() {
    return this.getParameters(SpecificationStepType.EXPECTATION);
  }

  public List<TestCaseParameterSchema> getParameters(final SpecificationStepType stepType) {
    return this.getSchema().stream()
        .filter(s -> s.getStep().equals(stepType))
        .collect(Collectors.toList());
  }

  public Object getValue(final String parameter) {
    return this.values.get(parameter);
  }

  public void setValue(final String parameter, final Object value) {
    this.values.put(parameter, value);
  }

  @Override
  public List<Specification> getSpecificationReferences() {
    return Arrays.asList(this);
  }
}
