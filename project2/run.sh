#!/bin/bash

# Build the project
javac -cp lib/gson-2.8.6.jar:lib/jade.jar  -d out  src/main/java/Launcher.java src/main/java/*/*.java src/main/java/*/*/*.java

#Run main method
java -cp lib/jade.jar:lib/gson-2.8.6.jar:out Launcher json/config.test.json