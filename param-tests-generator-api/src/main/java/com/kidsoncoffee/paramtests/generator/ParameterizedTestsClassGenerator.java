package com.kidsoncoffee.paramtests.generator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public interface ParameterizedTestsClassGenerator<T extends ParameterizedTestClassDefinition> {

  void init(final ProcessingEnvironment processingEnvironment);

  List<Class<? extends Annotation>> getAnnotationsToProcess();

  List<T> createDefinitions(final List<Element> elements);

  boolean generate(final T definition);
}
