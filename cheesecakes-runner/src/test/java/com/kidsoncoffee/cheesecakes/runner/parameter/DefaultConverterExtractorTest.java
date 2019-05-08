package com.kidsoncoffee.cheesecakes.runner.parameter;

import com.kidsoncoffee.cheesecakes.Parameter;
import com.kidsoncoffee.cheesecakes.Scenario;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.kidsoncoffee.cheesecakes.ImmutableSchema.schema;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.times;

/**
 * Unit test for {@link DefaultConverterExtractor}.
 *
 * @author fernando.chovich
 * @since 1.0
 */
public class DefaultConverterExtractorTest {

  /** Checks that the extractor is able to assign a converter to the type in the schema. */
  @Test
  public void assignConverters() {
    final Parameter.RegistrableConverter converter;
    final List<Parameter.RegistrableConverter> converters;

    final DefaultConverterExtractor extractor;

    final Parameter.Schema stringParameter, integerParameter;
    final List<Parameter.Schema> parameters;

    final Optional<Parameter.Converter>[] parametersConverters;

    given:
    converter = Mockito.mock(Parameter.RegistrableConverter.class);
    converters = singletonList(converter);
    extractor = new DefaultConverterExtractor(converters);

    stringParameter =
        schema()
            .name("STRING_PARAMETER")
            .overallOrder(0)
            .step(Scenario.StepType.REQUISITE)
            .type(String.class)
            .build();

    integerParameter =
        schema()
            .name("INTEGER_PARAMETER")
            .overallOrder(1)
            .step(Scenario.StepType.EXPECTATION)
            .type(int.class)
            .build();

    parameters = Arrays.asList(stringParameter, integerParameter);

    orchestrate:
    Mockito.when(converter.test(String.class)).thenReturn(true);

    when:
    parametersConverters = extractor.extract(parameters);

    then:
    Assertions.assertThat(parametersConverters)
        .hasSize(2)
        .containsExactly(Optional.of(converter), Optional.empty());

    verification:
    Mockito.verify(converter, times(1)).test(String.class);
    Mockito.verify(converter, times(1)).test(int.class);
    Mockito.verifyNoMoreInteractions(converter);
  }
}
