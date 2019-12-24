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
 * Resolves the example parameters.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleParametersResolver {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ExampleParametersResolver.class);

  /** The {@link Parameter.Converter} resolver. */
  private final ParameterConverterResolver parameterConverterResolver;

  /** The {@link Parameter.Convertible} resolver. */
  private final ParameterConvertibleCreator parameterConvertibleCreator;

  /**
   * Constructs with the parameter converter resolver and the convertible creator.
   *
   * @param parameterConverterResolver The parameter converter resolver.
   * @param parameterConvertibleCreator The
   */
  public ExampleParametersResolver(
      final ParameterConverterResolver parameterConverterResolver,
      final ParameterConvertibleCreator parameterConvertibleCreator) {
    this.parameterConverterResolver = parameterConverterResolver;
    this.parameterConvertibleCreator = parameterConvertibleCreator;
  }

  public Optional<Object[]> resolve(final Method testMethod, final Example.Builder example) {
    // TODO fchovich MOVE LOGIC FROM THE RESOLVER. CREATE CONVERTIBLES BEFORE.
    return this.parameterConverterResolver
        .resolveConverters(testMethod, example)
        .flatMap(c -> this.resolveParameters(testMethod, example, c));
  }

  private Optional<Object[]> resolveParameters(
      final Method testMethod,
      final Example.Builder example,
      final Parameter.Converter[] converters) {
    final Parameter.Convertible[] parameters =
        this.parameterConvertibleCreator.create(testMethod, example);

    // TODO fchovich REMOVE THIS LOGIC TO WHERE PARAMETERS != CONVERTERS IS IMPORTANT
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
