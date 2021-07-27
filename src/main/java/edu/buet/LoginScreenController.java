package edu.buet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.buet.messages.ClubEntry;
import edu.buet.messages.ClubListRequest;
import edu.buet.messages.ClubListResponse;
import edu.buet.messages.LoginRequest;
import edu.buet.messages.LoginResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import me.xdrop.fuzzywuzzy.FuzzySearch;

/**
 * The Autocomplete TextField was based on the following gist:
 * Original: floralvikings@github
 * Source: https://gist.github.com/floralvikings/10290131
 *
 */
public class LoginScreenController {
    @FXML
    private TextField clubNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private VBox body;
    @FXML
    private VBox loginBox;

    private BottomBar bottomBar;

    private ContextMenu entriesPopup;
    private List<String> altNames;
    private Map<String, ClubEntry> entryMap;

    private int currentClubId;

    void showLogin(List<ClubEntry> clubEntries) {
        entriesPopup = new ContextMenu();
        entriesPopup.getStyleClass().add("club-search-suggestion");
        altNames = new ArrayList<String>(clubEntries.size());
        entryMap = new HashMap<String, ClubEntry>(clubEntries.size());
        for (var entry : clubEntries) {
            entryMap.put(entry.altName, entry);
            altNames.add(entry.altName);
        }
        clubNameField.textProperty().addListener( (observable, s1, s2) -> {
            if (clubNameField.getText().length() == 0) {
                entriesPopup.hide();
            } else {
                var entries = FuzzySearch.extractTop(clubNameField.getText(), altNames, 10, 70).stream().map(e -> entryMap.get(e.getString())).collect(Collectors.toList());
                if (entries.size() > 0) {
                    populatePopup(entries);
                    if (!entriesPopup.isShowing()) {
                        entriesPopup.show(clubNameField, Side.BOTTOM, 0, 0);
                    }
                } else {
                    entriesPopup.hide();
                }
            }

        });
        clubNameField.focusedProperty().addListener( c -> {
            entriesPopup.hide();
        });
        loginButton.setOnAction( e -> {
            App.connect().thenAccept( s -> {
                System.out.println("Login button pressed");
                s.send(new LoginRequest(currentClubId, passwordField.getText().trim()), LoginResponse.class)
                .thenAccept( res -> {
                    Platform.runLater( () -> {
                        if (res.success()) {
                            App.setRoot(new Primary(res.get()));
                        } else {
                            bottomBar.setMessage(BottomBar.Type.ERROR, res.getMessage());
                            bottomBar.show(body);
                        }
                    });
                }).exceptionally( err -> {
                    Platform.runLater( () -> {
                        bottomBar.setMessage(BottomBar.Type.ERROR, err.getMessage());
                        bottomBar.show(body);
                    });
                    return null;
                });
            });
        });
        loginBox.setVisible(true);
    }

    @FXML
    void initialize() {
        loginBox.setVisible(false);
        connect();
    }
    void connect() {
        bottomBar = new BottomBar();
        bottomBar.setMessage(BottomBar.Type.INFO, "Fetching club data...");
        bottomBar.show(body);
        App.connect().thenAccept(s -> {
            Platform.runLater(()->{
                bottomBar.setMessage(BottomBar.Type.INFO, "Connected to server");
            });
            s.send(new ClubListRequest(), ClubListResponse.class)
            .thenAccept( res -> {
                Platform.runLater(() -> {
                    bottomBar.hide(body);
                    showLogin(res.get());
                });
            }).exceptionally( e -> {
                connect();
                return null;
            });
        });
    }

    private void populatePopup(List<ClubEntry> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            var result = searchResult.get(i);
            var entryLabel = new Label(result.name);
            entryLabel.setPrefWidth(clubNameField.getWidth() - 10);
            entryLabel.setMaxWidth(clubNameField.getWidth() - 10);
            entryLabel.setWrapText(true);
            var item = new CustomMenuItem(entryLabel, true);
            item.getStyleClass().clear();
            item.getStyleClass().add("club-search-suggestion-entry");
            item.setOnAction(e -> {
                clubNameField.setText(result.name);
                currentClubId = result.id;
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}
