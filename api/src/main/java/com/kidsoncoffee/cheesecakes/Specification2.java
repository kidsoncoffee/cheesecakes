package com.kidsoncoffee.cheesecakes;

import org.immutables.value.Value;

import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public abstract class Specification2 implements Spec {

  public abstract List<SpecificationParameter> getSchema();

  @Value.Auxiliary
  public List<SpecificationParameter> getRequisites() {
    return this.getSchema();
  }

  @Value.Auxiliary
  public List<SpecificationParameter> getExpectations() {
    return this.getSchema();
  }
}
