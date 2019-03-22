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

  // TODO fchovich REMOVE THIS. USE REFLECTION TO INJECT.
  List<Supplier> getInjectionables();
}
