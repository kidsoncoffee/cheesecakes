package com.kidsoncoffee.cheesecakes.processor.aggregator.example;

import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ParameterToGenerate;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class JavadocValidator {
  public Optional<String> validate(
      final List<ParameterToGenerate> parameters, final List<String> scenarioJavadocLines) {
    return validateDataTableSize(scenarioJavadocLines)
        .map(Optional::of)
        .orElseGet(() -> validateNumberOfColumns(parameters, scenarioJavadocLines))
        .map(Optional::of)
        .orElseGet(() -> validateHeaderColumns(parameters, scenarioJavadocLines));
  }

  private static Optional<String> validateDataTableSize(final List<String> scenarioJavadocLines) {
    if (scenarioJavadocLines.isEmpty()) {
      return Optional.of("Empty data table for the scenario.");
    }

    if (scenarioJavadocLines.size() <= 3) {
      return Optional.of(
          "The data table should contain a header row, a separator row and at least one example row.");
    }

    if (!scenarioJavadocLines.get(1).trim().startsWith("---")) {
      return Optional.of(
          String.format(
              "The separator row must start with '---'. Found: %s.", scenarioJavadocLines.get(1)));
    }

    return Optional.empty();
  }

  private static Optional<String> validateNumberOfColumns(
      List<ParameterToGenerate> parameters, List<String> scenarioJavadocLines) {
    return IntStream.range(0, scenarioJavadocLines.size())
        .mapToObj(i -> validateNumberOfParameters(parameters.size(), i, scenarioJavadocLines))
        .filter(Objects::nonNull)
        .findFirst();
  }

  private static Optional<String> validateHeaderColumns(
      List<ParameterToGenerate> parameters, List<String> scenarioJavadocLines) {
    final List<String> parameterNames =
        parameters.stream().map(ParameterToGenerate::getName).collect(Collectors.toList());
    return Arrays.stream(StringUtils.split(scenarioJavadocLines.get(0), "|"))
        .map(String::trim)
        .filter(p -> !parameterNames.contains(p))
        .map(
            name ->
                format(
                    "The data table column '%s' does not match any scenario method parameter. Available: %s.",
                    name, parameterNames))
        .findFirst();
  }

  private static String validateNumberOfParameters(
      final int parametersSize, final int row, final List<String> scenarioJavadocLines) {
    final int count = StringUtils.countMatches(scenarioJavadocLines.get(row), "|");
    if (count + 1 != parametersSize) {
      return format(
          "The row '%s' does not have the same number of parameters (%s) than the method (%s). Found: %s.",
          row, count + 1, parametersSize, scenarioJavadocLines.get(0));
    }
    return null;
  }
}
