package com.kidsoncoffee.cheesecakes.processor.generator;

import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.Specification;
import com.kidsoncoffee.cheesecakes.processor.domain.Feature;
import com.kidsoncoffee.cheesecakes.processor.domain.Scenario;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
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
public class DataDrivenScenariosGenerator {

  private final Filer filer;

  public DataDrivenScenariosGenerator(final Filer filer) {
    this.filer = filer;
  }

  public void generate(final Feature feature, Map<Scenario, ClassName> generatedSchemas) {
    final String scenariosClassName = format("%s_DataDrivenScenarios", feature.getTestClassName());
    final ClassName scenariosType =
        ClassName.get(feature.getTestClassPackage(), scenariosClassName);

    final List<MethodSpec> scenarioSuppliers = generateSuppliers(feature, generatedSchemas);

    final TypeSpec featureClass =
        TypeSpec.classBuilder(scenariosType)
            .addSuperinterface(com.kidsoncoffee.cheesecakes.DataDrivenScenario.class)
            .addMethods(scenarioSuppliers)
            // .addTypes(scenariosTypes)
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
      final Feature feature, final Map<Scenario, ClassName> generatedSchemas) {
    return feature.getScenarios().stream()
        .filter(scenario -> !scenario.getExamples().isEmpty())
        .map(scenario -> generateSupplier(feature, scenario, generatedSchemas.get(scenario)))
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private static List<MethodSpec> generateSupplier(
      final Feature feature, final Scenario scenario, final ClassName generatedSchema) {

    final ParameterizedTypeName examplesType =
        ParameterizedTypeName.get(List.class, Specification.class);

    final AnnotationSpec bindingAnnotation =
        AnnotationSpec.builder(Parameters.ScenarioBinding.class)
            .addMember("testClass", "$N.class", feature.getTestClassName())
            .addMember("testMethod", "$S", scenario.getTestMethod())
            .build();

    final List<Pair<String, MethodSpec>> examples =
        IntStream.range(0, scenario.getExamples().size())
            .mapToObj(i -> createExampleMethod(scenario, generatedSchema, i))
            .collect(Collectors.toList());

    final String exampleMethods =
        examples.stream()
            .map(e -> String.format("%s()", e.getLeft()))
            .collect(Collectors.joining(","));
    final MethodSpec.Builder builder =
        MethodSpec.methodBuilder(scenario.getTestMethod())
            .addModifiers(Modifier.STATIC, Modifier.FINAL)
            .addAnnotation(bindingAnnotation)
            .returns(examplesType)
            .addStatement("return asList($L)", exampleMethods);

    final MethodSpec examplesSupplier = builder.build();
    final List<MethodSpec> methods = new ArrayList<>();
    methods.add(examplesSupplier);
    methods.addAll(examples.stream().map(Pair::getRight).collect(Collectors.toList()));

    return methods;
  }

  private static Pair<String, MethodSpec> createExampleMethod(
      final Scenario scenario, final ClassName generatedSchema, final int id) {
    final String methodName = format("%s_%s", scenario.getTestMethod(), id);
    final MethodSpec.Builder method =
        MethodSpec.methodBuilder(methodName)
            .addModifiers(Modifier.STATIC, Modifier.FINAL)
            .returns(Specification.class)
            .addStatement(
                "final $T example = new $T(asList($T.values()))",
                Specification.class,
                Specification.class,
                generatedSchema);
    scenario
        .getExamples()
        .get(id)
        .getValue()
        .forEach((key, value) -> method.addStatement("example.setValue($S,$S)", key, value));

    return Pair.of(methodName, method.addStatement("return example").build());
  }
}
