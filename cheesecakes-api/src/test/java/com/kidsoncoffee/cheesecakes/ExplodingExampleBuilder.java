package com.kidsoncoffee.cheesecakes;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExplodingExampleBuilder extends Example.Builder {
  public ExplodingExampleBuilder(
      Class featureClass, String scenarioMethodName, List<Parameter.Schema> schema) {
    super(featureClass, scenarioMethodName, schema);
    throw new UnsupportedOperationException("KA-BOOM!!!");
  }
}
