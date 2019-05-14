package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Loads examples from fields in the feature class.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class FieldExamplesLoader implements ExamplesLoader {

  /** The logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassExamplesLoader.class);

  /**
   * Loads examples from fields in the feature class. It is expected that the feature class itself
   * has example fields.
   *
   * <p>The example field:
   *
   * <ul>
   *   <li>Must be or extend from {@link Example.Builder}
   *   <li>Must be static
   *   <li>Feature class ({@link Example.Builder#getFeatureClass()}) must match the feature class
   * </ul>
   *
   * <p>Any other error will skip and log.
   *
   * @param featureClass The feature class to load the example from.
   * @return The list of examples matching the criteria explained above.
   */
  public List<Example.Builder> load(final Class featureClass) {
    return Arrays.stream(featureClass.getDeclaredFields())
        .filter(field -> field.getType().isAssignableFrom(Example.Builder.class))
        .map(FieldExamplesLoader::retrieveExample)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(example -> example.getFeatureClass().equals(featureClass))
        // TODO fchovich ADD LOGGER INFO
        .collect(Collectors.toList());
  }

  /**
   * Retrieves an example optional from the field. In case of invalid format or error it will return
   * an empty optional.
   *
   * @param field The field to retrieve the example from.
   * @return An example optional.
   */
  private static Optional<Example.Builder> retrieveExample(final Field field) {
    if (!Modifier.isStatic(field.getModifiers())) {
      LOGGER.warn(
          String.format(
              "Every field of type '%s' should be static. Found '%s' in '%s'.",
              Example.Builder.class, field, field.getDeclaringClass()));
      return Optional.empty();
    }
    try {
      field.setAccessible(true);
      return Optional.of((Example.Builder) field.get(null));
    } catch (Throwable e) {
      LOGGER.warn(
          String.format(
              "Error while retrieving examples from '%s.%s'.",
              field.getDeclaringClass(), field.getName()),
          e);
      return Optional.empty();
    }
  }
}
