package edu.buet;

import java.io.IOException;

import edu.buet.data.Country;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class CountryEntry extends HBox {
    
    @FXML
    private Label countryLabel;
    @FXML
    private Label countLabel;
    @FXML
    private ImageView flag;

    private Country country;
    private int count;

    public CountryEntry(Country country, int count) {
        this.country = country;
        this.count = count;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("countryentry.fxml"));
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
        countryLabel.setText(country.getName());
        countLabel.setText(count + "");
        flag.setCache(true);
        flag.setSmooth(false);
        flag.setImage(new Image(country.getFlagUrl(), true));
    }

}
