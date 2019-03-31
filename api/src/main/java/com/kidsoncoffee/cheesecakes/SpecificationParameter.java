package com.kidsoncoffee.cheesecakes;

import javax.lang.model.type.TypeMirror;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface SpecificationParameter {
    String getName();
    TypeMirror getType();
    SpecificationStep getStep();
}
