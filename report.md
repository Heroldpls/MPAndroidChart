# Report for assignment 3

## Project

Name: MPAndroidChart

URL: https://github.com/PhilJay/MPAndroidChart

One or two sentences describing it
It is a library for plotting Android charts and graphs.

## Branches of the project
In the "master" branch the refactored code can be found, additionally, unittests can also be found there.
In the branch "coverage" you can find all of our branch coverage tools.

## Onboarding experience
1. How easily can you build the project? Briefly describe if everything worked as documented or not:  
    a. Did you have to install a lot of additional tools to build the software?  
    We had to install specifically Java 11 (which was not stated in the onboarding description), additionally we also had to install android studio, which probably
    isn't necessary, but it made development easier since it seems that IDE is what the project was made for. This could have been a recommended IDE in the README.  
    b. Were those tools well documented?  
    No, no documentation whatsoever.  
    c. Were other components installed automatically by the build script?  
    Yes, there is a gradle.build file that contains a bunch of libraries.  
    d. Did the build conclude automatically without errors?  
    It did conclude but with some warnings, such as:
    ```
    MPAndroidChart\MPChartLib\src\main\java\com\github\mikephil\charting\animation\ChartAnimator.java:5: error: cannot find symbol                                                 
    import androidx.annotation.RequiresApi;
                          ^                                                                                                                                                                          
    symbol:   class RequiresApi
    location: package androidx.annotation
    ```
    e. How well do examples and tests run on your system(s)?  
    There was an issue with one of the tests where we had to change a "." to a ",", but after that all tests passed.

2. Do you plan to continue or choose another project?  
Yes, but the original repo is not longer being maintained. Therefore someone else has created their own fork where people work, so we'll continue on the project but we'll pick an issue in the fork instead. The fork can found at:
```
https://github.com/AppDevNext/AndroidChart
```

Additional notes on onboarding:
There was no description whatsoever of how to contribute to the project. The only documentation showed how to use the library in your other project (and
that description was also very poor). The README could be improved by adding a description of how build and contribute to the project. Additionally,
there could also be a section for prerequisites, such as stating that Java 11 is required for the project.



## Complexity
Checking a specific function using lizard: 
```
lizard yourfile.java | grep "yourFunctionName"
```
Our four functions (in ```MPAndroidChart/MPChartLib/src/main/java/com/github/mikephil/charting/```):  
**Jannik**: renderer/LegendRenderer.java -> computeLegend() 
**Zyad**: listener/BarLineChartTouchListener.java -> onTouch()  
**Harald**: renderer/BarChartRenderer.java -> drawValues()  
**Amanda**: renderer/HorizontalBarChartRenderer.java -> drawValues()  

| Total nloc  | CCN | CCN (Counted manually)|
|-------|-----|-----------|
| 112 | 22  | 22  |
| 122   | 48  | 48    |
| 156   | 44  | 44 |
| 156   | 50  | 50 |

1. What are your results for five complex functions?
    * Did all methods (tools vs. manual count) get the same result?

CCN = #decision points + 1
Types of decision points:
if
else if
case (from switch case)
for
while
? (ternary operator)
&&
| |

| Student | Method  | `if` | `else if` | `case` | `for`  | `while` | `?` | `&&` | `\|\|` | total CCN|  
|---------|---------|----|---------|------|------|-------|---|----|--------|----------|
|Jannik|LegendRenderer.java -> computeLegend()|9|2|0|4|0|1|5|0|21 + 1 = 22|
|Zyad|listener/BarLineChartTouchListener.java -> onTouch()|20|2|6|0|0|4|5|10|47 + 1 = 48|
|Harald|renderer/BarChartRenderer.java -> drawValues()|18|1|0|4|1|8|6|5|43 + 1 = 44|
|Amanda|renderer/HorizontalBarChartRenderer.java -> drawValues()|23|1|0|4|1|12|6|2|49 + 1 = 50|

* Are the results clear?   
  Using the function M=#decision points + 1 where M is the cyclomatic complexity we found quite clear results.
  They're reliable since both methods (lizard and manual count) both resulted in the same values. 

2. Are the functions just complex, or also long?  
**Jannik**: Compared to the other methods mine was a bit shorter (112 lines) and less complex, but there was still a lot to improve.
**Harald**: The function is both complex and very long (156 lines).  
**Zyad**: The function is long and somewhat complex, but it’s not shocking for what it’s trying to achieve. (122 lines)

3. What is the purpose of the functions?

**Jannik**: Prepares and calculates all the necessary forms, labels, and colors for the legend of a chart.  
**Harald**: The purpose of the method is to draw the values of a given entry (containing multiple datasets) on the provided canvas.  
**Zyad**: The purpose of the method is to handle user touch interactions for charts, the touch gestures being dragging, pinch zooming, double tap zooming, flinging and single tap.  
**Amanda**: The purpose of drawValues() in HorizontalBarChartRenderer.java is to draw text and icons on or above the bars of horizontal bar charts. It does this by taking a canvas (a drawing surface) object as input and retrieving datasets of what should be drawn on it. It also applies the styling and positions the values that should be drawn.  

4. Are exceptions taken into account in the given measurements?  
   Yes, both for Lizard and manual counting, there were no exceptions in the functions we chose though.  
5. Is the documentation clear w.r.t. all the possible outcomes?  

**Jannik**: The documentation is very minimal. It would be better if different outcomes would be described more in detail.  
**Harald**: No, there is basically no information about the different outcomes, barely any documentation at all.  
**Zyad**: While the documentation does a decent job for functionality, it could be improved in terms of addressing possible outcomes. This would be especially useful in this case where there are many states that are transitioned to with gesture changes, and mTouchMode that also changes  
**Amanda**: Some parts of the method are better described than others but, over all, the documentation could be improved a lot. The majority of the branches do not have any documentation at all and also there is no method description.  

## Refactoring

Plan for refactoring complex code:  

**Jannik**: The computeLegend method was a good choice to refactor. Since it handled different types of ChartData, these functionalities could be moved into a separate methods (e.g. PieChartData, BarChartData).
**Harald**: My plan for refactoring the code is to put the part of the method handling stacked data into a separate method, which is then called within the drawValues method.
This will also increase the readability of the method immensely.  
**Zyad**: Since the method already did a lot of things, it was pretty obvious that I needed to move the conditional blocks into small single-responsibility functions. This not only improves CCN, but also readability and modularity.  
**Amanda**: To reduce the complexity of drawValues in HorizontalBarChartRenderer, I added four new methods and thereby breaked down the logic into smaller parts. First, a big if-else statement in drawValues was made into two method calls. If the bars in the chart are single bars, the method drawSingleValues will be called and if the bars are stacked, the method drawStackedValues will be called instead. The method drawStackedValues was however still relatively big and therefore it was splitted by the same principle. If there appears a bar that is single even though the rest are stacked, the method drawSingleEntry is called, otherwise drawStackedEntry is called.  



Estimated impact of refactoring (lower CC, but other drawbacks?).
Carried out refactoring (optional, P+):  
**Jannik**: reduced CCN of computeLegend() from 22 to 4 (reduced by ~82%).  
**Zyad**: reduced CCN of onTouch from 48 to 12 (reduced by 75%).  
**Harald**: reduced CCN from 44 to 17 (reduced by ~ 61%).  
**Amanda**: reduced CCN of drawValues in HorizontalBarChartRenderer from 50 to 5 (90%).  

Added methods when refactoring:

|Method|CCN|
|------|--|
|drawValues|5|
|drawSingleValues|13|
|drawStackedValues|7|
|drawSingleEntry|9|
|drawStackedEntry|20|



git diff ...

## Coverage

### Tools
Jacoco was used to measure the branch coverage. The plugin https://github.com/vanniktech/gradle-android-junit-jacoco-plugin was added so that Jacoco can measure the coverage of a Gradle project by executing its unit tests and then generate a report with the results.  
  
The initial branch coverage for our four chosen methods was 0% as measured with Jacoco. The total coverage of the entire project was 6%. Most files have zero coverage, however there are files that have a coverage that extends up to 100%. After the unit tests were added, the coverage of the entire project rose to …%.  
  
Document your experience in using a "new"/different coverage tool.  
How well was the tool documented? Was it possible/easy/difficult to integrate it with your build environment?

At first it was very hard to get Jacoco to work with the project because there were compatibility errors with the versions of it, Gradle and Java. But after we found the plugin, it was much easier to integrate it with the rest of the project especially since the plugin was well documented. When everything was set up correctly, it was also easy to use and it creates a report with the results that is straightforward and easy to read.  




### Your own coverage tool
**OBS!!**
Different members of the group implemented the coverage in different ways (specifically how to present the coverage). Therefore, three of the methods
(HorizontalBarChartRenderer -> drawValues(), computeLegend() and onTouch()) are printed normally, while BarChartRenderer -> drawValues() writes it to a file that is added to the folder where the tests are located. ```Branch_Coverage_BarChartRenderer_drawValues```.

Show a patch (or link to a branch) that shows the instrumented code to
gather coverage measurements.  
Branch: https://github.com/Heroldpls/MPAndroidChart  (we have the refactored code on the main (master) branch)
3 of the 4 refactored methods can be found in a folder called refactor. The method drawValues of the class BarChartRenderer.java is refactored within the original class.

git diff ...


### Evaluation

We take ternary operators (condition ? yes : no)  into account by checking ( if (condition) branches[i] = true ). We do not have exceptions in our code, but they could easily be taken into account by adding another branch if an exception is thrown. We do not take into account && and || operators, which would increase the quality. However, to do so we would have to rewrite the code, which the professor advised us to avoid.


## Coverage improvement

LegendRenderer -> computeLegend() (Jannik)
Previous branch coverage: 0%, since no test used this method.
I created a new test class and had to create data structures to be able to call the method directly. Then I wrote for each of the possible chart data structures a test to overall cover most of the branches.
Improved branch coverage: 64.7% (with 4 added test methods)

BarChartRenderer -> drawValues() (Harald)
Previous branch coverage: 0%, since no tests in the project us this method. I created four new tests, which increased the branch coverage to 17.2%. This seems quite low but there are many branches that need to be checked in this method.

HorizontalBarChartRenderer -> drawValues() (Amanda)
There was no coverage on this method so therefore there were no existing tests to expand on. To unit test the method and increase the coverage, I used Mockito to mock the canvas that method uses to draw on and most other dependencies. This made it easier to test the method without having to e.g. implement an actual canvas. I made four tests, the first one verifies that a value is drawn on the canvas as it should be. The three other tests verifies that nothing gets drawn on the canvas when there is nothing to draw, when drawing is not allowed or when drawing is not enabled. With these tests the coverage of the method increased from 0% to 25%.


Show the comments that describe the requirements for the coverage.

Report of old coverage: [link]

Report of new coverage: [link]

Test cases added:

To check the difference between our additions and the state prior to us forking, run the following:

```
git diff 0550d3f7907c635744c08ad8ff9c45e1b6281cc7 {commit sha you want to compare}
```


Number of test cases added: two per team member (P) or at least four (P+).

## Self-assessment: Way of working

After having worked together for several weeks, both the teamwork and also the usage of git is becoming more natural. This allows us to work more effectively and with more focus on the given assignments. Based on that, we currently consider ourselves to be in the “In Place” state of the Essence Standard Checklist. Spending more time with the tools and together in a team would contribute to making our workflow even more efficient and natural. We believe, however, that we are on a good path to reach the next level before the end of the course.


## Overall experience

We had limited experience working with open source projects before. The project taught us how to contribute to a large scale open source project, which was not easy, especially in the beginning. Understanding the project and getting to run the tests afforded some time and effort, but eventually we managed to complete all the tasks including measuring method complexity, adding new test cases and conducting a refactoring.


