package edu.buet;

import java.io.IOException;

import edu.buet.data.Player;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class PlayerEntry extends HBox{
    @FXML
    private Label jerseyNumber;
    @FXML
    private Label name;
    @FXML
    private Label country;
    @FXML
    private Label position;
    @FXML
    private Label age;
    @FXML
    private Label height;
    @FXML
    private Label salaryOrFee;
    @FXML
    private ImageView playerImageView;
    @FXML
    private ImageView flagImageView;
    @FXML
    private Button sellOrBuyButton;
    @FXML
    private Button infoButton;

    private Image playerImage, flagImage;
    private Player player;

    public PlayerEntry(Player player) {
        this.player = player;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("playerentry.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    @FXML
    void initialize(){
        jerseyNumber.setText(player.getJerseyNumber() + "");
        name.setText(player.getName());
        country.setText(player.getCountry().getName());
        position.setText(player.getPosition().getShortPosition());
        age.setText(player.getAge() + "");
        height.setText(player.getHeight() + " cm");
        salaryOrFee.setText(player.getWeeklySalary().getString());
        playerImageView.setCache(true);
        playerImageView.setSmooth(false);
        flagImageView.setCache(true);
        flagImageView.setSmooth(false);
        playerImage = new Image(player.getImageUrl(), true);
        flagImage = new Image(player.getCountry().getFlagUrl(), true);
        playerImageView.setImage(playerImage);
        flagImageView.setImage(flagImage);
    }
    ObjectProperty<EventHandler<? super MouseEvent>> sellOrBuyButtonClickedProperty(){
        return sellOrBuyButton.onMouseClickedProperty();
    }
    ObjectProperty<EventHandler<? super MouseEvent>> infoButtonClickedProperty(){
        return infoButton.onMouseClickedProperty();
    }
    public Player getPlayer(){
        return player;
    }
    Image getPlayerImage(){
        return playerImage; 
    }
    Image getFlagImage(){
        return flagImage; 
    }
}
