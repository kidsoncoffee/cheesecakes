package com.kidsoncoffee.cheesecakes;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface SpecificationParameter {
  String getName();

  Class getType();

  SpecificationStepType getStep();
}
