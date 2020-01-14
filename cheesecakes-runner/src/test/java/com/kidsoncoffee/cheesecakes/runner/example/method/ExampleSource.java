package com.kidsoncoffee.cheesecakes.runner.example.method;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.ClassExamplesLoader;
import com.kidsoncoffee.cheesecakes.runner.example.ClassExamplesLoaderTest;

import java.util.Collections;

/**
 * Supplies examples for {@link ClassExamplesLoaderTest}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class ExampleSource implements Example.Source {

  /** The name of the scenario for {@link #exampleSupplierNotStatic()}. */
  public static final String EXAMPLE_SUPPLIER_NOT_STATIC = "EXAMPLE_SUPPLIER_NOT_STATIC";

  /** The name of the scenario for {@link #exampleSupplierWithParameters(String)}. */
  public static final String EXAMPLE_SUPPLIER_WITH_PARAMETERS = "EXAMPLE_SUPPLIER_WITH_PARAMETERS";

  /** The name of the scenario for {@link #exampleSupplierTestClass()}. */
  public static final String EXAMPLE_SUPPLIER_STATIC_TEST_CLASS =
      "EXAMPLE_SUPPLIER_STATIC_TEST_CLASS";

  /** The name of the scenario for {@link #exampleSupplierMainClass()}. */
  public static final String EXAMPLE_SUPPLIER_STATIC_MAIN_CLASS =
      "EXAMPLE_SUPPLIER_STATIC_MAIN_CLASS";

  /**
   * Invalid example supplier. Examples suppliers should be static.
   *
   * @return A dummy example.
   */
  @Example.Supplier
  public Example.Builder exampleSupplierNotStatic() {
    return new Example.Builder(Integer.class.getName(), EXAMPLE_SUPPLIER_NOT_STATIC, Collections.emptyList());
  }

  /**
   * Invalid example supplier. Example suppliers should have no parameters.
   *
   * @param unused The invalid parameter.
   * @return A dummy example.
   */
  @Example.Supplier
  public static Example.Builder exampleSupplierWithParameters(final String unused) {
    return new Example.Builder(
        Integer.class.getName(), EXAMPLE_SUPPLIER_WITH_PARAMETERS, Collections.emptyList());
  }

  /**
   * Invalid example supplier. This will throw an exception in the top.
   *
   * @return A dummy example.
   */
  @Example.Supplier
  public static Example.Builder exampleSupplierException() {
    throw new UnsupportedOperationException("A failure should be handled.");
  }

  /**
   * Valid example supplier for {@link ClassExamplesLoaderTest} (feature target class).
   *
   * @return A dummy example.
   */
  @Example.Supplier
  public static Example.Builder exampleSupplierTestClass() {
    return new Example.Builder(
        ClassExamplesLoaderTest.class.getName(), EXAMPLE_SUPPLIER_STATIC_TEST_CLASS, Collections.emptyList());
  }

  /**
   * Valid example supplier for {@link ClassExamplesLoader} (feature target class).
   *
   * @return A dummy example.
   */
  @Example.Supplier
  public static Example.Builder exampleSupplierMainClass() {
    return new Example.Builder(
        ClassExamplesLoader.class.getName(), EXAMPLE_SUPPLIER_STATIC_MAIN_CLASS, Collections.emptyList());
  }
}
