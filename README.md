# Cheesecakes

Data-driven junit tests

How about writing tests this way:

```Java
  /**
   *
   *
   * <pre>
   * WHERE:
   *
   * firstValue | secondValue | expectedSum
   * ---------- | ----------- | -----------
   * 1          | 2           | 3
   * 2          | 4           | 6
   * </pre>
   */
  @Test
  @DataDriven("The scenario....")
  public void dataDrivenTestCase(
      @Requisites final int firstValue,
      @Requisites final int secondValue,
      @Expectations final int expectedSum) {
    final int sum;

    when:
    sum = firstValue + secondValue;

    then:
    assert sum == expectedSum;
  }
```

Tables are usually better to ...

## Quickstart

### Import as a dependency

Add `cheesecakes-all` as a dependency to your project. See the latest release.

### Create a test case class

As you would with regular junit tests, create a test class, but add **Cheesecakes** as a custom runner.

* Introduction to junit

### Create a test case method

Create a test case method as you would with regular junit tests, the difference that it needs to be annotated with `@DataDriven`. This annotation tells the **Cheesecakes** junit runner to parse the method's java doc.

[//]: # (Does it need to be annotated by default?)

### Write the functional specification

On the method's javadoc, as the last piece of information, inside `pre` tags, add the keyword `Where:`. This informs **Cheesecakes** that every line in the javadoc, after the keyword, will be interpreted as a **data-driven table**. The table must contain a header row, a separator row and at least one **example** row. The number of columns in every row must be the same. You can do this step after writing the test logic as well.

### Write the test logic

As you proceed to write the test logic you can declare the method's parameters that will store the values defined in your examples. The name of the parameters must match only one name in the **data-driven table**'s header row.

[//]: # (Do the example need to specifiy @Requisites or @Expectations by default?)

### Run the test

If you are in...

