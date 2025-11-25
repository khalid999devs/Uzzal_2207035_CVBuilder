package org.example.cv_builder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.cv_builder.database.CVRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
            try {
                Path photoPath = Paths.get(data.getPhotoPath());
                if (Files.exists(photoPath)) {
                    Image image = new Image(photoPath.toUri().toString(), true);
                    photoImageView.setImage(image);
                }
            } catch (Exception e) {
            }
        }
    }

    @FXML
    private void handleSidebarDashboard(ActionEvent event) throws IOException {
        loadScene(event, "main-view.fxml");
    }

    @FXML
    private void handleSidebarCreate(ActionEvent event) throws IOException {
        CVDataManager dataManager = CVDataManager.getInstance();
        dataManager.clearCurrentCVData();
        dataManager.setNavigationSource("preview");
        loadScene(event, "create-cv-view.fxml");
    }

    @FXML
    private void handleBackToEdit(ActionEvent event) throws IOException {
        CVDataManager.getInstance().setNavigationSource("preview");
        loadScene(event, "create-cv-view.fxml");
    }

    @FXML
    private void handleDeleteCV(ActionEvent event) {
        CVDataManager dataManager = CVDataManager.getInstance();
        if (!dataManager.hasCurrentCVData()) {
            return;
        }
        
        CVData cv = dataManager.getCurrentCVData();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete CV");
        alert.setHeaderText("Delete \"" + cv.getFullName() + "\"?");
        alert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            CVRepository repository = CVRepository.getInstance();
            boolean success = repository.deleteCV(cv.getId());
            if (success) {
                dataManager.refreshCVList();
                dataManager.clearCurrentCVData();
                try {
                    loadScene(event, "main-view.fxml");
                } catch (IOException e) {
                    showError("Failed to return to dashboard.");
                }
            } else {
                showError("Failed to delete CV.");
            }
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
