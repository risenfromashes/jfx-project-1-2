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
    private static InetAddress address;
    private static int port;

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
        return client.connectAsync(address, port);
    }
    static Client getClient() {
        return client;
    }
    public static void main(String[] args) {
        try {
            if (args.length > 0) {
                var props = args[0].split(":");
                address = InetAddress.getByName(props[0].trim());
                port = Integer.parseInt(props[1]);
            } else {
                address = InetAddress.getLocalHost();
                port = 3001;
            }
        } catch (IOException e) {
            try {
                address = InetAddress.getLocalHost();
                port = 3001;
            } catch (IOException err) {
                throw new RuntimeException(e);
            }
        }
        client = new Client();
        launch();
        System.exit(0);
    }
}
