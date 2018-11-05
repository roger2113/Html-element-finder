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
Time was off, so next weak points were rewritten in "iteration" branch
1) posibility to pass 3-rd argument as target element id;
2) searching by all data of target element, no attributes hard coded
3) result is the element with max score, and now score increments with every matching word in target and compared attribute values 
