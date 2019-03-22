package com.kidsoncoffee.paramtests.generator;

import org.immutables.value.Value;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Value.Immutable
public interface BindingClass extends ParameterizedTestClassDefinition {
    String getTestClassPackage();
    String getTestClassName();
    String getTestMethodName();
}
