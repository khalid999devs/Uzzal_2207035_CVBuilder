package org.example.cv_builder;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class mainController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Hello JavaFX World!");
    }
}
