package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.CustomDataDrivenScenarioConverter;
import com.kidsoncoffee.cheesecakes.DataDrivenScenarioConverter;
import com.kidsoncoffee.cheesecakes.TestCaseParameterSchema;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kidsoncoffee.cheesecakes.ImmutableDataDrivenScenarioConverter.of;

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
            of(String.class, Byte.class, Byte::parseByte),
            of(String.class, byte.class, Byte::parseByte),
            of(String.class, Short.class, Short::parseShort),
            of(String.class, short.class, Short::parseShort),
            of(String.class, Integer.class, Integer::parseInt),
            of(String.class, int.class, Integer::parseInt),
            of(String.class, Long.class, Long::parseLong),
            of(String.class, long.class, Long::parseLong),
            of(String.class, Float.class, Float::parseFloat),
            of(String.class, float.class, Float::parseFloat),
            of(String.class, Double.class, Double::parseDouble),
            of(String.class, double.class, Double::parseDouble),
            of(String.class, Boolean.class, Boolean::parseBoolean),
            of(String.class, boolean.class, Boolean::parseBoolean),
            of(Byte.class, byte.class, Byte::byteValue),
            of(Short.class, short.class, Short::shortValue),
            of(Integer.class, int.class, Integer::intValue),
            of(Long.class, long.class, Long::longValue),
            of(Float.class, float.class, Float::floatValue),
            of(Double.class, double.class, Double::doubleValue),
            of(Boolean.class, boolean.class, Boolean::booleanValue));
  }

  public Object[] resolve(final TestCase testCase, final Method testMethod) {
    final List<DataDrivenScenarioConverter> customConverters = extractCustomConverters(testMethod);

    final Map<String, TestCaseParameterSchema> indexedSchema =
        testCase.getSpecification().getSchema().stream()
            .collect(Collectors.toMap(TestCaseParameterSchema::getName, schema -> schema));
    return testCase.getSpecification().getSchema().stream()
        .sorted(Comparator.comparingInt(TestCaseParameterSchema::getOverallOrder))
        .map(TestCaseParameterSchema::getName)
        .map(fieldName -> resolveValue(indexedSchema, customConverters, testCase, fieldName))
        .toArray(Object[]::new);
  }

  private List<DataDrivenScenarioConverter> extractCustomConverters(final Method testMethod) {
    return Arrays.stream(testMethod.getDeclaringClass().getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(CustomDataDrivenScenarioConverter.class))
        .filter(f -> f.getType().isAssignableFrom(DataDrivenScenarioConverter.class))
        .map(
            f -> {
              f.setAccessible(true);
              // TODO fchovich VALIDATE TYPES AND MANDATORY BLABLABLA
              try {
                return (DataDrivenScenarioConverter) f.get(null);
              } catch (IllegalAccessException e) {
                throw new IllegalStateException("", e);
              }
            })
        .collect(Collectors.toList());
  }

  private Object resolveValue(
      final Map<String, TestCaseParameterSchema> indexedSchema,
      List<DataDrivenScenarioConverter> customConverters,
      final TestCase testCase,
      final String fieldName) {

    final TestCaseParameterSchema schema = indexedSchema.get(fieldName);
    final Object rawValue = testCase.getSpecification().getValue(fieldName);

    if (schema.getType().isInstance(rawValue)) {
      return rawValue;
    } else if (rawValue instanceof String && schema.getType().isArray()) {
      return Arrays.stream(rawValue.toString().split(","))
          .map(String::trim)
          .map(r -> applyConvertersForValue(customConverters, schema, r))
          .toArray();
    } else if (rawValue instanceof String
        && (schema.getType().isAssignableFrom(Collection.class)
            || schema.getType().isAssignableFrom(List.class))) {
      return Arrays.stream(rawValue.toString().split(","))
          .map(String::trim)
          .map(r -> applyConvertersForValue(customConverters, schema, r))
          .collect(Collectors.toList());
    }
    return applyConvertersForValue(customConverters, schema, rawValue);
  }

  private Object applyConvertersForValue(
      final List<DataDrivenScenarioConverter> customConverters,
      TestCaseParameterSchema schema,
      Object rawValue) {
    return applyConverters(customConverters, schema, rawValue)
        .orElseGet(
            () ->
                applyConverters(this.defaultConverters, schema, rawValue)
                    .orElseThrow(() -> new IllegalStateException("")));
  }

  private static Optional<Object> applyConverters(
      final List<DataDrivenScenarioConverter> converters,
      TestCaseParameterSchema schema,
      Object rawValue) {
    return converters.stream()
        .filter(converter -> converter.test(rawValue, schema.getType()))
        .map(converter -> converter.convert(rawValue))
        .findFirst();
  }
}
