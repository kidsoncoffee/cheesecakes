package com.kidsoncoffee.cheesecakes.processor.generator;

import com.google.inject.Inject;
import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Feature;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.FeatureToGenerate;
import com.kidsoncoffee.cheesecakes.processor.aggregator.domain.ScenarioToGenerate;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.lang.String.format;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class DataTableExampleGenerator {

  private final Filer filer;

  @Inject
  public DataTableExampleGenerator(final Filer filer) {
    this.filer = filer;
  }

  public void generate(
      final FeatureToGenerate feature, Map<ScenarioToGenerate, ClassName> generatedSchemas) {
    final String scenariosClassName =
        format("%s_%s", feature.getTestClassName(), Example.Source.class.getSimpleName());
    final ClassName scenariosType =
        ClassName.get(feature.getTestClassPackage(), scenariosClassName);

    final List<MethodSpec> scenarioSuppliers = generateSuppliers(feature, generatedSchemas);

    final TypeSpec featureClass =
        TypeSpec.classBuilder(scenariosType)
            .addSuperinterface(Example.Source.class)
            .addMethods(scenarioSuppliers)
            .build();

    try {
      // TODO fchovich ADD @Generator TO CODE
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(feature.getTestClassPackage(), featureClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(format("Error generating '%s'.", Feature.class), e);
    }
  }

  private static List<MethodSpec> generateSuppliers(
      final FeatureToGenerate feature, final Map<ScenarioToGenerate, ClassName> generatedSchemas) {
    return feature.getScenarios().stream()
        .filter(scenario -> !scenario.getExamples().isEmpty())
        .map(scenario -> generateSupplier(feature, scenario, generatedSchemas.get(scenario)))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private static List<MethodSpec> generateSupplier(
      final FeatureToGenerate feature,
      final ScenarioToGenerate scenario,
      final ClassName generatedSchema) {

    final List<Pair<String, MethodSpec>> examples =
        IntStream.range(0, scenario.getExamples().size())
            .mapToObj(i -> createExampleMethod(feature, scenario, generatedSchema, i))
            .collect(Collectors.toList());

    return examples.stream().map(Pair::getRight).collect(Collectors.toList());
  }

  private static Pair<String, MethodSpec> createExampleMethod(
      final FeatureToGenerate feature,
      final ScenarioToGenerate scenario,
      final ClassName generatedSchema,
      final int id) {
    final String methodName = format("%s_%s", scenario.getTestMethod(), id);
    final MethodSpec.Builder method =
        MethodSpec.methodBuilder(methodName)
            .addAnnotation(Example.Supplier.class)
            .addModifiers(Modifier.STATIC, Modifier.FINAL)
            .returns(Example.Builder.class)
            .addStatement(
                "final $T example = new $T($L.$L.class, $S, asList($T.values()))",
                Example.Builder.class,
                Example.Builder.class,
                feature.getTestClassPackage(),
                feature.getTestClassName(),
                scenario.getTestMethod(),
                generatedSchema);
    scenario
        .getExamples()
        .get(id)
        .getValue()
        .forEach((key, value) -> method.addStatement("example.setValue($S,$S)", key, value));

    return Pair.of(methodName, method.addStatement("return example").build());
  }
}
