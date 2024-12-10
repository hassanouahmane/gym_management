package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ConfigDbFormController implements Initializable {

	@FXML
    private Button clean;

    @FXML
    private TextField password;

    @FXML
    private Button save;

    @FXML
    private TextField url;

    @FXML
    private TextField username;
    
    private ConfigurationHandler configurationHandler;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		configurationHandler = new ConfigurationHandler();
        loadConfiguration();
	}
	
	private void loadConfiguration() {
        Configuration configuration = configurationHandler.getConfiguration();
        if (configuration != null) {
            url.setText(configuration.getDbUrl());
            username.setText(configuration.getDbUsername());
            password.setText(configuration.getDbPassword());
            url.setFocusTraversable(true);
        }
    }
	
	@FXML
	void clean(ActionEvent event) {
		url.clear();
		username.clear();
		password.clear();
	}
	
	@FXML
	void save(ActionEvent event) {
	    String dbUrl = url.getText();
        String dbUsername = username.getText();
        String dbPassword = password.getText();

        Configuration configuration = new Configuration();
        configuration.setDbUrl(dbUrl);
        configuration.setDbUsername(dbUsername);
        configuration.setDbPassword(dbPassword);

        configurationHandler.saveConfiguration(configuration);
        showAlert(Alert.AlertType.INFORMATION, "Success", "Database configuration info saved successfully.");
	}
	
	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
