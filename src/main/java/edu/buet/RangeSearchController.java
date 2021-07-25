package edu.buet;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Spinner;

public class RangeSearchController {
    @FXML
    private Spinner<Double> low;
    @FXML
    private Spinner<Double> high;

    public RangeSearchController() {
    }
    public void setRangeLow(float v) {
        low.valueFactoryProperty().get().valueProperty().set((double)v);
    }
    public void setRangeHigh(float v) {
        high.valueFactoryProperty().get().valueProperty().set((double)v);
    }
    ObjectProperty<Double> rangeLowProperty() {
        return low.valueFactoryProperty().get().valueProperty();
    }
    ObjectProperty<Double> rangeHighProperty() {
        return high.valueFactoryProperty().get().valueProperty();
    }
}
