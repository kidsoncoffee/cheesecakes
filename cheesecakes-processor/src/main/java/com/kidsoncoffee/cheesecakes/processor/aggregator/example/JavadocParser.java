package com.kidsoncoffee.cheesecakes.processor.aggregator.example;

import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ExampleToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ImmutableExampleToGenerate;

import javax.lang.model.util.Elements;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.stream;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class JavadocParser {

  private final Elements elementUtils;

  @Inject
  public JavadocParser(final Elements elementUtils) {
    this.elementUtils = elementUtils;
  }

  public List<ExampleToGenerate> parse(final List<String> javadocScenarioLines) {
    final List<String> header =
        stream(javadocScenarioLines.get(0).split("\\|"))
            .map(String::trim)
            .collect(Collectors.toList());

    return javadocScenarioLines.subList(2, javadocScenarioLines.size()).stream()
        .map(s -> parseExample(s, header))
        .collect(Collectors.toList());
  }

  private static ExampleToGenerate parseExample(final String scenario, final List<String> header) {
    final String[] fields = scenario.split("\\|");
    final Map<String, String> values =
        IntStream.range(0, fields.length)
            .boxed()
            .collect(Collectors.toMap(header::get, i -> fields[i].trim()));
    return ImmutableExampleToGenerate.builder().putAllValue(values).build();
  }
}
