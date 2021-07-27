package edu.buet;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

import edu.buet.net.Client;
import edu.buet.net.SocketHandle;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static Client client;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("loginscreen.fxml"));
        Parent parent = fxmlLoader.load();
        scene = new Scene(parent, 1280, 720);
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }
    static void setRoot(Parent root) {
        scene.setRoot(root);
    }

    static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    static CompletableFuture<SocketHandle> connect() {
        try {
            return client.connectAsync(InetAddress.getLocalHost(), 3001);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    static Client getClient() {
        return client;
    }
    public static void main(String[] args) {
        client = new Client();
        launch();
        System.exit(0);
    }
}
