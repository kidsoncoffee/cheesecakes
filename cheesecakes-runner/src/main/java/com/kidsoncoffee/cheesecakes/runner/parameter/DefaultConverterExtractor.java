package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Parameter;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Provides an implementation of {@link ParameterConverterExtractor} that extracts {@link
 * Parameter.Converter}s from a list of {@link Parameter.Schema}s matching a set of {@link
 * Parameter.RegistrableConverter}s.
 *
 * <p>These converters are considered default because they deal with usual Java language
 * transformations.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class DefaultConverterExtractor
    implements ParameterConverterExtractor<List<Parameter.Schema>> {

  /** The list of default converters. */
  private final List<Parameter.RegistrableConverter> defaultConverters;

  /**
   * Constructs the extract with a list of default converters.
   *
   * @param defaultConverters The list to pull the converters from.
   */
  public DefaultConverterExtractor(final List<Parameter.RegistrableConverter> defaultConverters) {
    this.defaultConverters = defaultConverters;
  }

  /**
   * Extracts an array of {@link Parameter.Converter} optionals from the list of {@link
   * Parameter.Schema}. The array returned is the same size of the given list of {@link
   * Parameter.Schema}s. If the {@link Parameter.Schema#getType()} does not match any converter an
   * empty optional is assigned to the respective position in the array.
   *
   * @param schemas The schemas to extract the converters from.
   * @return An array of converter optionals.
   */
  @Override
  public Optional<Parameter.Converter>[] extract(final List<Parameter.Schema> schemas) {
    return schemas.stream()
        .sorted(Comparator.comparingInt(Parameter.Schema::getOverallOrder))
        .map(
            schema ->
                this.defaultConverters.stream().filter(c -> c.test(schema.getType())).findFirst())
        .toArray(Optional[]::new);
  }
}
