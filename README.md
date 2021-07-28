 
# Javafx Client/Server Football Club Database Manager Project

The app is a simple client/server football club management system. A server hosts a database of about 20k player information (collected from [fifacm](https://www.fifacm.com/)). Clients login as clubs and can search and view the details and information about the players in the club. Additionally, players can be bought or sold across clients connected to the same server.

![screenshot](https://github.com/risenfromashes/jfx-project-1-2/blob/master/screenshots/s1.png?raw=true)

### Prebuilt

You can run the prebuilt cross-platform executable jars from the release.

### Building

The project is built using Apache Maven.

Download [JDK 11 or later](http://jdk.java.net/) for your operating system.
Make sure `JAVA_HOME` is properly set to the JDK installation directory. 

Install Maven and add to path.


### Build

    mvn compile package

### Run Server

    cd build/
    java -jar jfx-project-1-2.jar --server
    
### Run Client
    cd build/
    java -jar jfx-project-1-2.jar
    
Or you can just execute the jar by clicking (on windows).
