package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Parameter;

import java.util.Optional;

/**
 * Extracts {@link Parameter.Converter}s from a base object.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public interface ParameterConverterExtractor<T> {

  /**
   * Extracts optionals from {@link Parameter.Converter} from the base object.
   *
   * @param base The base object to extract the converters from.
   * @return An array of {@link Parameter.Converter} optionals.
   */
  Optional<Parameter.Converter>[] extract(final T base);
}
