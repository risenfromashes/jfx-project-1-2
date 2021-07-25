package edu.buet;


import edu.buet.data.Position;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class CheckboxSearchController {
    @FXML
    private CheckBox forward;
    @FXML
    private CheckBox midfielder;
    @FXML
    private CheckBox defender;
    @FXML
    private CheckBox goalkeeper;

    private IntegerProperty selectedPositions;

    public CheckboxSearchController() {
        selectedPositions = new SimpleIntegerProperty(Position.FORWARD | Position.MIDFIELDER | Position.DEFENDER | Position.GOALKEEPER);
    }

    @FXML
    void initialize(){
        forward.setSelected(true);
        midfielder.setSelected(true);
        defender.setSelected(true);
        goalkeeper.setSelected(true);
        forward.onActionProperty().set( c -> {
            selectedPositions.set(selectedPositions.get() ^ Position.FORWARD);
        });
        midfielder.onActionProperty().set( c -> {
            selectedPositions.set(selectedPositions.get() ^ Position.MIDFIELDER);
        });
        defender.onActionProperty().set( c -> {
            selectedPositions.set(selectedPositions.get() ^ Position.DEFENDER);
        });
        goalkeeper.onActionProperty().set( c -> {
            selectedPositions.set(selectedPositions.get() ^ Position.GOALKEEPER);
        });
    }
    IntegerProperty selectedPositionsProperty() {
        return selectedPositions;
    }
}
