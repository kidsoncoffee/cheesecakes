package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import com.kidsoncoffee.cheesecakes.SpecificationBlock;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class TestCaseParameterResolver {
  public Object[] resolve(final TestCase testCase, final Method testMethod) {
    final List<Pair<Class, String>> fieldNames = resolveFieldNames(testMethod, testCase);
    return resolveParameterValues(fieldNames, testCase.getSpecification());
  }

  private Object retrieveParameterValue(final TestCase testCase, final Pair<Class, String> field) {
    if (Parameters.Requisites.class.isAssignableFrom(field.getLeft())) {
      return retrieveFromBlock(testCase.getSpecification().getRequisites(), field.getRight());
    }
    return retrieveFromBlock(testCase.getSpecification().getExpectations(), field.getRight());
  }

  // TODO fchovich MOVE Parameters.Binding TO RUNNER MODULE
  private static List<Pair<Class, String>> resolveFieldNames(
      final Method testMethod, final TestCase testCase) {
    if (testCase.getSpecification() instanceof Parameters.Binding) {
      return ((Parameters.Binding) testCase.getSpecification()).getParameterNames();
    }
    return resolveFieldNamesByAnnotation(testMethod);
  }

  private static List<Pair<Class, String>> resolveFieldNamesByAnnotation(Method testMethod) {
    final List<Pair<Class, String>> fieldNames = new ArrayList<>();

    // TODO fchovich VALidATE NO FIELD NAMES
    for (final Parameter parameter : testMethod.getParameters()) {
      if (parameter.isAnnotationPresent(Parameters.Requisites.class)) {
        fieldNames.add(
            Pair.of(
                Parameters.Requisites.class,
                parameter.getAnnotation(Parameters.Requisites.class).value()));
      } else {
        fieldNames.add(
            Pair.of(
                Parameters.Expectations.class,
                parameter.getAnnotation(Parameters.Expectations.class).value()));
      }
    }

    return fieldNames;
  }

  private static Object[] resolveParameterValues(
      final List<Pair<Class, String>> fieldNames, final Specification specification) {
    return fieldNames.stream()
        .map(field -> resolveParameterValue(specification, field))
        .toArray(Object[]::new);
  }

  private static Object resolveParameterValue(
      Specification specification, Pair<Class, String> field) {
    if (Parameters.Requisites.class.isAssignableFrom(field.getLeft())) {
      return retrieveFromBlock(specification.getRequisites(), field.getRight());
    } else {
      return retrieveFromBlock(specification.getExpectations(), field.getRight());
    }
  }

  private static Object retrieveFromBlock(final SpecificationBlock block, final String fieldName) {
    try {
      // TODO fchovich VALIDATE FIELD NAME
      final Field parameterField = block.getClass().getDeclaredField(fieldName);
      parameterField.setAccessible(true);

      final Object parameterValue = parameterField.get(block);
      return parameterValue;
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new IllegalStateException("", e);
    }
  }
}
