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





