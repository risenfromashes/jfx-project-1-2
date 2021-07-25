package edu.buet;

import java.io.IOException;

import edu.buet.data.Club;
import edu.buet.data.Player;
import edu.buet.data.PlayerDatabase;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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
        var headerLoader = new FXMLLoader(App.class.getResource("playerlistheader.fxml"));
        Parent header = headerLoader.load();
        PlayerListHeaderController headerController = headerLoader.getController();
        body.getChildren().add(header);
        var searchLoader = new FXMLLoader(App.class.getResource("playersearch.fxml"));
        Parent search = searchLoader.load();
        PlayerSearchController searchController = searchLoader.getController();

        var playerList = FXCollections.<PlayerEntry>observableArrayList();
        var playerListView = new ListView<>(playerList);
        VBox.setVgrow(playerListView, Priority.ALWAYS);
        playerListView.setMaxHeight(Double.MAX_VALUE);
        for (var player : club.getPlayers()) {
            playerList.add(new PlayerEntry(player));
        }
        body.getChildren().add(playerListView);
        headerController.stateProperty().addListener( (ev, s0, state) -> {
            playerList.sort((p1_, p2_) -> {
                var p1 = p1_.getPlayer();
                var p2 = p2_.getPlayer();
                if (state.sortOrder == 0) return 0;
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
                if (state.sortOrder == 1)
                    return ret;
                else
                    return -ret;
            });
        });
        headerController.searchButtonClickProperty().set( ev -> {
            if (ev.getButton() == MouseButton.PRIMARY) {
                body.getChildren().remove(0);
                body.getChildren().add(0, search);
            }
        });
        searchController.queryProperty().addListener( (ev, o1, o2) -> {
            playerListView.setItems(playerList.filtered( p -> o2.match(p.getPlayer())));
        });
    }
}
