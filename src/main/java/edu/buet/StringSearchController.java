package edu.buet;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class StringSearchController {
   @FXML
   private TextField searchField;
    
   StringProperty searchTextProperty(){
       return searchField.textProperty();
   }
}
