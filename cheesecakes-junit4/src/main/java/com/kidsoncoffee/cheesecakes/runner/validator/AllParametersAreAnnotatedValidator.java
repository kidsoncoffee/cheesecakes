package com.kidsoncoffee.cheesecakes.runner.validator;

import com.kidsoncoffee.cheesecakes.Parameter;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.TestClass;
import org.junit.validator.TestClassValidator;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class AllParametersAreAnnotatedValidator implements TestClassValidator {
  @Override
  public List<Exception> validateTestClass(TestClass testClass) {
    return testClass.getAnnotatedMethods(Test.class).stream()
        .filter(AllParametersAreAnnotatedValidator::parametersAreMissingAnnotation)
        .map(AllParametersAreAnnotatedValidator::parameterMissingException)
        .collect(Collectors.toList());
  }

  private static boolean parametersAreMissingAnnotation(final FrameworkMethod method) {
    return Arrays.stream(method.getMethod().getParameters())
        .anyMatch(parameter -> notRequisite(parameter) && notExpectation(parameter));
  }

  private static boolean notRequisite(java.lang.reflect.Parameter parameter) {
    return !parameter.isAnnotationPresent(Parameter.Requisite.class);
  }

  private static boolean notExpectation(java.lang.reflect.Parameter parameter) {
    return !parameter.isAnnotationPresent(Parameter.Expectation.class);
  }

  private static Exception parameterMissingException(FrameworkMethod method) {
    return new Exception(
        String.format(
            "The method '%s' does not have all parameters annotated with '%s' or '%s'.",
            method.getName(), Parameter.Requisite.class, Parameter.Expectation.class));
  }
}
