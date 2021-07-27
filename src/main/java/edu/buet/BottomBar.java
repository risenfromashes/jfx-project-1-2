package edu.buet;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BottomBar extends HBox {
    public enum Type { INFO, WARNING, ERROR }
    @FXML
    private Label message;
    private boolean showed;
    public BottomBar () {
        this.showed = false;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("bottombar.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        HBox.setHgrow(this, Priority.ALWAYS);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    public void setMessage(Type type, String message) {
        getStyleClass().clear();
        switch (type) {
        case INFO:
            getStyleClass().add("botton-bar-info");
            break;
        case WARNING:
            getStyleClass().add("botton-bar-warning");
            break;
        case ERROR:
            getStyleClass().add("botton-bar-error");
            break;
        }
        this.message.textProperty().set(message);
    }
    void show(VBox box) {
        showed = true;
        if (!box.getChildren().contains(this))
            box.getChildren().add(this);
    }
    void hide(VBox box) {
        showed = false;
        if (box.getChildren().contains(this))
            box.getChildren().remove(this);
    }
    boolean isShowed() {
        return showed;
    }
}
