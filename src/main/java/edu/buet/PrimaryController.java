package edu.buet;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class PrimaryController {

    @FXML
    private Label clubName;
    @FXML
    private VBox body;

    @FXML
    void initialize() throws IOException{
        body.getChildren().add(App.loadFXML("playerlistheader"));
    }

}

