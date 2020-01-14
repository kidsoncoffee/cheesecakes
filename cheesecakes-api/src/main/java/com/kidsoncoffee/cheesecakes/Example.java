package com.kidsoncoffee.cheesecakes;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface Example {

  interface Source {
    // MARKER INTERFACE
  }

  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @interface Supplier {}

  class Builder {
    private Class featureClass;
    private final String scenarioMethodName;
    private final List<Parameter.Schema> schema;
    private final Map<String, Object> values;

    public Builder(
        final String featureClass,
        final String scenarioMethodName,
        final List<Parameter.Schema> schema) {
      try {
        this.featureClass = (featureClass == null) ? null : Class.forName(featureClass);
      } catch (ClassNotFoundException e) {
        this.featureClass = null;
      }
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

    public Object getValue(final Parameter.Schema parameter) {
      return this.values.get(parameter.getName());
    }

    public void setValue(final String parameter, final Object value) {
      this.values.put(parameter, value);
    }

    public void setValue(final Parameter.Schema parameter, final Object value) {
      this.setValue(parameter.getName(), value);
    }
  }

  static Multiple multiple(final Example.Builder... examples) {
    return multiple(Arrays.asList(examples));
  }

  static Multiple multiple(final List<Example.Builder> examples) {
    return new Multiple(examples);
  }

  class Multiple {

    private final List<Example.Builder> examples;

    public Multiple(final List<Builder> examples) {
      this.examples = examples;
    }

    public List<Builder> getExamples() {
      return examples;
    }
  }
}
