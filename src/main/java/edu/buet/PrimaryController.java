package edu.buet;

import java.io.IOException;

import edu.buet.data.Club;
import edu.buet.data.Player;
import edu.buet.data.PlayerDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PrimaryController {
    private Club club;
    private Image clubLogoImage;
    private PlayerDatabase db;

    @FXML
    private Label clubName;
    @FXML
    private Label clubBudget;
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

    private ListView<PlayerEntry> playerList;

    public PrimaryController() throws IOException {
        db = PlayerDatabase.parseFile("data/");
        club = db.getClub(4);
    }
    @FXML
    void initialize() throws IOException {
        clubLogo.setCache(true);
        clubLogo.setSmooth(false);
        clubLogoImage = new Image(club.getLogoUrl(), true);
        clubLogo.setImage(clubLogoImage);
        clubName.setText(club.getName());
        clubBudget.setText(club.getBudget().getString());
        populatePlayers();
    }
    private void populatePlayers() throws IOException {
        var fxmlLoader = new FXMLLoader(App.class.getResource("playerlistheader.fxml"));
        Parent parent = fxmlLoader.load();
        PlayerListHeaderController headerController = fxmlLoader.getController();
        body.getChildren().add(parent);

        playerList = new ListView<PlayerEntry>();
        VBox.setVgrow(playerList, Priority.ALWAYS);
        playerList.setMaxHeight(Double.MAX_VALUE);
        for (var player : club.getPlayers()) {
            playerList.getItems().add(new PlayerEntry(player));
        }
        body.getChildren().add(playerList);
        headerController.stateProperty().addListener( (ev, s0, state) -> {
            playerList.getItems().sort((p1_, p2_) -> {
                var p1 = p1_.getPlayer();
                var p2 = p2_.getPlayer();
                if(state.sortOrder == 0) return 0;
                int ret  = 0;
                switch (state.sortField) {
                case NUMBER:
                    ret = Integer.compare(p1.getJerseyNumber(), p2.getJerseyNumber());
                    break;
                case NAME:
                    ret = p1.getAltName().compareTo(p2.getAltName());
                    break;
                case COUNTRY:
                    ret = p1.getCountry().getAltName().compareTo(p2.getCountry().getAltName());
                    break;
                case  POSITION:
                    ret = p1.getPosition().getShortPosition().compareTo(p2.getPosition().getShortPosition());
                    break;
                case AGE:
                    ret =  Integer.compare(p1.getAge(), p2.getAge());
                    break;
                case HEIGHT:
                    ret =  Integer.compare(p1.getHeight(), p2.getHeight());
                    break;
                case SALARY:
                    ret =  Float.compare(p1.getWeeklySalary().getNumber(), p2.getWeeklySalary().getNumber());
                    break;
                }
                if(state.sortOrder == 1)
                    return ret;
                else 
                    return -ret;
            });
        });
    }

}
