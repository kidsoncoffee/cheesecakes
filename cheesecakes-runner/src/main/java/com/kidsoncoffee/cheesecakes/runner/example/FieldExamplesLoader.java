package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class FieldExamplesLoader implements ExamplesLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataTableExamplesLoader.class);

  public List<Example.Builder> load(final Class featureClass) {
    return Arrays.stream(featureClass.getDeclaredFields())
        .filter(field -> field.getType().isAssignableFrom(Example.class))
        .map(field -> retrieveExample(featureClass, field))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.toList());
  }

  private static Optional<Example.Builder> retrieveExample(Class<?> examplesSupplier, Field field) {
    try {
      field.setAccessible(true);
      return Optional.of((Example.Builder) field.get(null));
    } catch (IllegalAccessException e) {
      LOGGER.warn(
          String.format(
              "Error while retrieving examples from '%s.%s'.", examplesSupplier, field.getName()),
          e);
      return Optional.empty();
    }
  }
}
