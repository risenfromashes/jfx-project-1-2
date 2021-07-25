module edu.buet {
    requires javafx.controls;
    requires javafx.fxml;
    requires fuzzywuzzy;

    opens edu.buet to javafx.fxml;
    exports edu.buet;
    exports edu.buet.data;
}
