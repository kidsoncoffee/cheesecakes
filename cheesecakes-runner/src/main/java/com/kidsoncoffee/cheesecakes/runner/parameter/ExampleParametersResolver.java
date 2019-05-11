package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolver {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleParametersResolver.class);
  private final ParameterConverterResolver parameterConverterResolver;
  private final ConvertableParametersCreator convertableParametersCreator;

  public ExampleParametersResolver(
      final ParameterConverterResolver parameterConverterResolver,
      final ConvertableParametersCreator convertableParametersCreator) {
    this.parameterConverterResolver = parameterConverterResolver;
    this.convertableParametersCreator = convertableParametersCreator;
  }

  public Optional<Object[]> resolve(final Method testMethod, final Example.Builder example) {
    return this.parameterConverterResolver
        .resolveConverters(testMethod, example)
        .flatMap(c -> this.resolveParameters(testMethod, example, c));
  }

  private Optional<Object[]> resolveParameters(
      final Method testMethod,
      final Example.Builder example,
      final Parameter.Converter[] converters) {
    final Parameter.ConvertableParameter[] parameters =
        this.convertableParametersCreator.create(testMethod, example);

    if (converters.length != parameters.length) {
      LOGGER.error(
          "The size of the converters ({}) and method parameters ({}) does not match for {} in {}.",
          converters.length,
          parameters.length,
          testMethod.getName(),
          testMethod.getDeclaringClass());
      return Optional.empty();
    }

    return Optional.of(
        IntStream.range(0, converters.length)
            .mapToObj(i -> converters[i].convert(parameters[i]))
            .toArray(Object[]::new));
  }
}
