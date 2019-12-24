package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.parameter.ExampleParametersResolver;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Unit tests of {@link InvokeExampleMethod}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeExampleMethodTest {

  /** Indicates if the method {@link #methodReference(String, int)} was invoked. */
  private final AtomicBoolean methodInvoked = new AtomicBoolean(false);

  /** Reset if the method was invoked. See {@link #methodReference(String, int)}. */
  @Before
  public void resetMethodInvoked() {
    this.methodInvoked.set(false);
  }

  /** Checks that an exception is thrown when no parameters are resolved. */
  @Test
  public void noParametersResolved() {
    final Example.Builder example = new Example.Builder(null, null, null);
    final Method testMethod = retrieveMethod("methodReference");
    final ExampleParametersResolver exampleParametersResolver =
        Mockito.mock(ExampleParametersResolver.class);

    final InvokeExampleMethod invoker =
        new InvokeExampleMethod(
            exampleParametersResolver, new FrameworkMethod(testMethod), this, example);

    Mockito.when(exampleParametersResolver.resolve(testMethod, example))
        .thenReturn(Optional.empty());

    final AbstractThrowableAssert<?, ? extends Throwable> expectedException =
        Assertions.assertThatThrownBy(invoker::evaluate);

    expectedException
        .isInstanceOf(CheesecakesException.class)
        .hasMessage(
            "Unable to invoke test. Incorrect parameters for 'methodReference' in 'class com.kidsoncoffee.cheesecakes.runner.InvokeExampleMethodTest'.");
  }

  /**
   * Checks that the scenario method is invoked when parameters of the same size and type are
   * resolved.
   *
   * @throws Throwable If any error occurs.
   */
  @Test
  public void parametersResolved() throws Throwable {
    final Example.Builder example = new Example.Builder(null, null, null);
    final Method testMethod = retrieveMethod("methodReference");
    final ExampleParametersResolver exampleParametersResolver =
        Mockito.mock(ExampleParametersResolver.class);

    final InvokeExampleMethod invoker =
        new InvokeExampleMethod(
            exampleParametersResolver, new FrameworkMethod(testMethod), this, example);

    Mockito.when(exampleParametersResolver.resolve(testMethod, example))
        .thenReturn(Optional.of(new Object[] {"A", 1}));

    invoker.evaluate();

    Assertions.assertThat(this.methodInvoked).isTrue();
  }

  /**
   * The scenario method used by tests to simulate invocation.
   *
   * @param a The first parameter.
   * @param b The second parameter.
   */
  public void methodReference(final String a, final int b) {
    this.methodInvoked.set(true);
  }

  /**
   * Returns a method with the given name from {@link ParameterConverterMethodsProvider}.
   *
   * @param methodName The name of the method to retrieve.
   * @return The method that matches the name.
   */
  private static Method retrieveMethod(final String methodName) {
    return Arrays.stream(InvokeExampleMethodTest.class.getDeclaredMethods())
        .filter(m -> m.getName().equals(methodName))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "The setup for this test is incorrect. Method not found."));
  }
}
