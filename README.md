#IPBroker 1.0

- lets player to find out ip addresses of other players. 

##Structure

- It is a RESTFul web service based on Spring Boot.
- Controller obtains the following channels:
```
 JOIN_PATH: /broker/1.0/join
 - method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE
 - expects a json object, that contains player nickname and uri to his server/client. The response is a map with all active players and theirs addresses.
    Request:        {
                        "nickname":"client1",
                        "address":"https://192.0.0.1:1414"
                    }
                    
    Response:       {
                        "Players":{"client2":"uri2","client3","uri3"}
                    }   
                    
 PLAYERS_PATH: /broker/1.0/players
 - method = GET, produces = APPLICATION_JSON_VALUE
 - returns all active players
    Request:        An empty GET-Request.
                    
    Response:       {
                        "Players":{"client2":"uri2","client3","uri3"}
                    } 

 GAME_PATH: /broker/1.0/messages
 - method = POST, consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE
 - expects a json object, that contains player nickname and a message, that will be delivered to him. The response obtains the response of the destination client.
    Request:        {
                        "nickname":"client2",
                        "message":"abc"
                    }
                    
    Response:       {
                        "nickname":"client1",
                        "message":"cba"
                    }  
```                    
- Player data is persistent to guava cache with strategy "expire after access". 
- Communication is based on SSL protocol.

##Configuration

- The most values are configurable in application.properties:
``` 
player.cache.expiration.time.in.seconds=3
player.cache.cleanup.period.in.millis=2000

server.port=8443
server.ssl.key-store=classpath:server_keystore.jks
server.ssl.key-store-password=secret
server.ssl.keyStoreType=jks
server.ssl.key-password=uY9DbMejmmRRusNxPyhQjTvvmsMdjTYqoNY8
server.ssl.keyAlias=localhost
``` 

##Start

- Choose a server port [default=8443]
- Configure ssl settings
- If it is a dedicated server, set in parameter 'server.ssl.keyAlias' a correct ip address
- Use maven to build a jar or just run Application.class from IDE
- To start the service from jar, execute the following command:
    "java -cp ipbroker.jar gr.planetz.Application"

[Release build boots up service on localhost]

##Test

- tests and integration tests are executed during maven building.
