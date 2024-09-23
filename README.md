# Netty Game Server

## Introduction

Use netty with spring-boot to program game server.
The project structure like as spring-mvc. has a Dispatch class to distribute request to target class method

At present. The framework integrate redis. mongodb. protocolBuffer. eventBus. and implement some useful tools.

## Getting Started

```
mvn clean package
java -jar game-start/target/*.jar
```

### Prerequisites

1. Installing redis 
2. Installing mongodb
3. Using the python execute the script `static-resource/run.py`. Add some data to mongodb
4. Executing game-start module App

```
python static-resource/run.py -h mongodb_host -p mongodb_port
```

## Usage

```
@Action
public class HelloAction {

    @RequestMapping
    public ResponseHello hello(Session session, RequestHello hello) {
        System.out.println("Get session: " + session.getId() + " info:" + hello);      
        return new ResponseHello("I am server.", 10001);
    }
    
}

@Protcol(10086)
public class RequestHello {

    private String content;
}

@Protcol(10087)
public class ResponseHello {

    private String content;

    private int version;
}
```
