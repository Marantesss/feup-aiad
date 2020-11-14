#!/bin/bash

# Build the project
javac -cp lib/gson-2.8.6.jar:lib/jade.jar  -d out  src/main/java/Main.java src/main/java/*/*.java src/main/java/*/*/*.java

# Change to the out directory
cd out || (echo "Out directory does not exist" && exit)

# Build the jar
jar cvfm scrum.jar ../manifest.txt .

# Copy the jar to the root directry
cp scrum.jar ../scrum.jar

