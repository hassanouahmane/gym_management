package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SignupController {
	
	@FXML
    private ImageView login_logo; 

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField verificationPasswordField;
    @FXML
    private Button signuplogin;
    @FXML
    private Button signUpButton;

    @FXML
    void handleSignUpButtonAction(ActionEvent event) throws IOException {
    	
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String verificationPassword = verificationPasswordField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || verificationPassword.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Veuillez remplir tous les champs");
            alert.showAndWait();
            return;
        }

        if (!password.equals(verificationPassword)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Les mots de passe ne correspondent pas");
            alert.showAndWait();
            return;
        }

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/expenses", "root", "");

            

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Users (user_fname, user_lname, email, password) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, password);
            preparedStatement.executeUpdate();
            
            connection.close();
            Stage stage = (Stage) signUpButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("login");
            dashboardStage.setScene(new Scene(root));
            dashboardStage.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    } @FXML
    void handleloginSignUpButtonAction(ActionEvent event)  throws IOException {
        signuplogin("login.fxml");
    }

    private void  signuplogin(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Parent root = loader.load();

        Stage currentStage = (Stage)  signuplogin.getScene().getWindow();
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.show();
    }
    
    @FXML
    void initialize() {
    	Timeline timeline = new Timeline();

        KeyFrame keyFrame1 = new KeyFrame(Duration.ZERO,
                new KeyValue(login_logo.scaleXProperty(), 0.0),
                new KeyValue(login_logo.scaleYProperty(), 0.0));

        KeyFrame keyFrame2 = new KeyFrame(Duration.seconds(0.75),
                new KeyValue(login_logo.scaleXProperty(), 1.2),
                new KeyValue(login_logo.scaleYProperty(), 1.2));

        KeyFrame keyFrame3 = new KeyFrame(Duration.seconds(1.5),
                new KeyValue(login_logo.scaleXProperty(), 1.0),
                new KeyValue(login_logo.scaleYProperty(), 1.0));

        timeline.getKeyFrames().addAll(keyFrame1, keyFrame2, keyFrame3);

        timeline.play();
    }
    public void initialize(URL location, ResourceBundle resources) {
    	

    }
}
