# Html-element-finder

Application reads predefined by id element data in original file, and then uses received data to find similar element in diff file

Build application

```
mvn clean install
```

Run application
```
java -cp ./target/finder.jar com.agileengine.makarov.ApplicationMain <path-to-original-file> <path-to-diff-file>
```
