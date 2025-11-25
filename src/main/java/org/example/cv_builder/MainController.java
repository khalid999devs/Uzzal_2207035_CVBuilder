package org.example.cv_builder;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.cv_builder.concurrent.DatabaseExecutor;
import org.example.cv_builder.database.CVRepository;
import org.example.cv_builder.observer.CVObserver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MainController implements CVObserver {

    @FXML
    private FlowPane cvListContainer;

    private CVRepository repository;
    private CVDataManager dataManager;
    private ProgressIndicator loadingIndicator;

    @FXML
    public void initialize() {
        repository = CVRepository.getInstance();
        dataManager = CVDataManager.getInstance();
        dataManager.addObserver(this);
        
        loadingIndicator = new ProgressIndicator();
        loadingIndicator.setMaxSize(50, 50);
        cvListContainer.getChildren().add(loadingIndicator);
        
        loadCVsAsync();
    }

    private void loadCVsAsync() {
        DatabaseExecutor.executeAsync(() -> {
            dataManager.refreshCVList();
            return dataManager.getSavedCVs();
        }).thenAccept(cvs -> {
            Platform.runLater(() -> {
                cvListContainer.getChildren().remove(loadingIndicator);
                displayCVs(cvs);
            });
        }).exceptionally(ex -> {
            Platform.runLater(() -> {
                cvListContainer.getChildren().remove(loadingIndicator);
                showError("Failed to load CVs. Please try again.");
            });
            return null;
        });
    }

    @Override
    public void onCVListChanged() {
        Platform.runLater(() -> loadCVCards());
    }

    private void displayCVs(List<CVData> savedCVs) {
        cvListContainer.getChildren().clear();
        if (savedCVs.isEmpty()) {
            Label emptyLabel = new Label("No CVs yet. Create your first CV!");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14px;");
            cvListContainer.getChildren().add(emptyLabel);
        } else {
            for (CVData cv : savedCVs) {
                cvListContainer.getChildren().add(createCVCard(cv));
            }
        }
    }

    private void loadCVCards() {
        List<CVData> savedCVs = dataManager.getSavedCVs();
        displayCVs(savedCVs);
    }

    private VBox createCVCard(CVData cv) {
        VBox card = new VBox();
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(200);
        card.setMaxWidth(200);
        card.setPadding(new Insets(0));
        card.setStyle("-fx-cursor: hand;");
        
        javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
        stackPane.setPrefSize(200, 250);
        stackPane.setMaxSize(200, 250);
        
        VBox documentPreview = new VBox();
        documentPreview.setAlignment(Pos.TOP_LEFT);
        documentPreview.setPrefSize(200, 250);
        documentPreview.setMaxSize(200, 250);
        documentPreview.setPadding(new Insets(16));
        documentPreview.getStyleClass().add("cv-card-preview");
        
        VBox previewContent = new VBox();
        previewContent.setSpacing(8);
        
        if (cv.getPhotoPath() != null && !cv.getPhotoPath().isBlank()) {
            try {
                Path photoPath = Paths.get(cv.getPhotoPath());
                if (Files.exists(photoPath)) {
                    ImageView photoView = new ImageView(new Image(photoPath.toUri().toString(), true));
                    photoView.setFitWidth(50);
                    photoView.setFitHeight(50);
                    photoView.setPreserveRatio(true);
                    photoView.setStyle("-fx-background-radius: 25; -fx-border-radius: 25;");
                    previewContent.getChildren().add(photoView);
                }
            } catch (Exception e) {
            }
        }
        
        Label namePreview = new Label(cv.getFullName());
        namePreview.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a1a1a;");
        namePreview.setWrapText(true);
        namePreview.setMaxWidth(170);
        
        Label emailPreview = new Label(cv.getEmail());
        emailPreview.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");
        emailPreview.setWrapText(true);
        emailPreview.setMaxWidth(170);
        
        Label phonePreview = new Label(cv.getPhone());
        phonePreview.setStyle("-fx-font-size: 10px; -fx-text-fill: #888;");
        
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        Label snippet = new Label("Education • Skills • Experience");
        snippet.setStyle("-fx-font-size: 9px; -fx-text-fill: #999;");
        
        previewContent.getChildren().addAll(namePreview, emailPreview, phonePreview, spacer, snippet);
        documentPreview.getChildren().add(previewContent);
        
        HBox buttonBox = new HBox();
        buttonBox.setSpacing(6);
        buttonBox.setAlignment(Pos.TOP_RIGHT);
        buttonBox.setPadding(new Insets(8));
        buttonBox.setVisible(false);
        javafx.scene.layout.StackPane.setAlignment(buttonBox, Pos.TOP_RIGHT);
        
        Button editButton = new Button("e");
        editButton.getStyleClass().add("cv-card-edit-button");
        editButton.setOnAction(e -> handleEditCV(cv));
        
        Button deleteButton = new Button("d");
        deleteButton.getStyleClass().add("cv-card-delete-button");
        deleteButton.setOnAction(e -> handleDeleteCV(cv));
        
        buttonBox.getChildren().addAll(editButton, deleteButton);
        
        stackPane.getChildren().addAll(documentPreview, buttonBox);
        
        stackPane.setOnMouseEntered(e -> buttonBox.setVisible(true));
        stackPane.setOnMouseExited(e -> buttonBox.setVisible(false));
        stackPane.setOnMouseClicked(e -> {
            if (e.getTarget() != editButton && e.getTarget() != deleteButton) {
                handleViewCV(cv);
            }
        });
        
        card.getChildren().add(stackPane);
        
        return card;
    }

    private void handleViewCV(CVData cv) {
        dataManager.setCurrentCVData(cv);
        dataManager.setNavigationSource("dashboard");
        try {
            FXMLLoader loader = new FXMLLoader(mainApplication.class.getResource("preview-cv-view.fxml"));
            Scene scene = new Scene(loader.load(), 1000, 650);
            scene.getStylesheets().add(
                mainApplication.class.getResource("styles/style.css").toExternalForm()
            );
            
            PreviewCVController controller = loader.getController();
            controller.setData(cv);
            
            Stage stage = (Stage) cvListContainer.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Failed to open preview.");
        }
    }

    private void handleEditCV(CVData cv) {
        dataManager.setCurrentCVData(cv);
        dataManager.setNavigationSource("dashboard");
        try {
            loadScene(null, "create-cv-view.fxml");
        } catch (IOException e) {
            showError("Failed to open edit view.");
        }
    }

    private void handleDeleteCV(CVData cv) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete CV");
        alert.setHeaderText("Delete \"" + cv.getFullName() + "\"?");
        alert.setContentText("This action cannot be undone.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean success = repository.deleteCV(cv.getId());
            if (success) {
                dataManager.refreshCVList();
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

    @FXML
    private void handleCreateNewCv(ActionEvent event) throws IOException {
        CVDataManager dataManager = CVDataManager.getInstance();
        dataManager.clearCurrentCVData();
        dataManager.setNavigationSource("dashboard");
        goToCreateScreen(event);
    }

    @FXML
    private void handleSidebarDashboard(ActionEvent event) throws IOException {
        loadScene(event, "main-view.fxml");
    }

    @FXML
    private void handleSidebarCreate(ActionEvent event) throws IOException {
        CVDataManager.getInstance().clearCurrentCVData();
        CVDataManager.getInstance().setNavigationSource("dashboard");
        goToCreateScreen(event);
    }

    private void goToCreateScreen(ActionEvent event) throws IOException {
        loadScene(event, "create-cv-view.fxml");
    }

    private void loadScene(ActionEvent event, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(mainApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(loader.load(), 1000, 650);
        scene.getStylesheets().add(
            mainApplication.class.getResource("styles/style.css").toExternalForm()
        );
        
        Stage stage;
        if (event != null) {
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        } else {
            stage = (Stage) cvListContainer.getScene().getWindow();
        }
        stage.setScene(scene);
    }
}
