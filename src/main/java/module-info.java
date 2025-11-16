module org.example.cv_builder {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cv_builder to javafx.fxml;
    exports org.example.cv_builder;
}