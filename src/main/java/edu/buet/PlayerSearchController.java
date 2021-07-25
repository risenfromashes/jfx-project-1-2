package edu.buet;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import edu.buet.data.Player;
import edu.buet.data.Position;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import me.xdrop.fuzzywuzzy.FuzzySearch;

public class PlayerSearchController {
    enum SearchField {
        NAME(0), COUNTRY(1), POSITION(2), SALARY(3);
        private final int value;
        private SearchField(int value) {
            this.value = value;
        }
        public int value() {
            return value;
        }
        public String toString() {
            switch (this) {
            case NAME:
                return "By Name";
            case COUNTRY:
                return "By Country";
            case POSITION:
                return "By Position";
            case SALARY:
                return "By Salary";
            }
            throw new RuntimeException();
        }
        public static SearchField fromValue(int value) {
            switch (value) {
            case 0:
                return NAME;
            case 1:
                return COUNTRY;
            case 2:
                return POSITION;
            case 3:
                return SALARY;
            }
            throw new RuntimeException();
        }
    }
    class Query {
        SearchField currentField;
        int positionFlags;
        String nameQuery;
        String countryQuery;
        float salaryMin;
        float salaryMax;
        public Query() {
            currentField = SearchField.NAME;
            positionFlags = Position.FORWARD | Position.MIDFIELDER | Position.DEFENDER | Position.GOALKEEPER;
            nameQuery = "";
            countryQuery = "";
            salaryMin = Float.MIN_VALUE;
            salaryMax = Float.MAX_VALUE;
        }
        public Query(SearchField field, int position, String nameQuery, String countryQuery, float min, float max) {
            this.currentField = field;
            this.positionFlags = position;
            this.nameQuery = nameQuery;
            this.countryQuery = countryQuery;
            this.salaryMin = min;
            this.salaryMax = max;
        }
        public boolean match(Player player) {
            var m0 = player.getPosition().checkPosition(positionFlags);
            var m1 = nameQuery.strip().isEmpty() || FuzzySearch.tokenSortPartialRatio(nameQuery, player.getAltName()) > 80;
            var m2 = countryQuery.strip().isEmpty() || FuzzySearch.tokenSortPartialRatio(countryQuery, player.getCountry().getAltName()) > 80;
            var m3 = player.getWeeklySalary().getNumber() >= salaryMin && player.getWeeklySalary().getNumber() <= salaryMax;
            return m0 && m1 && m2 && m3;
        }
        public Query clone() {
            return new Query(currentField, positionFlags, nameQuery, countryQuery, salaryMin, salaryMax);
        }
    }
    @FXML
    private ComboBox<String> searchCombo;
    @FXML
    private Button backButton;
    @FXML
    private HBox searchBox;

    private ObjectProperty<Query> queryProperty;

    public PlayerSearchController() {
        queryProperty = new SimpleObjectProperty<>();
        queryProperty.set(new Query());
    }
    ObjectProperty<Query> queryProperty(){
        return queryProperty;
    }
    @FXML
    void initialize() throws IOException {
        FXMLLoader stringSearchLoader = new FXMLLoader(App.class.getResource("stringsearch.fxml"));
        FXMLLoader checkboxSearchLoader = new FXMLLoader(App.class.getResource("checkboxsearch.fxml"));
        FXMLLoader rangeSearchLoader = new FXMLLoader(App.class.getResource("rangesearch.fxml"));
        Parent stringSearch = stringSearchLoader.load();
        Parent checkboxSearch = checkboxSearchLoader.load();
        Parent rangeSearch = rangeSearchLoader.load();
        StringSearchController stringSearchController = stringSearchLoader.getController();
        CheckboxSearchController checkboxSearchController = checkboxSearchLoader.getController();
        RangeSearchController rangeSearchController = rangeSearchLoader.getController();
        searchCombo.getItems().addAll(Arrays.stream(SearchField.values()).map(e -> e.toString()).collect(Collectors.toList()));
        searchCombo.getSelectionModel().select(queryProperty.get().currentField.value());
        searchBox.getChildren().add(stringSearch);
        searchCombo.getSelectionModel().selectedIndexProperty().addListener( (ev, i1, i2) -> {
            var field = SearchField.fromValue((Integer)i2);
            var nQ = new Query();
            switch (field) {
            case NAME:
                searchBox.getChildren().clear();
                searchBox.getChildren().add(stringSearch);
                nQ.currentField = SearchField.NAME;
                stringSearchController.searchTextProperty().set(nQ.nameQuery);
                break;
            case COUNTRY:
                searchBox.getChildren().clear();
                searchBox.getChildren().add(stringSearch);
                nQ.currentField = SearchField.COUNTRY;
                stringSearchController.searchTextProperty().set(nQ.countryQuery);
                break;
            case SALARY:
                searchBox.getChildren().clear();
                searchBox.getChildren().add(rangeSearch);
                nQ.currentField = SearchField.SALARY;
                rangeSearchController.rangeLowProperty().set((double)nQ.salaryMin);
                rangeSearchController.rangeHighProperty().set((double)nQ.salaryMax);
                break;
            case POSITION:
                searchBox.getChildren().clear();
                searchBox.getChildren().add(checkboxSearch);
                nQ.currentField = SearchField.POSITION;
                checkboxSearchController.selectedPositionsProperty().set(nQ.positionFlags);
                break;
            }
            queryProperty.setValue(nQ);
        });
        stringSearchController.searchTextProperty().addListener( (c, o1, o2) -> {
            var nQ = queryProperty.getValue().clone();
            switch (nQ.currentField) {
            case NAME:
                nQ.nameQuery = o2;
                break;
            case COUNTRY:
                nQ.countryQuery = o2;
                break;
            default:
                break;
            }
            queryProperty.setValue(nQ);
        });
        checkboxSearchController.selectedPositionsProperty().addListener( (ev, o1, o2) -> {
            var nQ = queryProperty.getValue().clone();
            nQ.positionFlags = checkboxSearchController.selectedPositionsProperty().get(); 
            queryProperty.setValue(nQ);
        });
        rangeSearchController.rangeLowProperty().addListener( (ev, o1, o2) -> {
            var nQ = queryProperty.getValue().clone();
            nQ.salaryMin = (float)(double)o2;
            queryProperty.setValue(nQ);
        });
        rangeSearchController.rangeHighProperty().addListener( (ev, o1, o2) -> {
            var nQ = queryProperty.getValue().clone();
            nQ.salaryMax = (float)(double)o2;
            queryProperty.setValue(nQ);
        });
    }
}
