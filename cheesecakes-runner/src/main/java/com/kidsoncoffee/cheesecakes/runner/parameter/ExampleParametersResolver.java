package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleParametersResolver.class);

  private final ParameterConverterExtractor<Method> customConverterExtractor;

  private final ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor;

  public ExampleParametersResolver(
      final ParameterConverterExtractor<Method> customConverterExtractor,
      final ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor) {
    this.customConverterExtractor = customConverterExtractor;
    this.defaultConverterExtractor = defaultConverterExtractor;
  }

  public Optional<Object[]> resolve(final Method testMethod, final Example.Builder example) {
    final Optional<Parameter.Converter[]> converters = this.extractConverters(example, testMethod);

    if (!converters.isPresent()) {
      return Optional.empty();
    }

    final Optional<String[]> parameters = this.retrieveParameters(example, testMethod, converters);

    return parameters.map(strings -> IntStream.range(0, converters.get().length)
            .mapToObj(i -> converters.get()[i].convert(strings[i]))
            .toArray(Object[]::new));

  }

  private Optional<Parameter.Converter[]> extractConverters(
      final Example.Builder example, final Method testMethod) {
    final Optional<Parameter.Converter>[] defaultConverters =
        this.defaultConverterExtractor.extract(example.getSchema());
    final Optional<Parameter.Converter>[] customConverters =
        this.customConverterExtractor.extract(testMethod);

    if (defaultConverters.length != customConverters.length) {
      LOGGER.error(
          "The size of the default converters ({}) and custom converters ({}) does not match for {} in {}.",
          defaultConverters.length,
          customConverters.length,
          testMethod.getName(),
          testMethod.getDeclaringClass());
      return Optional.empty();
    }

    return Optional.of(
        IntStream.range(0, defaultConverters.length)
            .sequential()
            .mapToObj(i -> customConverters[i].orElse(defaultConverters[i].get()))
            .toArray(Parameter.Converter[]::new));
  }

  private Optional<String[]> retrieveParameters(
      final Example.Builder example,
      final Method testMethod,
      final Optional<Parameter.Converter[]> converters) {
    final String[] parameters =
        example.getSchema().stream()
            .sorted(Comparator.comparingInt(Parameter.Schema::getOverallOrder))
            .map(schema -> example.getValue(schema).toString())
            .toArray(String[]::new);

    if (converters.get().length != parameters.length) {
      LOGGER.error(
          "The size of the converters ({}) and method parameters ({}) does not match for {} in {}.",
          converters.get().length,
          parameters.length,
          testMethod.getName(),
          testMethod.getDeclaringClass());
      return Optional.empty();
    }
    return Optional.of(parameters);
  }
}
