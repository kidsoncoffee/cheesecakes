package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * Resolves the parameter converters for a {@link Method}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterConverterResolver {

  /** Logger for this factory. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ParameterConverterResolver.class);

  /**
   * The custom converter extractor. These are converters explicitly indicate on a scenario method.
   */
  private final ParameterConverterExtractor<Method> customConverterExtractor;

  /** The default converter extractor. These are converters that matches Java common types. */
  private final ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor;

  public ParameterConverterResolver(
      final ParameterConverterExtractor<Method> customConverterExtractor,
      final ParameterConverterExtractor<List<Parameter.Schema>> defaultConverterExtractor) {
    this.customConverterExtractor = customConverterExtractor;
    this.defaultConverterExtractor = defaultConverterExtractor;
  }

  /**
   * Extracts converters for the given example and test method. It will extract the default and
   * custom converters, expecting they will return arrays of the same size and return a merged array
   * of instantiated resolvers giving higher priority to custom over default.
   *
   * <p>It will return an optional of an array the same size of the method's parameters. It will as
   * well follow the signature order.
   *
   * @param testMethod The test method.
   * @param example The example to be run.
   * @return An optional of an array of parameter converters.
   */
  public Optional<Parameter.Converter[]> resolveConverters(
      final Method testMethod, final Example.Builder example) {
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
            .filter(i -> customConverters[i].isPresent() || defaultConverters[i].isPresent())
            .mapToObj(i -> customConverters[i].orElseGet(() -> defaultConverters[i].get()))
            .toArray(Parameter.Converter[]::new));
  }
}
