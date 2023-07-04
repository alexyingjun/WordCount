# Word Counter Service

Word Counter Service provides APIs for 2 functions:
1. Add single or multiple words to the service and count the appearance of each valid word
2. Retrieve the count of given word appeared in the added words

## How to run the service

1. You need to have the scala tools installed and sbt available for the project.
2. The port 8080 has to be available for the service
3. Type run or go to the RestApiController and hit the run button to start the service

## How to use the library

Please refer to the DemoApplication in test folder com.synechron.wordcounter.demo

## How to make call to the API
1. Add words
```
POST http://localhost:8080/add
content-type: application/json
{"words":["abc", "BBC", "Ceebebe"]}
```

2, Get count for given word
```
GET http://localhost:8080/count/abc
```
