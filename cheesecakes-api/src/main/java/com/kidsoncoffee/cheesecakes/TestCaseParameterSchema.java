package com.kidsoncoffee.cheesecakes;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface TestCaseParameterSchema {
  String getName();

  Class getType();

  SpecificationStepType getStep();
}
