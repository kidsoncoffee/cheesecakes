package com.kidsoncoffee.paramtests;

import com.google.auto.service.AutoService;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestsDefinition;
import com.kidsoncoffee.paramtests.generator.ParameterizedTestsGenerator;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(ParameterizedTestsGenerator.class)
public class TestProcessor implements ParameterizedTestsGenerator {
  @Override
  public void init(ProcessingEnvironment processingEnvironment) {}

  @Override
  public List<Class<? extends Annotation>> getAnnotationsToProcess() {
    return null;
  }

  @Override
  public List<ParameterizedTestsDefinition> createDefinitions(List<Element> elements) {
    return null;
  }

  @Override
  public boolean generate(ParameterizedTestsDefinition definition) {
    return false;
  }
}
