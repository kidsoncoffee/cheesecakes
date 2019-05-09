package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleParametersResolver.class);
  private final ParameterConverterResolver parameterConverterResolver;

  public ExampleParametersResolver(final ParameterConverterResolver parameterConverterResolver) {
    this.parameterConverterResolver = parameterConverterResolver;
  }

  public Optional<Object[]> resolve(final Method testMethod, final Example.Builder example) {
    final Optional<Parameter.Converter[]> converters = this.parameterConverterResolver.resolveConverters(testMethod, example);

    if (!converters.isPresent()) {
      return Optional.empty();
    }

    final Optional<String[]> parameters =
        this.retrieveParameters(example, testMethod, converters.get());

    return parameters.map(
        strings ->
            IntStream.range(0, converters.get().length)
                .mapToObj(i -> converters.get()[i].convert(strings[i]))
                .toArray(Object[]::new));
  }

  private Optional<String[]> retrieveParameters(
      final Example.Builder example,
      final Method testMethod,
      final Parameter.Converter[] converters) {
    final String[] parameters =
        example.getSchema().stream()
            .sorted(Comparator.comparingInt(Parameter.Schema::getOverallOrder))
            .map(schema -> example.getValue(schema).toString())
            .toArray(String[]::new);

    if (converters.length != parameters.length) {
      LOGGER.error(
          "The size of the converters ({}) and method parameters ({}) does not match for {} in {}.",
          converters.length,
          parameters.length,
          testMethod.getName(),
          testMethod.getDeclaringClass());
      return Optional.empty();
    }
    return Optional.of(parameters);
  }
}
