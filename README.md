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
  <a href="#download">Download</a> •
  <a href="#credits">Credits</a> •
  <a href="#related">Related</a> •
  <a href="#license">License</a>
</p>

_ ADD HERE AN EYE CATCHING FEATURE

## Key Features

* Write test scenarios using *data-driven* tables
* Injects requisites and expectations directly into the test method parameters
* Use auto-generated builders to write scenarios programmatically

## Quickstart

The following steps goes through all aspects of creating *data-driven* scenarios using **Cheesecakes**. 

All throughout the instructions are links to more advanced features or in-depth explanation of a particular concept. 

We assume that **you are already familiar with Junit** (if that's not the case give [this](https://junit.org/junit4/) a reading) but if you never used **Junit parameterized tests**, it is recommended that you follow this quickstart and then go back and customize to fit your needs.

### Import `cheesecakes-all` as a dependency

### Add **Cheesecakes** as the custom runner

```diff
+ import com.kidsoncoffee.cheesecakes.Cheesecakes;

+ @RunWith(Cheesecakes.class)
public class MyTest {

  public void test(){
  } 
}

```

### Write the functional specification

```diff
import com.kidsoncoffee.cheesecakes.Cheesecakes;

@RunWith(Cheesecakes.class)
public class MyTest {
  
+  /**
+   * Checks that the first and last name are concatenated correctly.
+   *
+   * <pre>
+   * Where:
+   * 
+   * firstName | lastName || completeName
+   * --------- | -------- || -------------
+   * John      | Doe      || John Doe
+   * Henry     | Rollins  || Henry Rollins
+   * </pre>
+   */
  public void test(){
  } 
}
```

On the method's javadoc, as the last piece of information, inside `pre` tags, add the keyword `Where:`. The keyword informs **Cheesecakes** that every line after it in the *Javadoc*, will be interpreted as a **data-driven table**. The table will require:
* To contain a header row, a separator row and at least one **example** row. 
* The number of columns in every row must be the same.
* That all requisites are in the left of a double pipe symbol (`||`) and all expectations are on the right.

You can do this step after writing the test logic as well.

### Write the test logic
  
As you proceed to write the test logic you can declare the method's parameters that will store the values defined in your examples. The name of the parameters must match only one name in the **data-driven table**'s header row.

### Run the tests

* How to run tests in intellij
* How to run tests in maven

## Deep Dive

## Download

## Credits

## Related

## License

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

