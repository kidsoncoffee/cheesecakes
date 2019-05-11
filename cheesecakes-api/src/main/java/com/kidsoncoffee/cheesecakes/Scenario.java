package com.kidsoncoffee.cheesecakes;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public @interface Scenario {
  enum StepType {
    REQUISITE,
    EXPECTATION;
  }

  abstract class StepBlock {
    // TODO fchovich REMOVE THiS CLASS
    // TODO fchovich IS THIS AN USER API CLASS?
    private final Example.Builder example;

    public StepBlock(final Example.Builder example) {
      this.example = example;
    }

    public Example.Builder getExample() {
      return this.example;
    }
  }
}
