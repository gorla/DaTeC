# DaTeC

DaTeC can compute the dataflow def-use pairs coverage of a given test suite on a given Java program under test. 
To know more about how DaTeC works please refer to the papers listed below

# System requirements

DaTeC is a rather old prototype and it has been built and tested using Java 6. It may work well for newer Java versions, but we do not guarantee it will.

* Java 6
* Apache maven
* SQLite3

# Build and run DaTeC on the CoffeeMaker project

* Install DaTeC dependencies by running  `make install-dependencies`
* Build DaTeC by running `make package`
* To run the dataflow analysis and compute the def-use pairs of the `coffeemaker` example run `make runAnalysis`
* Instrument the Java code under test to trace def-use pairs coverage by running `make instrument`
* To compile and run the sample test case in the sample-test directory run `make runSampleTest`
* Generate the coverage reports by running `make generateReport`

# References

* **Contextual Integration Testing of Classes** (Giovanni Denaro, Alessandra Gorla, Mauro Pezzè), In FASE'08: Proceedings of the 11th International Conference on Fundamental Approaches to Software Engineering, 2008
* **DaTeC: Dataflow Testing of Java Classes** (Giovanni Denaro, Alessandra Gorla, Mauro Pezzè), In ICSE Companion'09: Proceedings of the International Conference on Software Engineering (Tool Demo), ACM, 2009
