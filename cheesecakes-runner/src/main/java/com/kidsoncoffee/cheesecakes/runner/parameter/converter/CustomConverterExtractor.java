package com.kidsoncoffee.cheesecakes.runner.parameter.converter;

import com.kidsoncoffee.cheesecakes.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;

/**
 * Provides an implementation of {@link ParameterConverterExtractor} that extracts {@link
 * Parameter.Converter} from a {@link Method}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class CustomConverterExtractor implements ParameterConverterExtractor<Method> {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(CustomConverterExtractor.class);

  /**
   * Extracts an array of {@link Parameter.Converter} optionals from the {@link Method}. The array
   * returned is the same size of the method parameters. The custom converter is declared by
   * annotating a parameter with {@link Parameter.Conversion}, and after retrieving the class it is
   * instantiated.
   *
   * @param method
   * @return
   */
  public Optional<Parameter.Converter>[] extract(final Method method) {
    return Arrays.stream(method.getParameters())
        .map(CustomConverterExtractor::extractParameter)
        .toArray(Optional[]::new);
  }

  /**
   * Extracts the {@link Parameter.Converter} from a method {@link java.lang.reflect.Parameter}. If
   * the parameters is annotated with {@link Parameter.Conversion}, its value, the converter class,
   * must have a public constructor with zero parameters to be properly instantiated.
   *
   * <p>If a converter is not assigned to this parameter or any error occurs while retrieving it, an
   * empty optional is returned.
   *
   * @param parameter The method's parameter to retrieve the converter from.
   * @return The converter optional.
   */
  private static Optional<Parameter.Converter> extractParameter(
      final java.lang.reflect.Parameter parameter) {
    if (parameter.isAnnotationPresent(Parameter.Conversion.class)) {
      final Class<? extends Parameter.Converter> converter =
          parameter.getAnnotation(Parameter.Conversion.class).value();

      if(converter.getEnclosingClass() == null || Modifier.isStatic(converter.getModifiers())){
        final boolean noParameterConstructor =
                Arrays.stream(converter.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0);

        if (!noParameterConstructor) {
          LOGGER.error(
                  "The custom converter '{}' for '{}' must have one constructor with zero parameters.",
                  converter,
                  parameter.getDeclaringExecutable().getName());
          return Optional.empty();
        }

        try {
          final Constructor<? extends Parameter.Converter> constructor = converter.getConstructor();
          constructor.setAccessible(true);
          return Optional.of(constructor.newInstance());
        } catch (Throwable e) {
          LOGGER.error(
                  "Error instantiating the custom converter '{}' for '{}'.",
                  converter,
                  parameter.getDeclaringExecutable().getName());
        }
      } else {
        final boolean noParameterConstructor =
                Arrays.stream(converter.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 1);

        if (!noParameterConstructor) {
          LOGGER.error(
                  "The custom converter '{}' for '{}' must have one constructor with zero parameters.",
                  converter,
                  parameter.getDeclaringExecutable().getName());
          return Optional.empty();
        }

        try {
          final Constructor<? extends Parameter.Converter> constructor = converter.getDeclaredConstructor(converter.getEnclosingClass());
          constructor.setAccessible(true);
          return Optional.of(constructor.newInstance(converter.getEnclosingClass().newInstance()));
        } catch (Throwable e) {
          LOGGER.error(
                  "Error instantiating the custom converter '{}' for '{}'.",
                  converter,
                  parameter.getDeclaringExecutable().getName());
        }
      }
    }
    return Optional.empty();
  }
}
