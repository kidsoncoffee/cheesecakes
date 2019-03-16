package com.kidsoncoffee.paramtests;

import com.kidsoncoffe.paramtests.BDDParameters.Requisites;
import com.kidsoncoffe.paramtests.TestCase;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author fernando.chovich
 * @since 1.0
 */
@Ignore
public class ExampleTest {

  @TestCase private static final GeneratedBDDParameters.a requisites = GeneratedBDDParameters.a()
          .name("John")
          .surname("Doe");

  @Test
  public void a(
      @Requisites final String name,
      @Requisites final String surname,
      final String expectedFullName) {

    final String fullName;

    when:
    fullName = String.format("%s %s", name, surname);

    then:
    assert fullName.equals(expectedFullName);
  }
}
