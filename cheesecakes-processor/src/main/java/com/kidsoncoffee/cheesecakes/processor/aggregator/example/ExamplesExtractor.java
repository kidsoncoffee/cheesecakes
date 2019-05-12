package com.kidsoncoffee.cheesecakes.processor.aggregator.example;

import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ExampleToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ParameterToGenerate;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.lang.model.element.Element;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ExamplesExtractor {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExamplesExtractor.class);

  private final JavadocRetriever javadocRetriever;
  private final JavadocValidator javadocValidator;
  private final JavadocParser javadocParser;

  @Inject
  public ExamplesExtractor(
      final JavadocRetriever javadocRetriever,
      final JavadocValidator javadocValidator,
      final JavadocParser javadocParser) {
    this.javadocRetriever = javadocRetriever;
    this.javadocValidator = javadocValidator;
    this.javadocParser = javadocParser;
  }

  public List<ExampleToGenerate> extract(
      final List<ParameterToGenerate> parameters, final Element scenario) {
    if (scenario.getAnnotation(Test.class) != null) {
      final List<String> javadocScenarioLines = this.javadocRetriever.retrieve(scenario);
      final Optional<String> error =
          this.javadocValidator.validate(parameters, javadocScenarioLines);

      if (error.isPresent()) {
        LOGGER.error(
            "Error while extracting examples from Javadoc for scenario method '{}'. {}",
            scenario.getSimpleName(),
            error.get());
        return emptyList();
      }

      return this.javadocParser.parse(javadocScenarioLines);
    }
    return emptyList();
  }
}
