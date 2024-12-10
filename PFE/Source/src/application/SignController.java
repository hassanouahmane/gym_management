package application;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;

public class SignController {
	private Main main=Main.getInstance();
    private User user = main.getUser();
    
    
    
    @FXML
    private ImageView login_logo;


    @FXML
    private TextField email_input;

    @FXML
    private PasswordField password_input;

    @FXML
    private Button login; 

    @FXML
    void login(ActionEvent event) {
        String email = email_input.getText();
        String pwd = password_input.getText();

        if (checkLogin(email, pwd)) {
            Stage stage = (Stage) email_input.getScene().getWindow();
            stage.close();
  
            try {
            	setUserInfo(user, email);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/main.fxml"));
                Parent root = loader.load();
                Stage dashboardStage = new Stage();
                dashboardStage.setTitle("FitPRO");
                dashboardStage.setScene(new Scene(root));
                dashboardStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            System.out.println(user);
        }
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


private Boolean checkLogin(String email, String password) {
    try {
        Connection connection = DriverManager.getConnection(main.getDb_url(),main.getDb_usr(),main.getDb_pwd());

        String selectQuery = "SELECT password FROM User WHERE username=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, email);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String storedPwd = resultSet.getString("password");
                    if (password.equals(storedPwd)) {
                        System.out.println("Login successful!");
                        return true;
                    } else {
                        showAlert("Invalid Password", "Login failed", "Invalid password. Please try again.");
                    }
                } else {
                    showAlert("User Not Found", "Login failed", "User not found. Please check your email.");
                }
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Login failed", "An error occurred during login. Please try again later.");
    }
    return false;
}

private void showAlert(String title, String headerText, String contentText) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(headerText);
    alert.setContentText(contentText);
    alert.showAndWait();
}

    private void setUserInfo(User user, String email) {
        try {
            Connection connection = DriverManager.getConnection(main.getDb_url(),main.getDb_usr(),main.getDb_pwd());

            String selectQuery = "SELECT * FROM User WHERE username=?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
                preparedStatement.setString(1, email);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        user.setUsername("username");
                        user.setUserID(resultSet.getInt("userID"));
                        user.setFName(resultSet.getString("FName"));
                        user.setLName(resultSet.getString("LName"));
                        user.setPassword(resultSet.getString("password"));
                        user.setAuthority(resultSet.getString("authority").charAt(0));
                        System.out.println(user);
                    } else {
                        System.out.println("User not found. Login failed.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
