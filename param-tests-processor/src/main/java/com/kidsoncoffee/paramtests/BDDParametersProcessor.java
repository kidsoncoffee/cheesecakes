package com.kidsoncoffee.paramtests;

import com.google.auto.service.AutoService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.*;

import static javax.lang.model.SourceVersion.RELEASE_8;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@AutoService(Processor.class)
@SupportedSourceVersion(RELEASE_8)
public class BDDParametersProcessor extends AbstractProcessor {

  private Messager messager;
  private Filer filer;
  private Elements elementUtils;

  @Override
  public void init(ProcessingEnvironment processingEnv) {
    this.messager = processingEnv.getMessager();
    this.filer = processingEnv.getFiler();
    this.elementUtils = processingEnv.getElementUtils();
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    final Set<String> annotations = new HashSet<>();
    annotations.add(Requisites.class.getCanonicalName());
    annotations.add(Expectations.class.getCanonicalName());
    return annotations;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    final Table<PackageElement, Name, List<Element>> elements = HashBasedTable.create();
    for (final Element element : roundEnv.getElementsAnnotatedWith(Requisites.class)) {
      final PackageElement pkg = this.elementUtils.getPackageOf(element);
      final Name testCase = element.getEnclosingElement().getSimpleName();

      if (!elements.contains(pkg, testCase)) {
        elements.put(pkg, testCase, new ArrayList<>());
      }
      elements.get(pkg, testCase).add(element);
    }
    for (final Element element : roundEnv.getElementsAnnotatedWith(Expectations.class)) {
      final PackageElement pkg = this.elementUtils.getPackageOf(element);
      final Name testCase = element.getEnclosingElement().getSimpleName();

      if (!elements.contains(pkg, testCase)) {
        elements.put(pkg, testCase, new ArrayList<>());
      }
      elements.get(pkg, testCase).add(element);
    }

    System.out.println(elements);

    final List<MethodSpec> classMethods = new ArrayList<>();

    for (PackageElement packageElement : elements.rowKeySet()) {
      for (Map.Entry<Name, List<Element>> classes : elements.row(packageElement).entrySet()) {
        final Name clazz = classes.getKey();
        final List<Element> classesElements = classes.getValue();

        final ClassName className =
            ClassName.get(
                packageElement.getQualifiedName().toString(),
                String.format("%sParameters", clazz.toString()));

        final List<TypeSpec> testCaseSpecs = new ArrayList<>();
        for (Map.Entry<Name, List<Element>> testCases : elements.row(packageElement).entrySet()) {
          final Name testCase = testCases.getKey();
          final List<Element> testCaseElements = testCases.getValue();

          final ClassName testCaseClass =
              ClassName.get(className.reflectionName(), testCase.toString());

          classMethods.add(
              MethodSpec.methodBuilder(testCase.toString())
                  .addModifiers(Modifier.STATIC)
                  .returns(testCaseClass)
                  .addStatement("return new $T()", testCaseClass)
                  .build());

          final List<MethodSpec> testCaseMethods = new ArrayList<>();
          final List<FieldSpec> testCaseFields = new ArrayList<>();
          for (Element testCaseElement : testCaseElements) {
            final String elementName = testCaseElement.getSimpleName().toString();
            final TypeName elementType = TypeName.get(testCaseElement.asType());

            final FieldSpec fieldSpec = FieldSpec.builder(elementType, elementName).build();
            testCaseFields.add(fieldSpec);

            final ParameterSpec parameterSpec =
                ParameterSpec.builder(elementType, elementName)
                    .addModifiers(Modifier.FINAL)
                    .build();

            testCaseMethods.add(
                MethodSpec.methodBuilder(elementName)
                    .returns(testCaseClass)
                    .addParameter(parameterSpec)
                    .addStatement("this.$N = $N", fieldSpec, parameterSpec)
                    .addStatement("return this")
                    .build());

            testCaseMethods.add(
                MethodSpec.methodBuilder("get" + elementName)
                    .returns(elementType)
                    .addStatement("return this.$N", fieldSpec)
                    .build());
          }

          testCaseSpecs.add(
              TypeSpec.classBuilder(testCaseClass)
                  .addModifiers(Modifier.STATIC)
                  .addFields(testCaseFields)
                  .addMethods(testCaseMethods)
                  .build());
        }

        final TypeSpec typeSpec =
            TypeSpec.classBuilder(className)
                .addMethods(classMethods)
                .addTypes(testCaseSpecs)
                .build();
        try {
          JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec)
              .build()
              .writeTo(this.filer);
        } catch (IOException e) {
          throw new UncheckedIOException(e);
        }
      }
    }
    return false;
  }

  private void processTestCaseParameters(RoundEnvironment roundEnv) {
    final Table<PackageElement, Name, List<Element>> elements = HashBasedTable.create();
    for (final Element element : roundEnv.getElementsAnnotatedWith(Requisites.class)) {
      if (!element.getKind().equals(ElementKind.PARAMETER)) {
        this.messager.printMessage(
            Diagnostic.Kind.ERROR,
            String.format(
                "Only parameters can be annotated with @%s.", Requisites.class.getSimpleName()),
            element);
      }
      final PackageElement pkg = this.elementUtils.getPackageOf(element);
      final Name testCase = element.getEnclosingElement().getSimpleName();

      if (!elements.contains(pkg, testCase)) {
        elements.put(pkg, testCase, new ArrayList<>());
      }
      elements.get(pkg, testCase).add(element);
    }

    final List<MethodSpec> classMethods = new ArrayList<>();

    for (PackageElement packageElement : elements.rowKeySet()) {
      final ClassName className =
          ClassName.get(packageElement.getQualifiedName().toString(), "GeneratedBDDParameters");

      final List<TypeSpec> testCaseSpecs = new ArrayList<>();
      for (Map.Entry<Name, List<Element>> testCases : elements.row(packageElement).entrySet()) {
        final Name testCase = testCases.getKey();
        final List<Element> testCaseElements = testCases.getValue();

        final ClassName testCaseClass =
            ClassName.get(className.reflectionName(), testCase.toString());

        classMethods.add(
            MethodSpec.methodBuilder(testCase.toString())
                .addModifiers(Modifier.STATIC)
                .returns(testCaseClass)
                .addStatement("return new $T()", testCaseClass)
                .build());

        final List<MethodSpec> testCaseMethods = new ArrayList<>();
        final List<FieldSpec> testCaseFields = new ArrayList<>();
        for (Element testCaseElement : testCaseElements) {
          final String elementName = testCaseElement.getSimpleName().toString();
          final TypeName elementType = TypeName.get(testCaseElement.asType());

          final FieldSpec fieldSpec = FieldSpec.builder(elementType, elementName).build();
          testCaseFields.add(fieldSpec);

          final ParameterSpec parameterSpec =
              ParameterSpec.builder(elementType, elementName).addModifiers(Modifier.FINAL).build();

          testCaseMethods.add(
              MethodSpec.methodBuilder(elementName)
                  .returns(testCaseClass)
                  .addParameter(parameterSpec)
                  .addStatement("this.$N = $N", fieldSpec, parameterSpec)
                  .addStatement("return this")
                  .build());

          testCaseMethods.add(
              MethodSpec.methodBuilder("get" + elementName)
                  .returns(elementType)
                  .addStatement("return this.$N", fieldSpec)
                  .build());
        }

        testCaseSpecs.add(
            TypeSpec.classBuilder(testCaseClass)
                .addModifiers(Modifier.STATIC)
                .addFields(testCaseFields)
                .addMethods(testCaseMethods)
                .build());
      }

      final TypeSpec typeSpec =
          TypeSpec.classBuilder(className).addMethods(classMethods).addTypes(testCaseSpecs).build();
      try {
        JavaFile.builder(packageElement.getQualifiedName().toString(), typeSpec)
            .build()
            .writeTo(this.filer);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
