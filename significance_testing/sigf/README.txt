SIGF - Significance testing for evaluation statistics
=====================================================
(c) Sebastian Pado, June 2006
    pado@coli.uni-saarland.de
    pado@stanford.edu

Version history
---------------

V.2 - June 2008. Refactorization to make extensions easier.
      	   	 Java 1.5 generics for proper type safety. 
      	   	 ARTs now subclass ApproximateRandomizationTest<E>. 

Requirements
-------------

V.2 needs Java 1.5 or higher. If you don't have 1.5, use V.1.

Files
-----

- Some java files in the src/ directory
- A simplistic Makefile (run with "make all" or just "make")
- Two directories with example data files (exampleFScore and exampleAverage)

Introduction 
------------ 

An important issue in empirical computational linguistics is to assess
the differences between two performance figures for two models m and n
on the same dataset. For many popular evaluation metrics (such as
Precision, Recall, or F-Score), asssessing the significance of these
differences is non-trivial, since the assumptions that standard tests
make are not met. (For example, the chi square test makes an
independence assumption between the categories).

According to the statistical literature, the way to go about this is
to generate a population of new models from the predictions of the
existing models. There are two ways for doing so: by doing the
bootstrap (i.e. sampling with replacement), or (approximate)
randomization. Since the bootstap assumes that the sample is
representative, we have implemented a randomization framework (Yeh
COLING 2000, Noreen 1989). The (rough) idea is that if that the
significance between two sets of predictions (m and n) is significant,
random shuffling of the predicions will only very infrequently result
a larger difference between the shuffled sets. The relative frequency
of this actually happening can be interpreted as significance of the
difference.


Input
-----

This program reads in two files containing frequencies of stratified
predictions/observations (e.g. predictions per sentence, per item, etc). 

Each prediction/observation is stored in an object that subclasses
the Observation interface.

- In the case of a simple average, each observation is a Singleton
  object that encodes the value of the one variable to be averaged. 

- In the case of F-scores, each observation is a Triple object that
  holds
   - # of successful predictions (model prediction == gold) 
   - # of all predictions of the model   (true+false positives),
   - # of all predictions of the gold standard 
       (true positives + false negatives)>


Usage example
-------------

cd class
java de/pado/sigf/FScoreART ../exampleFScore/model1 ../exampleFScore/model2 10000

Output (to STDOUT, if not redirected):

Number of observations: 3
Number of different observations: 2
Statistic of file1: 0.7272727272727272
Statistic of file2: 0.1818181818181818
Difference: 0.5454545454545454
Number of iterations: 10000
p-value (2-tailed): 0.48605139486051396


Structure of the program
------------------------

- Statistic: Parametrised Interface, defines what a Statistic must be 
             able to do. The parameter of the Statistic is the
	     applicable Observation class. 
             A Statistic can thus be seen as an object encapsulating 
	     a set of sufficient statistics of a dataset.

	     Examples: FScore implements Statistic<Triple<Long>>
	     	       Average implements Statistic<Singleton<Long>>

- ApproximateRandomizationTest: 
             Abstract class that performs randomisation of two sets of
             observations, computes the difference,
	     and calculates the relative frequency of
             the event "randomised difference > original difference".
	     
	     A non-abstract subclass of ART must be parametrised by the 
             observations it uses, since it uses a Statistic object for 
	     bookkeeping.
	     
	     Examples: FScoreART extends ART<Triple<Long>>
	     	       AverageART extends ART<Singleton<Long>>

- FScoreART: An implemented example of the framework for F-Scores.
	     It contains the classes "FScore" and FScoreART.

- AverageART: An implemented example of the framework for F-Scores.
	      It contains the classes "Average" and AverageART.


Implementing a new statistic in sigf
------------------------------------

1. Figure out what type your observations are. If you can't reuse
   Singleton or Triple, write a new class that implements Observation.

2. Write a class that implements Statistic that computes what you want
   and that fits your Observation type.

3. write a class that extends ApproximateRandomizationTest
   - implement the method readInput(). Its return type must be
     a Vector of your observation objects
   - implement the static main() method which must basically
     construct an ART object and call its run() method with the 
     command line arguments.
