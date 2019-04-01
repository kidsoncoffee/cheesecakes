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
public class Specification implements Spec {

  private final List<SpecificationParameter> schema;

  private final Map<String, Object> values;

  public Specification(final List<SpecificationParameter> schema) {
    this.schema = schema;
    this.values = new HashMap<>();
  }

  public List<SpecificationParameter> getSchema() {
    return this.schema;
  }

  public List<SpecificationParameter> getRequisites() {
    return this.getParameters(SpecificationStepType.REQUISITE);
  }

  public List<SpecificationParameter> getExpectations() {
    return this.getParameters(SpecificationStepType.EXPECTATION);
  }

  public List<SpecificationParameter> getParameters(final SpecificationStepType stepType) {
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
