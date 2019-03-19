package com.kidsoncoffee.paramtests;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface TestCaseParameters {
  TestCaseParametersBlock getRequisites();

  TestCaseParametersBlock getExpectations();

  List<Supplier> getInjectionables();
}
