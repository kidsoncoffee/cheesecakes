package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Loads examples from an independent {@link Example.Source} class.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ClassExamplesLoader implements ExamplesLoader {

  /** The logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(ClassExamplesLoader.class);

  /**
   * Loads examples from a {@link Example.Source} class. Inside the class there should be a method
   * annotated with {@link Example.Supplier}, which is responsible for providing an example.
   *
   * <p>The example method:
   *
   * <ul>
   *   <li>Must be or extend {@link Example.Builder}
   *   <li>Must be static
   *   <li>Must have zero parameters
   *   <li>Feature class ({@link Example.Builder#getFeatureClass()} must match the feature class.
   * </ul>
   *
   * @param featureClass The feature class to load the example from.
   * @return An example optional.
   */
  public List<Example.Builder> load(final Class featureClass) {
    final Reflections reflections = new Reflections(featureClass.getPackage().getName());
    final Set<Class<? extends Example.Source>> examplesSuppliers =
        reflections.getSubTypesOf(Example.Source.class);

    return examplesSuppliers.stream()
        .map(Class::getDeclaredMethods)
        .flatMap(Arrays::stream)
        .filter(method -> method.isAnnotationPresent(Example.Supplier.class))
        .map(ClassExamplesLoader::retrieveExample)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .filter(example -> example.getFeatureClass().equals(featureClass))
        .collect(Collectors.toList());
  }

  /**
   * Retrieves an example optional from the method. In case of invalid format or error it will
   * return an empty optional.
   *
   * @param method The method to retrieve the example from.
   * @return An example optional.
   */
  private static Optional<Example.Builder> retrieveExample(final Method method) {
    if (!Modifier.isStatic(method.getModifiers())) {
      LOGGER.error(
          String.format(
              "Every method annotated with '%s' should be static. Found '%s' in '%s'.",
              Example.Supplier.class, method, method.getDeclaringClass()));
      return Optional.empty();
    }

    if (method.getParameters().length > 0) {
      LOGGER.error(
          String.format(
              "Every method annotated with '%s' should have no parameters. Found %s in '%s' in '%s'.",
              Example.Supplier.class,
              method.getParameterCount(),
              method,
              method.getDeclaringClass()));
      return Optional.empty();
    }

    if (!method.getReturnType().isAssignableFrom(Example.Builder.class)) {
      LOGGER.error(
          String.format(
              "Every method annotated with '%s' should return an instance assignable from '%s'. Found '%s' in '%s' in '%s'.",
              Example.Supplier.class,
              Example.Builder.class,
              method.getReturnType(),
              method,
              method.getDeclaringClass()));
      return Optional.empty();
    }

    try {
      method.setAccessible(true);
      return Optional.of((Example.Builder) method.invoke(method.getDeclaringClass(), null));
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.warn(
          String.format(
              "Error while retrieving examples from '%s.%s'.",
              method.getDeclaringClass(), method.getName()),
          e);
      return Optional.empty();
    }
  }
}
