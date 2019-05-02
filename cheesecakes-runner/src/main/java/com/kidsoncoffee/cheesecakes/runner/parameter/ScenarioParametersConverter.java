package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.CheesecakesException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.kidsoncoffee.cheesecakes.ImmutableRegistrableConverter.of;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioParametersConverter {

  private final List<Parameter.RegistrableConverter> defaultConverters;

  public ScenarioParametersConverter() {
    // TODO fchovich BETTER NAMING PLEASE
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
            of(
                String.class,
                Class.class,
                className -> {
                  try {
                    return Class.forName(className);
                  } catch (ClassNotFoundException e) {
                    throw new CheesecakesException(
                        String.format("Error converting String '%s' to Class.", className), e);
                  }
                }));
  }

  public Object[] resolve(final Example.Builder example, final Method testMethod) {
    final List<Parameter.Converter> customConverters = extractCustomConverters(testMethod);

    final Map<String, Parameter.Schema> indexedSchema =
        example.getSchema().stream()
            .collect(Collectors.toMap(Parameter.Schema::getName, schema -> schema));
    return example.getSchema().stream()
        .sorted(Comparator.comparingInt(Parameter.Schema::getOverallOrder))
        .map(Parameter.Schema::getName)
        .map(fieldName -> resolveValue(indexedSchema, customConverters, example, fieldName))
        .toArray(Object[]::new);
  }

  private List<Parameter.Converter> extractCustomConverters(final Method testMethod) {
    return Arrays.stream(testMethod.getDeclaringClass().getDeclaredFields())
        .filter(f -> f.isAnnotationPresent(Parameter.Conversion.class))
        .filter(f -> f.getType().isAssignableFrom(Parameter.Converter.class))
        .map(
            f -> {
              f.setAccessible(true);
              // TODO fchovich VALIDATE TYPES AND MANDATORY BLABLABLA
              try {
                return (Parameter.Converter) f.get(null);
              } catch (IllegalAccessException e) {
                throw new IllegalStateException("", e);
              }
            })
        .collect(Collectors.toList());
  }

  private Object resolveValue(
      final Map<String, Parameter.Schema> indexedSchema,
      List<Parameter.Converter> customConverters,
      final Example.Builder example,
      final String fieldName) {

    final Parameter.Schema schema = indexedSchema.get(fieldName);
    final Object rawValue = example.getValue(fieldName);

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
      final List<Parameter.Converter> customConverters, Parameter.Schema schema, Object rawValue) {
    return applyConverters(customConverters, schema, rawValue)
        .orElseGet(
            () ->
                applyConverters(this.defaultConverters, schema, rawValue)
                    .orElseThrow(() -> new CheesecakesException("")));
  }

  private static Optional<Object> applyConverters(
      final List<? extends Parameter.Converter> converters,
      Parameter.Schema schema,
      Object rawValue) {
    return converters.stream()
        .filter(converter -> converter.test(rawValue, schema.getType()))
        .map(converter -> converter.convert(rawValue))
        .findFirst();
  }
}
