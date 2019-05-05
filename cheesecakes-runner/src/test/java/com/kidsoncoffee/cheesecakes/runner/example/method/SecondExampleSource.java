package com.kidsoncoffee.cheesecakes.runner.example.method;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.example.ClassExamplesLoaderTest;

import java.util.Collections;

/**
 * Supplies examples for {@link ClassExamplesLoaderTest}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class SecondExampleSource implements Example.Source {

  /**
   * Valid example supplier for {@link Integer} (feature target class).
   *
   * @return A dummy example.
   */
  @Example.Supplier
  public static Example.Builder exampleSupplier() {
    return new Example.Builder(Integer.class, "SCENARIO", Collections.emptyList());
  }

  /**
   * Invalid example supplier. Example suppliers should return an instance assignable from {@link
   * Example.Builder}.
   *
   * @return A plain string.
   */
  @Example.Supplier
  public static String exampleNotString() {
    return "PLEASE IGNORE ME";
  }
}
