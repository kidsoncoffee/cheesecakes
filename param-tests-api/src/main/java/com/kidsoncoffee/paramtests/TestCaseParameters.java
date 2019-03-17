package com.kidsoncoffee.paramtests;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface TestCaseParameters {
  TestCaseParametersBlock getRequisites();

  TestCaseParametersBlock getExpectations();
}
