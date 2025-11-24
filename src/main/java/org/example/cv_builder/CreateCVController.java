package org.example.cv_builder;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.cv_builder.database.CVRepository;

import java.io.File;
import java.io.IOException;

public class CreateCVController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextArea addressArea;

    @FXML
    private TextArea educationArea;

    @FXML
    private TextArea skillsArea;

    @FXML
    private TextArea workExperienceArea;

    @FXML
    private TextArea projectsArea;

    @FXML
    private Button uploadPhotoButton;

    @FXML
    private Button generateCVButton;

    @FXML
    private ImageView photoPreviewImageView;

    private String selectedPhotoPath;
    private Integer currentCVId;
    private CVRepository repository;

    @FXML
    public void initialize() {
        repository = CVRepository.getInstance();
        loadExistingData();
    }

    private void loadExistingData() {
        CVDataManager dataManager = CVDataManager.getInstance();
        if (dataManager.hasCurrentCVData()) {
            CVData data = dataManager.getCurrentCVData();
            currentCVId = data.getId();
            
            if (data.getFullName() != null) fullNameField.setText(data.getFullName());
            if (data.getEmail() != null) emailField.setText(data.getEmail());
            if (data.getPhone() != null) phoneField.setText(data.getPhone());
            if (data.getAddress() != null) addressArea.setText(data.getAddress());
            if (data.getEducation() != null) educationArea.setText(data.getEducation());
            if (data.getSkills() != null) skillsArea.setText(data.getSkills());
            if (data.getWorkExperience() != null) workExperienceArea.setText(data.getWorkExperience());
            if (data.getProjects() != null) projectsArea.setText(data.getProjects());
            selectedPhotoPath = data.getPhotoPath();
            
            if (selectedPhotoPath != null && !selectedPhotoPath.isBlank()) {
                uploadPhotoButton.setText("Change Photo");
                Image image = new Image(selectedPhotoPath, true);
                photoPreviewImageView.setImage(image);
            }
            
            if (currentCVId != null && generateCVButton != null) {
                generateCVButton.setText("Update CV");
            }
        }
    }

    @FXML
    private void handleSidebarDashboard(ActionEvent event) throws IOException {
        saveCurrentData();
        loadScene(event, "main-view.fxml");
    }

    @FXML
    private void handleSidebarCreate(ActionEvent event) throws IOException {}

    @FXML
    private void handleBackToHome(ActionEvent event) throws IOException {
        CVDataManager.getInstance().clearCurrentCVData();
        loadScene(event, "main-view.fxml");
    }

    private void saveCurrentData() {
        CVData data = new CVData();
        data.setFullName(fullNameField.getText());
        data.setEmail(emailField.getText());
        data.setPhone(phoneField.getText());
        data.setAddress(addressArea.getText());
        data.setEducation(educationArea.getText());
        data.setSkills(skillsArea.getText());
        data.setWorkExperience(workExperienceArea.getText());
        data.setProjects(projectsArea.getText());
        data.setPhotoPath(selectedPhotoPath);
        CVDataManager.getInstance().setCurrentCVData(data);
    }

    @FXML
    private void handleUploadPhoto(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Profile Photo");
        chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            selectedPhotoPath = file.toURI().toString();
            uploadPhotoButton.setText("Change Photo");
            photoPreviewImageView.setImage(new Image(selectedPhotoPath, true));
        }
    }

    @FXML
    private void handleGenerateCv(ActionEvent event) throws IOException {
        if (!validate()) {
            return;
        }

        CVData data = new CVData();
        data.setFullName(fullNameField.getText().trim());
        data.setEmail(emailField.getText().trim());
        data.setPhone(phoneField.getText().trim());
        data.setAddress(addressArea.getText().trim());
        data.setEducation(educationArea.getText().trim());
        data.setSkills(skillsArea.getText().trim());
        data.setWorkExperience(workExperienceArea.getText().trim());
        data.setProjects(projectsArea.getText().trim());
        data.setPhotoPath(selectedPhotoPath);

        if (currentCVId != null) {
            data.setId(currentCVId);
            boolean success = repository.updateCV(data);
            if (!success) {
                showError("Failed to update CV. Please try again.");
                return;
            }
        } else {
            int generatedId = repository.insertCV(data);
            if (generatedId == -1) {
                showError("Failed to save CV. Please try again.");
                return;
            }
            data.setId(generatedId);
        }

        CVDataManager dataManager = CVDataManager.getInstance();
        dataManager.setCurrentCVData(data);
        dataManager.refreshCVList();

        FXMLLoader loader = new FXMLLoader(mainApplication.class.getResource("preview-cv-view.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 650);
        scene.getStylesheets().add(
            mainApplication.class.getResource("styles/style.css").toExternalForm()
        );
        
        PreviewCVController controller = loader.getController();
        controller.setData(data);
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validate() {
        StringBuilder errors = new StringBuilder();

        if (fullNameField.getText() == null || fullNameField.getText().isBlank()) {
            errors.append("Full Name is required.\n");
        }
        
        String email = emailField.getText();
        if (email == null || email.isBlank()) {
            errors.append("Email is required.\n");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.append("Invalid email format.\n");
        }
        
        String phone = phoneField.getText();
        if (phone == null || phone.isBlank()) {
            errors.append("Phone Number is required.\n");
        } else if (!phone.matches("^(\\+8801|01)[0-9]{9}$")) {
            errors.append("Invalid phone format. Use +8801XXXXXXXXX or 01XXXXXXXXX format.\n");
        }

        if (errors.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Data");
            alert.setHeaderText("Please correct the following problems:");
            alert.setContentText(errors.toString());
            alert.showAndWait();
            return false;
        }

        return true;
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
