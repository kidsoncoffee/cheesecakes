package com.kidsoncoffee.cheesecakes.processor.datadriven;

import com.kidsoncoffee.cheesecakes.DataDrivenScenario;
import com.kidsoncoffee.cheesecakes.Feature;
import com.kidsoncoffee.cheesecakes.Parameters;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.FeatureDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ScenarioDefinition;
import com.kidsoncoffee.cheesecakes.processor.datadriven.domain.ScenarioExampleDefinition;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class DataDrivenScenariosClassGenerator {

  private final Filer filer;

  public DataDrivenScenariosClassGenerator(Filer filer) {
    this.filer = filer;
  }

  public void generate(final FeatureDefinition feature) {
    final String scenariosClassName = format("%sDataDrivenScenarios", feature.getTestClassName());
    final ClassName scenariosType =
        ClassName.get(feature.getTestClassPackage(), scenariosClassName);

    final List<MethodSpec> scenarioSuppliers = generateSuppliers(feature);

    final TypeSpec dataDrivenClass =
        TypeSpec.classBuilder(scenariosType)
            .addSuperinterface(DataDrivenScenario.class)
            .addMethods(scenarioSuppliers)
            // .addTypes(scenariosTypes)
            .build();

    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(feature.getTestClassPackage(), dataDrivenClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(format("Error generating '%s'.", Feature.class), e);
    }
  }

  private static List<MethodSpec> generateSuppliers(final FeatureDefinition feature) {
    return feature.getScenarios().stream()
        .map(scenario -> generateSupplier(feature, scenario))
        .collect(Collectors.toList());
  }

  /*class aFeature {
    @TestCaseBinding.. class and method
      static final List<Map<String, String>> aScenario() {
          final List<Map<String, String>> scenarios = new ArrayList<>();

          //...
          final ImmutableMap.Builder<String, String> parameters = ImmutableMap.builder();
          scenarios.add(parameters.build());

          return scenarios;
      }
  }*/

  private static MethodSpec generateSupplier(
      final FeatureDefinition feature, final ScenarioDefinition scenario) {

    final ParameterizedTypeName parameterMapType =
        ParameterizedTypeName.get(Map.class, String.class, String.class);

    final ParameterizedTypeName listOfParametersType =
        ParameterizedTypeName.get(ClassName.get(List.class), parameterMapType);

    final AnnotationSpec bindingAnnotation =
        AnnotationSpec.builder(Parameters.ScenarioBinding.class)
            .addMember("testClass", "$N.class", feature.getTestClassName())
            .addMember("testMethod", "$S", scenario.getTestMethodName())
            .build();

    final MethodSpec.Builder builder =
        MethodSpec.methodBuilder(scenario.getTestMethodName())
            .addModifiers(Modifier.STATIC, Modifier.FINAL)
            .addAnnotation(bindingAnnotation)
            .returns(listOfParametersType)
            .addStatement("final $T scenarios = new $T<>()", listOfParametersType, ArrayList.class);

    final ParameterizedTypeName mapType =
        ParameterizedTypeName.get(Map.class, String.class, String.class);

    for (int i = 0; i < scenario.getExamples().size(); i++) {
      final ScenarioExampleDefinition example = scenario.getExamples().get(i);
      final String exampleVariableName = format("example_%s", i);

      builder.addStatement("final $T $N = new $T<>()", mapType, exampleVariableName, HashMap.class);
      example
          .getParameters()
          .forEach(
              (key, value) ->
                  builder.addStatement("$N.put($S, $S)", exampleVariableName, key, value));
      builder.addStatement("scenarios.add($N)", exampleVariableName);
    }

    return builder.addStatement("return scenarios").build();
  }
}
