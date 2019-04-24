**THIS IS A WORK IN PROGRESS.. CHECK OUT FOR THE FULL BETA RELEASE IN 29/04/2019**

<h1 align="center">
  <br>
  <img src="https://cdn.pixabay.com/photo/2017/11/28/00/45/cheesecake-2982634_960_720.png" alt="Cheesecakes" width="400">
  <br>
  Cheesecakes
  <br>
</h1>

<h4 align="center">A data-driven testing framework built on top of <a href="https://junit.org/junit4/" target="_blank">Junit</a>.</h4>

<p align="center">
  <a href="#">
    <img alt="Maven Central" src="https://img.shields.io/maven-central/v/com.kidsoncoffee.cheesecakes/cheesecakes-all.svg"/>
  </a>
  <a href="https://travis-ci.org/kidsoncoffee/cheesecakes">
      <img src="https://travis-ci.org/kidsoncoffee/cheesecakes.svg?branch=master"/>
  </a>
  <a href="https://www.codacy.com/app/fernandochovich/cheesecakes?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=kidsoncoffee/cheesecakes&amp;utm_campaign=Badge_Grade">
      <img src="https://api.codacy.com/project/badge/Grade/d06b366b33a74e1ba180a44fe68d20cd"/>
  </a>
  <a href="https://github.com/kidsoncoffee/cheesecakes/issues">
      <img src="https://img.shields.io/github/issues/kidsoncoffee/cheesecakes.svg"/>
  </a>
  <a href="#">
      <img src="https://img.shields.io/badge/contributions-welcome-orange.svg"/>
  </a>
  <a href="https://opensource.org/licenses/MIT">
      <img src="https://img.shields.io/badge/license-MIT-blue.svg"/>
  </a>
  <br/>
  <a href="https://gitter.im/cheesecakes-ddt">
    <img src="https://badges.gitter.im/cheesecakes-ddt.svg"/>
  </a>
  <a href="https://saythanks.io/to/kidsoncoffee">
      <img src="https://img.shields.io/badge/SayThanks.io-%E2%98%BC-1EAEDB.svg"/>
  </a>
</p>

<p align="center">
  <a href="#key-features">Key features</a> •
  <a href="#quickstart">Quickstart</a> •
  <a href="#deep-dive">Deep dive</a> •
  <a href="#under-the-hood">Under the hood</a> •
  <a href="#download">Download</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> 
</p>

_ ADD HERE AN EYE CATCHING FEATURE

## Key Features

* Write test scenarios using *data-driven* tables, with the test method **Javadoc** as the single source of information
* Injects requisites and expectations directly into the test method parameters
* Use auto-generated builders to write scenarios programmatically

## Quickstart

The following steps goes through all aspects of creating *data-driven* scenarios using **Cheesecakes**. 

All throughout the instructions are links to more advanced features or in-depth explanation of a particular concept. 

We assume that **you are already familiar with Junit** (if that's not the case give [this](https://junit.org/junit4/) a reading) but if you never used **Junit parameterized tests**, it is recommended that you follow this quickstart and then go back and customize to fit your needs.

### Import the dependency

#### Maven

```diff
<dependencies>

+    <dependency>
+      <groupId>com.kidsoncoffee.cheesecakes</groupId>
+      <artifactId>cheesecakes-all</artifactId>
+      <version>LATEST</version>
+      <scope>test</scope>
+    </dependency>

</dependencies>
```

#### Gradle

```diff
dependencies {

+  testImplementation 'com.kidsoncoffee.cheesecakes:cheesecakes-all:LATEST'

}
```

### Add the custom runner to your Junit test class

```diff
+ import com.kidsoncoffee.cheesecakes.Cheesecakes;

+ @RunWith(Cheesecakes.class)
public class MyTest {

  @Test
  public void test(){
  } 
}
```

### Write the test's scenario examples

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;

@RunWith(Cheesecakes.class)
public class MyTest {
  
+  /**
+   * Checks that the first and last name are concatenated correctly.
+   *
+   * <pre>
+   * Examples:
+   * 
+   * firstName | lastName || completeName
+   * --------- | -------- || --------------
+   * John      | Doe      || John Doe
+   * Exene     | Cervenka || Exene Cervenka
+   * </pre>
+   */
  @Test
  public void test(){
  } 
}
```

The test's scenario examples is written on the **Javadoc** of the test method. It requires:
* To be the last piece of information in the **Javadoc**.
* The keyword `Examples:`, which indicates that every line after it, in the same **Javadoc**, will be interpreted as a *data-driven table*, that we call **Scenario Examples**. The table requires:
  * To have a **header row**, a **separator row** and at least one **example row**. 
  * Columns to be separated by a single pipe symbol (`|`).
  * All rows to have the same number of columns.
  * All rows have all requisites in the left of a double pipe symbol (`||`) and all expectations on the right.

Optionally but recommended, it all needs to be inside `<pre>` tags, so you can control formatting.

This step can be done after writing the test logic as well.

### Write the test case logic

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;

@RunWith(Cheesecakes.class)
public class MyTest {
  
  /**
   * Checks that the first and last name are concatenated correctly.
   *
   * <pre>
   * Examples:
   * 
   * firstName | lastName || completeName
   * --------- | -------- || --------------
   * John      | Doe      || John Doe
   * Exene     | Cervenka || Exene Cervenka
   * </pre>
   */
  @Test
-  public void test(){
+  public void test(final String firstName, final String lastName, final String completeName){
+    final String actualCompletaName;
+    
+    when:
+    actualCompleteName = String.format("%s %s", firstName, lastName); 
+    
+    then:
+    assert actualCompleteName.equals(completeName);
  } 
}
```

While writing the test case logic you can access the values of a **scenario example** by declaring method parameters. They require:
* To have the same name as defined in the **scenario examples** header row.
* The name must match only one of the **scenario examples** header row.

### Run the tests

Now you should be able to run this test successfully and see that each **scenario example** run independently.

* [How to run tests in Intellij](https://www.jetbrains.com/help/idea/performing-tests.html)
* [How to run tests in Maven](http://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html)

## Deep Dive

### Concepts

In general, *Cheesecakes* concepts tries to be close to *[Gherkin](https://cucumber.io/docs/gherkin/reference/)* concepts, but as it needs to translate to *Java* code as well, some simplification needed to happen.

#### Feature

In *Gherkin*: 
> The purpose of the Feature keyword is to provide a high-level description of a software feature, and to group related scenarios.

In *Cheesecakes*, **a *Feature* is a test class that groups related scenarios.**

For example, a class named *Aggregator*, being the unit under test, has an acompannying class named *AggregatorTest*, which contains all test cases related to that unit. This *AggregatorTest* class is the *Feature*. 

#### Scenario

In *Gherkin*:

> This is a concrete example that illustrates a business rule. It consists of a list of steps.
> The keyword Scenario is a synonym of the keyword Example.

In *Cheesecakes*, **a *Scenario* is a test method that describe an expected behavior of the unit under test**. It also consists of a list of steps. 

A *Scenario* may be parameterized to indicate requisites and expectation values that are important for the execution of that test case. 

A *Scenario* may be executed more than once but with different input values.

#### Example

In *Gherkin*, a *Scenario* and an *Example* are synonyms but they also have a keyword to describe something more close to an *Example* in *Cheesecakes*. This is how a *Scenario Outline* is described:

> The Scenario Outline keyword can be used to run the same Scenario multiple times, with different combinations of values.

In *Cheesecakes*, **an *Example* is the definition of requisites and expectations values of a *Scenario***.

There are a couple of different ways to create *Examples* for a given *Scenario*:
* Programmatically with a builder-like syntax
* Through a data-driven table in the *Scenario* method *Javadoc*.

#### Summary

To help illustrate these concepts, we can consider the example of the *Quickstart* in the following manner:

_ ADD A ILLUSTRATION OF THE QUICKSTART AND CONCEPTS POINTED OUT _

### Creating an *Example* for a *Scenario*

#### Data tables

As shown in the *Quickstart*, data tables are probably the most effective way for writing and reading test cases that have similar behavior for different requisites and expectations.

```java
/**
 * <pre>                                                    // Pre-formatted text prevents the IDE from formatting
 * Examples:                                                // Mandatory line with the keyword Examples:
 *                                                          // Empty lines are ignored
 * requisiteA | requisiteB || expectationA | expectationB   // The data table header row
 * ---------- | ---------- || ------------ | ------------   // The data table separator row
 * 1          | 2          || A            | B              // The first example
 * 3          | 4          || X            | Y              // The second example
 * </pre>
 */                                                         
```

The example are written on the *Scenario* method *Javadoc*, but needs to follow these guidelines:
* The data table should be the last thing in the *Javadoc* (beside closing `<pre>`, `@param`, `@return` and other usual tags).
* Just before the data table the keyword `Examples:` should be present (empty lines are ignored).
* The data table should contain a *header row*, a *separator row* and at least one *example row*.
* The data table columns should be separated by:
  * A single pipe symbol (`|`) for all values, except for the case below.
  * A double pipe symbol (`||`) for requisites, on the left, and expectations, on the right.
* The data table should have the same number of columns

Optionally but recommended, wrap all above in `<pre>` tags, so you can control formatting.

#### Programmatically

##### One *Example* by field

During the annotation processing phase in the build, some classes are auto generated based on the *Scenario* method signature. This gives a lot of flexibility to the developer and can be very powerful when generating requisites and expectations values dynamically.

For example, for the *Feature* class in the *Quickstart*, the same *Examples* could be written like this:

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;
+ import static MyTestExampleParameters.Tests.given;

@RunWith(Cheesecakes.class)
public class MyTest {
  
+  private static final Example JOHN_DOE_EXAMPLE = given()
+                                                    .firstName("John")
+                                                    .lastName("Doe")
+                                                    .then()
+                                                      .completeName("John Doe");
                                                                      
+  private static final Example EXENE_CERVENKA_EXAMPLE = given()
+                                                          .firstName("Exene")
+                                                          .lastName("Cervenka")
+                                                          .then()
+                                                            .completeName("Exene Cervenka");
   
  /**
   * Checks that the first and last name are concatenated correctly.
-   *
-   * <pre>
-   * Examples:
-   * 
-   * firstName | lastName || completeName
-   * --------- | -------- || --------------
-   * John      | Doe      || John Doe
-   * Exene     | Cervenka || Exene Cervenka
-   * </pre>
   */
  @Test
  public void test(@Requisites   final String firstName, 
                   @Requisites   final String lastName, 
                   @Expectations final String completeName){
    final String actualCompletaName;
    
    when:
    actualCompleteName = String.format("%s %s", firstName, lastName); 
    
    then:
    assert actualCompleteName.equals(completeName);
  } 
}
```

An auto-generated *Example* class can be instantiated following the pattern: `<NAME_OF_THE_FEATURE_CLASS>ExampleParameters.<NAME_OF_THE_SCENARIO_METHOD>.given()`. For example, looking at the example above, for the *Feature* class **MyTest** and *Scenario* method **test**, an *Example* class is instantiated with **MyTestExampleParameters.Test.given()**. Note that the *Scenario* method equivalent in the *Example* class is in *Pascal Case*.

It is required that the *Example* field in the *Feature* class is declared as **static**.

##### Multiple *Examples* by field

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;
import static MyTestExampleParameters.Tests.given;
import static ...

@RunWith(Cheesecakes.class)
public class MyTest {

+ private static final Examples CONCATENATION_EXAMPLES = examples(
+                                                          given()
+                                                            .firstName("John")
+                                                            .lastName("Doe")
+                                                            .then()
+                                                              .completeName("John Doe"),
+                                                          given()
+                                                            .firstName("Exene")
+                                                            .lastName("Cervenka")
+                                                            .then()
+                                                              .completeName("Exene Cervenka")
+                                                        );
+ 
-  private static final Example JOHN_DOE_EXAMPLE = given()
-                                                    .firstName("John")
-                                                    .lastName("Doe")
-                                                    .then()
-                                                      .completeName("John Doe");
-        
-  private static final Example EXENE_CERVENKA_EXAMPLE = given()
-                                                          .firstName("Exene")
-                                                          .lastName("Cervenka")
-                                                          .then()
-                                                            .completeName("Exene Cervenka");
   
  /**
   * Checks that the first and last name are concatenated correctly.
   */
  @Test
  public void test(@Requisites   final String firstName, 
                   @Requisites   final String lastName, 
                   @Expectations final String completeName){
    final String actualCompletaName;
    
    when:
    actualCompleteName = String.format("%s %s", firstName, lastName); 
    
    then:
    assert actualCompleteName.equals(completeName);
  } 
}
```

You can also use the `Examples.examples` aggregator to have only one *Example* static field declared. Even though that may turn into identation hell in some cases, it might help legibility in others.

##### Binding *Examples* to *Scenarios*

It is possible to have more than one *Scenario* method in a *Feature* class, but if you are defining your *Examples* programmatically you'll need to add a binding annotation to both the *Example* and *Scenario*.

###### Binding by *String* identification

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;
import static MyTestExampleParameters.Tests.given;

@RunWith(Cheesecakes.class)
public class MyTest {
  
+  @Scenario(binding="successfulConcatenation")
  private static final Example JOHN_DOE_EXAMPLE = given()
                                                    .firstName("John")
                                                    .lastName("Doe")
                                                    .then()
                                                      .completeName("John Doe");
        
+  @Scenario(binding="successfulConcatenation")
  private static final Example EXENE_CERVENKA_EXAMPLE = given()
                                                          .firstName("Exene")
                                                          .lastName("Cervenka")
                                                          .then()
                                                            .completeName("Exene Cervenka");
   
  /**
   * Checks that the first and last name are concatenated correctly.
   */
  @Test
+  @Scenario(binding="successfulConcatenation")
  public void test(@Requisites   final String firstName, 
                   @Requisites   final String lastName, 
                   @Expectations final String completeName){
    final String actualCompletaName;
    
    when:
    actualCompleteName = String.format("%s %s", firstName, lastName); 
    
    then:
    assert actualCompleteName.equals(completeName);
  } 
}
```

This solution may add too much configuration boilerplate, which may become messy as the *Feature* class evolve.

###### Binding by *enum*

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;
import static MyTestExampleParameters.Tests.given;

@RunWith(Cheesecakes.class)
public class MyTest {
  
+  @Scenario(bindingScenario=MyTestScenarioBindings.TEST)
  private static final Example JOHN_DOE_EXAMPLE = given()
                                                    .firstName("John")
                                                    .lastName("Doe")
                                                    .then()
                                                      .completeName("John Doe");
        
+  @Scenario(bindingScenario=MyTestScenarioBindings.TEST)
  private static final Example EXENE_CERVENKA_EXAMPLE = given()
                                                          .firstName("Exene")
                                                          .lastName("Cervenka")
                                                          .then()
                                                            .completeName("Exene Cervenka");
   
  /**
   * Checks that the first and last name are concatenated correctly.
   */
  @Test
  public void test(@Requisites   final String firstName, 
                   @Requisites   final String lastName, 
                   @Expectations final String completeName){
    final String actualCompletaName;
    
    when:
    actualCompleteName = String.format("%s %s", firstName, lastName); 
    
    then:
    assert actualCompleteName.equals(completeName);
  } 
}
```

Note that there is no scenario binding information in the *Scenario* method and the *Examples* are passing an *enum* to its `@Scenario` annotation. This is another auto-generated class which lists all *Scenarios* in a *Feature*. This *enum* can be accessed with the pattern `<NAME_OF_THE_FEATURE_CLASS>ScenarioBindings`. For example, looking at the example above for the *Feature* class **MyTest** and the *Scenario* method **test**, a *Scenario Bindings* enum value can be accessed with **MyTestScenarioBindings.TEST**. Note that the *Scenario* method equivalent in the *Scenario Bindings* enum is in *Snake Case*.

### Parameter injection

## Under the hood

Under the hood, **Cheesecakes** uses annotation processing to generate custom classes based on the test case method. The same classes used to generate the scenarios based on a test case method **Javadoc**, can be used programmatically to define the test cases.

## Download

## Credits

## Related
