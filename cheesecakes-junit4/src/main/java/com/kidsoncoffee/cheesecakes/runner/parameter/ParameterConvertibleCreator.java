package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Example;
import com.kidsoncoffee.cheesecakes.Parameter;

import java.lang.reflect.Method;
import java.util.Comparator;

import static com.kidsoncoffee.cheesecakes.ImmutableConvertible.convertableParameter;

/**
 * @author fernando.chovich
 * @since 1.0
 */
public class ParameterConvertibleCreator {
  public Parameter.Convertible[] create(
      final Method method, final Example.Builder example) {
    return example.getSchema().stream()
        .sorted(Comparator.comparingInt(Parameter.Schema::getOverallOrder))
        .map(
            schema ->
                convertableParameter()
                    .schema(schema)
                    .method(method)
                    .value(
                        example
                            .getValue(schema)
                            .toString()) // IN THIS CASE IS EXPECTED THE PARAMETER VALUE TO BE A
                                         // STRING
                    .build())
        .toArray(Parameter.Convertible[]::new);
  }
}
