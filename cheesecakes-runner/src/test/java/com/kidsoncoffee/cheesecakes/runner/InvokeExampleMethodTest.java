package com.kidsoncoffee.cheesecakes.runner;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.runner.parameter.ExampleParametersResolver;
import com.kidsoncoffee.cheesecakes.runner.parameter.converter.ParameterConverterMethodsProvider;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runners.model.FrameworkMethod;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class InvokeExampleMethodTest {

  private final AtomicBoolean methodInvoked = new AtomicBoolean(false);

  @Test
  public void noParametersResolved() throws Throwable {
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
