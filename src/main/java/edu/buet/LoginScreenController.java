package edu.buet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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

    private List<String> entries;
    private ContextMenu entriesPopup;

    @FXML
    void initialize() {
        entries = new ArrayList<>();
        entries.add("hello1");
        entries.add("hello2");
        entries.add("hello3");
        entries.add("hello4");
        entriesPopup = new ContextMenu();
        clubNameField.textProperty().addListener( (observable, s1, s2) -> {
            //entriesPopup.setWidth(clubNameField.getLayoutBounds().getWidth());
            System.out.println(entriesPopup.getMinWidth());
            entriesPopup.getStyleClass().add("club-search-suggestion");
            if (clubNameField.getText().length() == 0) {
                entriesPopup.hide();
            } else {
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
            if (true)
                try {
                    App.setRoot("primary");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
        });
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            var result = searchResult.get(i);
            var entryLabel = new Label(result);
            entryLabel.setPrefWidth(clubNameField.getWidth() - 10);
            entryLabel.setMaxWidth(clubNameField.getWidth() - 10);
            entryLabel.setWrapText(true);
            var item = new CustomMenuItem(entryLabel, true);
            item.getStyleClass().clear();
            item.getStyleClass().add("club-search-suggestion-entry");
            item.setOnAction(e -> {
                clubNameField.setText(result);
                entriesPopup.hide();
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);
    }
}
