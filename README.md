# Project Title

Netty Game Server

## Introduction

Use netty with spring-boot to program game server.
The project structure like as spring-mvc. has a Dispatch class to distribute request to target class method

At present. The framework integrate redis. mongodb. protocolBuffer. eventBus. and implement some useful tools.

## Getting Started

```
mvn install pack
java -jar game-start/target/*.jar
```

### Prerequisites

1. Install redis 
2. Install mongodb
3. Using the python execute the script `static-resource/run.py` to add some data
4. Executing game-start module App

```
python static-resource/run.py
```

## Feature 

* Used redis with redisson to cache player data.
* Save data asynchronously to mongodb.
* Integrating google proto buffer to speed ​​up serialization.

## Usage

```
@Action
public class HelloAction {

    @RequestMapping(-10086)
    @ResponseMapping(-10087)
    public ResponseHello hello(Session session, RequestHello hello) {
        System.out.println("Get session: " + session.getId() + " info:" + hello);      
        return new ResponseHello("I am server.", 10001);
    }
}

public class RequestHello {

    private String content;
}

public class ResponseHello {

    private String content;

    private int version;
}
```