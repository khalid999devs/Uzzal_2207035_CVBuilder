module org.example.cv_builder {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens org.example.cv_builder to javafx.fxml;
    exports org.example.cv_builder;
    exports org.example.cv_builder.observer;
}