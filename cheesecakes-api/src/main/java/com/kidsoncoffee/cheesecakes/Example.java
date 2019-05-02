package com.kidsoncoffee.cheesecakes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public @interface Example {

  interface Source {
    // MARKER INTERFACE
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Supplier {}

  class Builder {
    private final Class featureClass;
    private final String scenarioMethodName;
    private final List<Parameter.Schema> schema;

    private final Map<String, Object> values;

    public Builder(
        final Class featureClass,
        final String scenarioMethodName,
        final List<Parameter.Schema> schema) {
      this.featureClass = featureClass;
      this.scenarioMethodName = scenarioMethodName;
      this.schema = schema;
      this.values = new HashMap<>();
    }

    public Class getFeatureClass() {
      return featureClass;
    }

    public String getScenarioMethodName() {
      return scenarioMethodName;
    }

    public List<Parameter.Schema> getSchema() {
      return this.schema;
    }

    public List<Parameter.Schema> getSchema(final Scenario.StepType stepType) {
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
  }
}
