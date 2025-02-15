#!/bin/bash

# Build the project
javac -cp lib/asm.jar:lib/beanbowl.jar:lib/colt.jar:lib/commons-collection.jar:lib/commons-logging.jar:lib/geotools_repast.jar:lib/gson-2.8.6.jar:lib/ibis.jar:lib/jade.jar:lib/jakarta-poi.jar:lib/jep-2.24.jar:lib/jgap.jar:lib/jh.jar:lib/jmf.jar:lib/jode-1.1.2-pre1.jar:lib/joone.jar:lib/JTS.jar:lib/junit.jar:lib/log4j-1.2.8.jar:lib/OpenForecast-0.4.0.jar:lib/openmap.jar:lib/plot.jar:lib/ProActive.jar:lib/repast.jar:lib/SAJaS.jar:lib/trove.jar:lib/violinstrings-1.0.2.jar  -d out  src/main/java/Launcher/Launcher.java src/main/java/*/*.java src/main/java/*/*/*.java

# Change to the out directory
cd out || (echo "Out directory does not exist" && exit)

# Build the jar
jar cvfm scrum.jar ../manifest.txt .

# Copy the jar to the root directry
cp scrum.jar ../scrum.jar

