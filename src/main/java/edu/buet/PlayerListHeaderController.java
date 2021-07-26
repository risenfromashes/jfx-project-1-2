package edu.buet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class PlayerListHeaderController {
    enum SortField {
        NUMBER(0), NAME(1), COUNTRY(2), POSITION(3), AGE(4), HEIGHT(5), SALARY(6);
        private final int value;
        private SortField(int val) {
            this.value = val;
        }
        public int value() {
            return value;
        }
    }
    class State {
        int sortOrder; //0 none, 1 ascending, 2 descending
        SortField sortField;
        public State() {
            sortOrder = 0;
            sortField = SortField.SALARY;
        }
        public State(SortField sortField, int order) {
            this.sortField = sortField;
            this.sortOrder = order;
        }
    }
    @FXML
    private Label noCaret;
    @FXML
    private HBox noBox;
    @FXML
    private Label nameCaret;
    @FXML
    private HBox nameBox;
    @FXML
    private Label countryCaret;
    @FXML
    private HBox countryBox;
    @FXML
    private Label positionCaret;
    @FXML
    private HBox positionBox;
    @FXML
    private Label ageCaret;
    @FXML
    private HBox ageBox;
    @FXML
    private Label heightCaret;
    @FXML
    private HBox heightBox;
    @FXML
    private Label salaryCaret;
    @FXML
    private HBox salaryBox;
    @FXML
    private HBox searchBox;

    Label[]  carets = new Label[SortField.values().length];
    HBox[]  boxes = new HBox[SortField.values().length];
    ObjectProperty<State> stateProperty;

    public PlayerListHeaderController() {
        stateProperty = new SimpleObjectProperty<>();
        stateProperty.set(new State());
    }
    ObjectProperty<State> stateProperty() {
        return stateProperty;
    }
    @FXML
    void initialize() {
        carets[SortField.NUMBER.value()] = noCaret;
        carets[SortField.NAME.value()] = nameCaret;
        carets[SortField.COUNTRY.value()] = countryCaret;
        carets[SortField.POSITION.value()] = positionCaret;
        carets[SortField.AGE.value()] = ageCaret;
        carets[SortField.HEIGHT.value()] = heightCaret;
        carets[SortField.SALARY.value()] = salaryCaret;
        boxes[SortField.NUMBER.value()] = noBox;
        boxes[SortField.NAME.value()] = nameBox;
        boxes[SortField.COUNTRY.value()] = countryBox;
        boxes[SortField.POSITION.value()] = positionBox;
        boxes[SortField.COUNTRY.value()] = countryBox;
        boxes[SortField.POSITION.value()] = positionBox;
        boxes[SortField.AGE.value()] = ageBox;
        boxes[SortField.HEIGHT.value()] = heightBox;
        boxes[SortField.SALARY.value()] = salaryBox;
        for (var sortField : SortField.values()) {
            carets[sortField.value()].setText("");
            boxes[sortField.value()].onMouseClickedProperty().set( ev -> {
                if (ev.getButton() == MouseButton.PRIMARY) {
                    var state = stateProperty.getValue();
                    int order = 1;
                    if (state.sortField == sortField) {
                        order = (state.sortOrder + 1) % 3;
                    }
                    for (var sf : SortField.values()) {
                        carets[sf.value()].setText("");
                    }
                    if (order == 1) {
                        carets[sortField.value()].setText("⯆");
                    } else if (order == 2) {
                        carets[sortField.value()].setText("⯅");
                    } else {
                        carets[sortField.value()].setText("");
                    }
                    stateProperty.setValue(new State(sortField, order));
                }
            });
        }
    }
    ObjectProperty<EventHandler<? super MouseEvent>> searchButtonClickProperty(){
        return searchBox.onMouseClickedProperty();
    }
    void resetCarets(){
        for (var sortField : SortField.values()) {
            carets[sortField.value()].setText("");
        }
    }
}
