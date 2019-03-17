package com.kidsoncoffee.paramtests.examples;

import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Ignore
public class SingleTestCaseTestDefinitionExampleTest {

  // TODO fchovich create analysis tool for syntatic sugar
  // TODO fchovich if only one test case method we shouldn't have to specify method
  // @SingleTestCase
  // private static final TestCase TEST_CASE =
  //    given().name("John").surname("Doe").then().fullname("John Doe");

  // @SingleTestCase
  // private static final TestCase TEST_CASE_2 =
  //    given().name("Mary").surname("Doe").then().fullname("Mary Doe");

  @Test
  public void singleTestCase(
      @Requisites final String name,
      @Requisites final String surname,
      @Expectations final String expectedFullName) {

    final String fullName;

    when:
    fullName = String.format("%s %s", name, surname);

    then:
    assert fullName.equals(expectedFullName);
  }
}
