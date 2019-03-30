package com.kidsoncoffee.cheesecakes;

import java.util.Arrays;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface Specification extends SpecificationReference, Spec {
  SpecificationBlock getRequisites();

  SpecificationBlock getExpectations();

  default List<Specification> getSpecificationReferences() {
    return Arrays.asList(this);
  }
}
