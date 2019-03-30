package com.kidsoncoffee.cheesecakes;

import java.util.Arrays;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public abstract class SpecificationBlock implements SpecificationReference, Spec {

  private final Specification specification;

  public SpecificationBlock(Specification specification) {
    this.specification = specification;
  }

  public Specification getSpecification() {
    return this.specification;
  }

  @Override
  public List<Specification> getSpecificationReferences() {
    return Arrays.asList(this.specification);
  }
}
