package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class UserFormController {
	private Main main =Main.getInstance();
	private User user=main.getUser();
	private User selectedUser;
	
    @FXML
    private TextField fname;

    @FXML
    private TextField lname;

    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private ComboBox<String> authority;

    @FXML
    private Button saveBtn;
    
    
    public void initData(User user) {
    	this.selectedUser=user;
        fname.setText(user.getFName());
        lname.setText(user.getLName());
        username.setText(user.getUsername());
        password.setText(user.getPassword());
        authority.setValue(String.valueOf(user.getAuthority()));
    }
    
    @FXML
    void initialize() {
    	ObservableList<String> items = FXCollections.observableArrayList("a", "s");
        authority.setItems(items);
    }

    @FXML
    void saveUser(ActionEvent event) {
        String firstName = fname.getText();
        String lastName = lname.getText();
        String userName = username.getText();
        String pass = password.getText();
        String auth = authority.getValue();

        if (firstName.isEmpty() || lastName.isEmpty() || userName.isEmpty() || pass.isEmpty() || auth == null) {
            showAlert(AlertType.ERROR, "Error", "All fields are required.");
            return;
        }

        boolean saved;
        if (selectedUser != null) {
            saved = updateUserInDatabase(selectedUser, firstName, lastName, userName, pass, auth);
        } else {
            saved = saveToDatabase(firstName, lastName, userName, pass, auth);
        }

        if (saved) {
            showAlert(AlertType.INFORMATION, "Success", "User saved successfully.");
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to save user.");
        }
    }

    private boolean updateUserInDatabase(User user, String firstName, String lastName, String userName, String password, String authority) {
        

        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd())) {
            // SQL statement to update user data
            String sql = "UPDATE user SET fname=?, lname=?, username=?, password=?, authority=? WHERE userID=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, userName);
            statement.setString(4, password);
            statement.setString(5, authority);
            statement.setInt(6, user.getUserID());

            int rowsUpdated = statement.executeUpdate();

            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean saveToDatabase(String firstName, String lastName, String userName, String password, String authority) {
        // Connection parameters
        String url = "jdbc:mysql://localhost:3306/your_database";
        String user = "your_username";
        String pass = "your_password";

        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd())) {
            // SQL statement to insert user data
            String sql = "INSERT INTO user (fname, lname, username, password, authority) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, userName);
            statement.setString(4, password);
            statement.setString(5, authority);

            int rowsInserted = statement.executeUpdate();

            return rowsInserted > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        fname.clear();
        lname.clear();
        username.clear();
        password.clear();
        authority.getSelectionModel().clearSelection();
    }
}

