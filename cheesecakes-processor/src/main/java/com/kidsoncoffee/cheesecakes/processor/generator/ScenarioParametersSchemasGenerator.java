package com.kidsoncoffee.cheesecakes.processor.generator;

import com.kidsoncoffee.cheesecakes.Parameter.SchemaSource;
import com.kidsoncoffee.cheesecakes.Scenario.StepType;
import com.kidsoncoffee.cheesecakes.processor.domain.Feature;
import com.kidsoncoffee.cheesecakes.processor.domain.Parameter;
import com.kidsoncoffee.cheesecakes.processor.domain.Scenario;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ScenarioParametersSchemasGenerator {

  private static final TypeName PARAMETER_TYPE =
      TypeName.get(com.kidsoncoffee.cheesecakes.Parameter.Schema.class);

  private final Filer filer;

  public ScenarioParametersSchemasGenerator(final Filer filer) {
    this.filer = filer;
  }

  public Map<Scenario, ClassName> generate(final Feature feature) {
    final ClassName schemaSourceClassName =
        ClassName.get(
            feature.getTestClassPackage(),
            format("%s_%s", feature.getTestClassName(), SchemaSource.class.getSimpleName()));

    final List<TypeSpec> innerClasses = new ArrayList<>();
    final Map<Scenario, ClassName> generatedClassNames = new HashMap<>();
    for (Scenario scenario : feature.getScenarios()) {
      //      final AnnotationSpec bindingAnnotation = createSchemaBindingAnnotation(feature,
      // scenario);
      final Pair<ClassName, TypeSpec> schemaEnum =
          createSchemaEnumConstant(schemaSourceClassName, scenario);
      innerClasses.add(schemaEnum.getRight());
      generatedClassNames.put(scenario, schemaEnum.getLeft());
    }

    final TypeSpec featureClass =
        TypeSpec.classBuilder(schemaSourceClassName)
            .addSuperinterface(TypeName.get(SchemaSource.class))
            .addTypes(innerClasses)
            .build();

    try {
      // TODO fchovich ADD COMMENTS TO GENERATED CLASS
      JavaFile.builder(feature.getTestClassPackage(), featureClass)
          .addStaticImport(Arrays.class, "asList")
          .build()
          .writeTo(this.filer);
    } catch (IOException e) {
      throw new UncheckedIOException(format("Error generating '%s'.", Feature.class), e);
    }
    return generatedClassNames;
  }

  //  private static AnnotationSpec createSchemaBindingAnnotation(Feature feature, Scenario
  // scenario) {
  //    return AnnotationSpec.builder(SchemaGeneration.class)
  //        .addMember("testClass", "$N.class", feature.getTestClassName())
  //        .addMember("testMethod", "$S", scenario.getTestMethod())
  //        .build();
  //  }

  private static Pair<ClassName, TypeSpec> createSchemaEnumConstant(
      final ClassName featureClassName, final Scenario def) {
    final String scenarioName = WordUtils.capitalize(def.getTestMethod());

    final ParameterSpec nameParameter =
        ParameterSpec.builder(String.class, "name", Modifier.FINAL).build();
    final ParameterSpec typeParameter =
        ParameterSpec.builder(Class.class, "type", Modifier.FINAL).build();
    final ParameterSpec stepParameter =
        ParameterSpec.builder(StepType.class, "step", Modifier.FINAL).build();
    final ParameterSpec overallOrderParameter =
        ParameterSpec.builder(int.class, "overallOrder", Modifier.FINAL).build();

    final FieldSpec nameField = FieldSpec.builder(String.class, "name", Modifier.FINAL).build();
    final FieldSpec typeField = FieldSpec.builder(Class.class, "type", Modifier.FINAL).build();
    final FieldSpec stepField = FieldSpec.builder(StepType.class, "step", Modifier.FINAL).build();
    final FieldSpec overallOrderField =
        FieldSpec.builder(int.class, "overallOrder", Modifier.FINAL).build();

    final ClassName scenarioClassName =
        ClassName.get(featureClassName.toString(), format("%sSchema", scenarioName));
    final TypeSpec.Builder enumBuilder =
        TypeSpec.enumBuilder(scenarioClassName)
            //            .addAnnotation(bindingAnnotation)
            .addSuperinterface(PARAMETER_TYPE)
            .addFields(asList(nameField, typeField, stepField, overallOrderField))
            .addMethod(
                MethodSpec.constructorBuilder()
                    .addParameters(
                        asList(nameParameter, typeParameter, stepParameter, overallOrderParameter))
                    .addStatement("this.$N = $N", nameField, nameParameter)
                    .addStatement("this.$N = $N", typeField, typeParameter)
                    .addStatement("this.$N = $N", stepField, stepParameter)
                    .addStatement("this.$N = $N", overallOrderField, overallOrderParameter)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getName")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", nameField)
                    .returns(String.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getType")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", typeField)
                    .returns(Class.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getStep")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", stepField)
                    .returns(StepType.class)
                    .build())
            .addMethod(
                MethodSpec.methodBuilder("getOverallOrder")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class)
                    .addStatement("return this.$N", overallOrderField)
                    .returns(int.class)
                    .build());

    def.getParameters().forEach(p -> createParameterSchemaConstant(enumBuilder, p));
    return Pair.of(scenarioClassName, enumBuilder.build());
  }

  private static TypeSpec.Builder createParameterSchemaConstant(
      final TypeSpec.Builder enumBuilder, final Parameter r) {
    return enumBuilder.addEnumConstant(
        r.getName().toUpperCase(),
        TypeSpec.anonymousClassBuilder(
                "$S, $T.class, $T.$L, $L",
                r.getName(),
                r.getType(),
                StepType.class,
                r.getStepType().name(),
                r.getOverallOrder())
            .build());
  }
}
