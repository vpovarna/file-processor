####
Docker run
```
$ docker run -d -p 6379:6379 --name redis redis
```

####
Run the Spring boot app:
```
$ mvn package spring-boot:run
```

Build Spring Boot app:
```
$ mvn clean install
```

####
API Commands example:

Get by taskId
```
$ curl -X GET localhost:18081/files/1
```

Add a new file:
```
 curl --header "Content-Type: application/json" --request POST --data '{"fileId": "FirstFile"}' http://localhost:18081/files
```

Get Text Files from IP
```
curl -X GET localhost:18081/files/ip/192.168.1.1
```
