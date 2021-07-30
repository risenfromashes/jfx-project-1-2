 
# Javafx Client/Server Football Club Database Manager Project

The app is a simple client/server football club management system. A server hosts a database of about 17k player information (collected from [fifacm](https://www.fifacm.com/)). Clients login as clubs and can search and view the details of the players in the club. Additionally, players can be bought or sold across clients connected to the same server.

![screenshot](https://github.com/risenfromashes/jfx-project-1-2/blob/master/screenshots/s1.png?raw=true)

### Prebuilt

You can run the prebuilt cross-platform executable jars from the [release](https://github.com/risenfromashes/jfx-project-1-2/releases/download/0.0.2/jfx-project-1-2.zip).

### Running over Network

To run the server at a specified port, say 3001, run
     
     java -jar server.jar 3001
   
To make the client connect to a specified ip address and port, say 101.102.103.104 on port 99, run,

    java -jar client.jar 101.102.103.104::99


### Building

The project is built using Apache Maven.

Download [JDK 11 or later](http://jdk.java.net/) for your operating system.
Make sure `JAVA_HOME` is properly set to the JDK installation directory. 

Install [Maven](https://maven.apache.org/install.html) and add to path.


### Build

From project root run

    mvn compile package

### Run Server

    cp data build/; cd build/
    java -jar client.jar --server
    
### Run Client
    java -jar client.jar
    
Or you can just execute the jar by clicking (on windows).
