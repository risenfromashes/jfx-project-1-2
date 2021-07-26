package edu.buet;

import java.io.IOException;

import edu.buet.data.Player;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PlayerInfo extends VBox {
    @FXML
    private Label name;
    @FXML
    private Label country;
    @FXML
    private Label age;
    @FXML
    private Label height;
    @FXML
    private Label weight;
    @FXML
    private Label jersyNumber;
    @FXML
    private Label preferredFoot;
    @FXML
    private Label positionLine0;
    @FXML
    private Label positionLine1;
    @FXML
    private Label positionLine2;
    @FXML
    private Label salary;
    @FXML
    private Label value;
    @FXML
    private ImageView imageView;
    @FXML
    private ImageView flagView;
    @FXML
    private Button sellOrConfirmButton;
    @FXML
    private Button backButton;
    @FXML
    private HBox transferBox;
    @FXML
    private Spinner<Double> feeSpinner;

    private Label[] positionLines = new Label[3];

    private Player player;
    private PlayerEntry entry;

    public PlayerInfo(PlayerEntry entry) {
        this.entry = entry;
        this.player = entry.getPlayer();
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("playerinfo.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        VBox.setVgrow(this, Priority.ALWAYS);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    ObjectProperty<EventHandler<? super MouseEvent>> backButtonClickedProperty() {
        return backButton.onMouseClickedProperty();
    }

    @FXML
    void initialize() {
        positionLines[0] = positionLine0;
        positionLines[1] = positionLine1;
        positionLines[2] = positionLine2;
        name.setText(player.getName());
        country.setText(player.getCountry().getName());
        age.setText(Integer.toString(player.getAge()));
        height.setText(player.getHeight() + " cm");
        weight.setText(player.getWeight() + " kg");
        jersyNumber.setText(Integer.toString(player.getJerseyNumber()));
        preferredFoot.setText(player.getPreferredFoot().toString());
        var positions = player.getPosition().getFullPosition();
        for (int i = 0; i < 3; i++) {
            if ( i < positions.size()) {
                positionLines[i].setText(positions.get(i));
            } else {
                positionLines[i].setText("");
            }
        }
        name.setText(player.getName());
        imageView.setSmooth(false);
        flagView.setSmooth(false);
        imageView.setCache(true);
        flagView.setCache(true);
        imageView.setImage(entry.getPlayerImage());
        flagView.setImage(entry.getFlagImage());
    }
}
