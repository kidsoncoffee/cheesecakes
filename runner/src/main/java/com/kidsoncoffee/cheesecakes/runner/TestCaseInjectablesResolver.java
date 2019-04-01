package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.SpecificationParameter;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class TestCaseInjectablesResolver {
  public Object[] resolve(final TestCase testCase, final Method testMethod) {
    return testCase.getSpecification().getSchema().stream()
        .map(SpecificationParameter::getName)
        .map(fieldName -> testCase.getSpecification().getValue(fieldName))
        .toArray(Object[]::new);
  }
}
