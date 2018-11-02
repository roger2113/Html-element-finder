# Html-element-finder

Application reads predefined element data in original file by id, and then uses received data to find similar element in diff file

Build application

```
mvn clean install
```

Run application
```
java -cp ./target/parser-1.0-SNAPSHOT.jar com.agileengine.makarov.ApplicationMain <path-to-original-file> <path-to-diff-file>
```
