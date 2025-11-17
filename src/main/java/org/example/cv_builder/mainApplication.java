package org.example.cv_builder;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class mainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);

        URL cssUrl = Objects.requireNonNull(
                mainApplication.class.getResource("styles/style.css")
        );
        scene.getStylesheets().add(cssUrl.toExternalForm());

        stage.setTitle("CV Builder (2207035)");
        stage.setScene(scene);
        stage.show();
    }
}
