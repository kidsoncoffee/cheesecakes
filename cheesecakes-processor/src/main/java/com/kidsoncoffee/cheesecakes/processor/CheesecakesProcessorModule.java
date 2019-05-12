package com.kidsoncoffee.cheesecakes.processor;

import com.google.inject.AbstractModule;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class CheesecakesProcessorModule extends AbstractModule {

  private final ProcessingEnvironment processingEnv;

  public CheesecakesProcessorModule(final ProcessingEnvironment processingEnv) {
    this.processingEnv = processingEnv;
  }

  @Override
  protected void configure() {
    //TODO fchovich move these to fields
    bind(Elements.class).toInstance(this.processingEnv.getElementUtils());
    bind(Types.class).toInstance(this.processingEnv.getTypeUtils());
    bind(Filer.class).toInstance(this.processingEnv.getFiler());
    /*bind(FeaturesAggregator.class).to(FeaturesAggregator.class);
    bind(ExamplesExtractor.class).to(ExamplesExtractor.class);
    bind(JavadocRetriever.class).to(JavadocRetriever.class);
    bind(JavadocValidator.class).to(JavadocValidator.class);
    bind(JavadocParser.class).to(JavadocParser.class);
    bind(ParameterGrouping.class).to(ParameterGrouping.class);*/
  }
}
