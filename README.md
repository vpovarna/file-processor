API Commands example:

Get by taskId
```
$ curl -X GET localhost:18081/files/1
```

Add a new file:
```
 curl --header "Content-Type: application/json" --request POST --data '{"fileId": "FirstFile"}' http://localhost:18081/files
```