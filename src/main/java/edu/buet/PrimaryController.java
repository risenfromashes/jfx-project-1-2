package edu.buet;

import java.io.IOException;

import edu.buet.data.Club;
import edu.buet.data.PlayerDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PrimaryController {
    private Club club;
    private Image clubLogoImage;
    private PlayerDatabase db;

    @FXML
    private Label clubName;
    @FXML
    private Label clubValue;
    @FXML 
    private ImageView clubLogo;
    @FXML
    private Button playersButton;
    @FXML
    private Button transferListButton;
    @FXML
    private Button logoutButton;
    @FXML
    private VBox body;
    public PrimaryController() throws IOException{
        db = PlayerDatabase.parseFile("data/");
        club = db.getClub(4);
        clubLogoImage = new Image(club.getLogoUrl(), true);
    }
    @FXML
    void initialize() throws IOException{
        body.getChildren().add(App.loadFXML("playerlistheader"));
    }

}

