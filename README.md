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
  <a href="#related">Related</a> 
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
+   * Where:
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
* The keyword `Where:`, which indicates that every line after it, in the same **Javadoc**, will be interpreted as a *data-driven table*, that we call **Scenario Examples**. The table requires:
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
   * Where:
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

While writing the test case logic you can access the values in the example by declaring method parameters. They require:
* To have the same name as defined in the **scenario examples** header row.
* The name must match only one of the **scenario examples** header row.

### Run the tests

Now you should be able to run this test successfully and see that each **scenario example** run independently.

* [How to run tests in Intellij](https://www.jetbrains.com/help/idea/performing-tests.html)
* [How to run tests in Maven](http://maven.apache.org/surefire/maven-surefire-plugin/examples/single-test.html)

## Deep Dive

## Download

## Credits

## Related
