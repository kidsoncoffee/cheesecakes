package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.DataDrivenScenarioConverter;
import com.kidsoncoffee.cheesecakes.ImmutableDataDrivenScenarioConverter;
import com.kidsoncoffee.cheesecakes.TestCaseParameterSchema;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class TestCaseInjectablesResolver {

  private final List<DataDrivenScenarioConverter> defaultConverters;

  public TestCaseInjectablesResolver() {
    // TODO fchovich bETTER NAMING PLEASE
    this.defaultConverters =
        Arrays.asList(
            ImmutableDataDrivenScenarioConverter.of(String.class, Integer.class, Integer::parseInt),
            ImmutableDataDrivenScenarioConverter.of(String.class, int.class, Integer::parseInt));
  }

  public Object[] resolve(final TestCase testCase, final Method testMethod) {
    final Map<String, TestCaseParameterSchema> indexedSchema =
        testCase.getSpecification().getSchema().stream()
            .collect(Collectors.toMap(TestCaseParameterSchema::getName, schema -> schema));
    return testCase.getSpecification().getSchema().stream()
        .sorted(Comparator.comparingInt(TestCaseParameterSchema::getOverallOrder))
        .map(TestCaseParameterSchema::getName)
        .map(fieldName -> resolveValue(indexedSchema, testCase, fieldName))
        .toArray(Object[]::new);
  }

  private Object resolveValue(
      final Map<String, TestCaseParameterSchema> indexedSchema,
      final TestCase testCase,
      final String fieldName) {

    final TestCaseParameterSchema schema = indexedSchema.get(fieldName);
    final Object rawValue = testCase.getSpecification().getValue(fieldName);

    if (schema.getType().isInstance(rawValue)) {
      return rawValue;
    }

    return this.defaultConverters.stream()
        .filter(converter -> converter.test(rawValue, schema.getType()))
        .map(converter -> converter.convert(rawValue))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException(""));
  }
}
