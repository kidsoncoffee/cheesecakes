package com.kidsoncoffee.paramtests.examples;

import com.kidsoncoffee.paramtests.annotations.BDDParameters.Expectations;
import com.kidsoncoffee.paramtests.annotations.BDDParameters.Requisites;
import org.junit.Ignore;
import org.junit.Test;

import java.util.function.Function;

import static com.kidsoncoffee.paramtests.examples.SingleTestCaseTestDefinitionExampleTest.SingleTestCaseTestDefinitionExampleTestParameters.given;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Ignore
public class SingleTestCaseTestDefinitionExampleTest {

  // TODO fchovich create analysis tool for syntatic sugar
  @SingleTestCase
  private static final TestCase testCase =
      given().name("John").surname("Doe").then().fullname("John Doe");

  @SingleTestCase
  private static final TestCase testCase2 =
      given().name("Mary").surname("Doe").then().fullname("Mary Doe");

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

  interface TestCase {
    TestCase get();
  }

  /* GENERATED */

  @interface SingleTestCase {
    String definedBy() default "singleTestCase";
  }

  static class SingleTestCaseTestDefinitionExampleTestParameters implements TestCase {

    static final Requisites given() {
      return new SingleTestCaseTestDefinitionExampleTestParameters().getRequisites();
    }

    private final Requisites requisites;
    private final Expectations expectations;
    private Function action;

    public SingleTestCaseTestDefinitionExampleTestParameters() {
      this.requisites = new Requisites(this);
      this.expectations = new Expectations(this);
    }

    class Requisites implements TestCase {
      private final TestCase testCase;

      /* CUSTOM */
      private String name;

      private String surname;

      public Requisites(TestCase testCase) {
        this.testCase = testCase;
      }

      public String getName() {
        return name;
      }

      public Requisites name(String name) {
        this.name = name;
        return this;
      }

      public String getSurname() {
        return surname;
      }

      public Requisites surname(String surname) {
        this.surname = surname;
        return this;
      }

      Expectations then() {
        return expectations;
      }

      Expectations when(Function method) {
        action = method;
        return expectations;
      }

      @Override
      public TestCase get() {
        return this.testCase;
      }
    }

    class Expectations implements TestCase {
      private final TestCase testCase;

      /* CUSTOM */
      private String fullName;

      public Expectations(TestCase testCase) {
        this.testCase = testCase;
      }

      public String getFullName() {
        return fullName;
      }

      public Expectations fullname(String fullName) {
        this.fullName = fullName;
        return this;
      }

      @Override
      public TestCase get() {
        return this.testCase;
      }
    }

    @Override
    public TestCase get() {
      return this;
    }

    public Requisites getRequisites() {
      return requisites;
    }

    public Expectations getExpectations() {
      return expectations;
    }
  }
}
