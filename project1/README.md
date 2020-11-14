# SCRUM: Simulating Agile Development using Jade 

> Very nice description

## Build and Execution

### Command Line

In order to compile the project to an `out/` folder run the following command:

```shell script
javac -cp lib/gson-2.8.6.jar:lib/jade.jar  -d out  src/main/java/Main.java src/main/java/*/*.java src/main/java/*/*/*.java
```

This will create the `out/` folder and place all the `.class` files in it, organized by package. Then we can run the main
class using:

```shell script
java -cp lib/jade.jar:lib/gson-2.8.6.jar:out Main src/main/resources/config.test.json
```

These commands have been written in the `run.sh` file, so you only need to run it.

### .jar file

In order to build the jar we have to build the project. To do that we can use the command above.

Then we have to change to the build directory (`cd out`) and build the jar using the provided `manifest.txt`:

```shell script
jar cvfm scrum.jar ../manifest.txt .
```

The `scrum.jar` file will be inside that directory.

As an alternative, the `build_jar.sh` script can be used to build the jar file.

The jar file can be executed using:

```shell script
java -jar scrum.jar <arguments>
```


