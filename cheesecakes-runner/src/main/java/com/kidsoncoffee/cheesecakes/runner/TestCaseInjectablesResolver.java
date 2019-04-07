package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.TestCaseParameterSchema;

import java.lang.reflect.Method;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class TestCaseInjectablesResolver {
  public Object[] resolve(final TestCase testCase, final Method testMethod) {
    return testCase.getSpecification().getSchema().stream()
        .map(TestCaseParameterSchema::getName)
        .map(fieldName -> testCase.getSpecification().getValue(fieldName))
        .toArray(Object[]::new);
  }
}
