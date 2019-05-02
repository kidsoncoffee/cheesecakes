package com.kidsoncoffee.cheesecakes.runner.example;

import com.kidsoncoffee.cheesecakes.Example;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class DataTableExamplesLoader implements ExamplesLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(DataTableExamplesLoader.class);

  public List<Example.Builder> load(final Class featureClass) {
    final Reflections reflections = new Reflections(featureClass.getPackage().getName());
    final Set<Class<? extends Example.Source>> examplesSuppliers =
        reflections.getSubTypesOf(Example.Source.class);

    for (final Class<? extends Example.Source> examplesSupplier : examplesSuppliers) {
      final List<Example.Builder> examples =
          Arrays.stream(examplesSupplier.getDeclaredMethods())
              .filter(method -> method.isAnnotationPresent(Example.Supplier.class))
              .map(method -> retrieveExample(examplesSupplier, method))
              .filter(Optional::isPresent)
              .map(Optional::get)
              .filter(example -> example.getFeatureClass().equals(featureClass))
              .collect(Collectors.toList());

      if (!examples.isEmpty()) {
        return examples;
      }
    }

    return Collections.emptyList();
  }

  private static Optional<Example.Builder> retrieveExample(
      Class<? extends Example.Source> examplesSupplier, Method method) {
    try {
      method.setAccessible(true);
      return Optional.of((Example.Builder) method.invoke(examplesSupplier, null));
    } catch (IllegalAccessException | InvocationTargetException e) {
      LOGGER.warn(
          String.format(
              "Error while retrieving examples from '%s.%s'.", examplesSupplier, method.getName()),
          e);
      return Optional.empty();
    }
  }
}
