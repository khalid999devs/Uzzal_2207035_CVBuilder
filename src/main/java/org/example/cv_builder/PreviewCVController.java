package org.example.cv_builder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

public class PreviewCVController {

    @FXML
    private ImageView photoImageView;

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label educationLabel;

    @FXML
    private Label skillsLabel;

    @FXML
    private Label workExperienceLabel;

    @FXML
    private Label projectsLabel;

    public void setData(CVData data) {
        nameLabel.setText(data.getFullName());
        emailLabel.setText(data.getEmail());
        phoneLabel.setText(data.getPhone());
        addressLabel.setText(data.getAddress());
        educationLabel.setText(data.getEducation());
        skillsLabel.setText(data.getSkills());
        workExperienceLabel.setText(data.getWorkExperience());
        projectsLabel.setText(data.getProjects());

        if (data.getPhotoPath() != null && !data.getPhotoPath().isBlank()) {
            Image image = new Image(data.getPhotoPath(), true);
            photoImageView.setImage(image);
        }
    }

    @FXML
    private void handleSidebarDashboard(ActionEvent event) throws IOException {
        loadScene(event, "main-view.fxml");
    }

    @FXML
    private void handleSidebarCreate(ActionEvent event) throws IOException {
        loadScene(event, "create-cv-view.fxml");
    }

    @FXML
    private void handleBackToEdit(ActionEvent event) throws IOException {
        loadScene(event, "create-cv-view.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(mainApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), 1000, 650);
        scene.getStylesheets().add(
            mainApplication.class.getResource("styles/style.css").toExternalForm()
        );
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }
}
