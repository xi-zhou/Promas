# Multi-agent epidemiological simulation powered by Problog

## Installation

- Download the source as zip file or fork this repository.

- Download [Repast Simphony](https://repast.github.io/download.html) version 2.8 and follow the [Quick Start Guide](https://repast.github.io/quick_start.html) import this project.

- Install [JEP](https://github.com/ninia/jep) version 3.9.1 or run ```pip install jep==3.9.1```

## Path reconfiguration

We use JEP, which implements the [JNI](https://docs.oracle.com/javase/8/docs/technotes/guides/jni/), to connect java and python. Therefore this project is not platform portability so you need to reconfigure the following paths:

1. Find where jep is installed, ```pip show jep``` and copy the path ```/Users/.../lib/python3.7/site-packages/jep``` to 
Eclipse ``` Referenced Libraries -> jep-3.7.0.jar-> Properties -> Native librairy location ```
In case of e.g. [path error](https://github.com/polynote/polynote/issues/521) or [UnsatisfiedLink error](https://github.com/ninia/jep/issues/141) you can find help from JEP.

2. Change the absolute paths in ```TransmissionModel.java``` ```ProgerssionModel.java``` to where you have Eclipse installed on your computer. Note: in total 4 paths need to be modified.

## Remarks

- Before starting a new simulation make sure you've deleted the old database(test.db) under ```./misc```
