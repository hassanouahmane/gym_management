package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CoachFormController {

    @FXML
    private TextField fname;

    @FXML
    private TextField lname;

    @FXML
    private TextField phone;

    @FXML
    private TextField identity;

    @FXML
    private ComboBox<String> sportIdField;

    private Coach selectedCoach;

    public void setSelectedCoach(Coach coach) {
        this.selectedCoach = coach;
    }

    public void initialize() {
        retrieveSportsFromDatabase();
        if (selectedCoach != null) {
            populateFields(selectedCoach);
        }
    }
    public void initData(Coach coach) {
        setSelectedCoach(coach);
        if (coach != null) {
            populateFields(coach);
        }
    }


    private void populateFields(Coach coach) {
        fname.setText(coach.getFname());
        lname.setText(coach.getLname());
        phone.setText(coach.getPhone());
        identity.setText(coach.getIdentity());
        sportIdField.setValue(String.valueOf(selectedCoach.getsportIdField()));
    }

    private void retrieveSportsFromDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name FROM Sport");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            List<String> sports = new ArrayList<>();
            while (resultSet.next()) {
                sports.add(resultSet.getString("Name"));
            }

            sportIdField.getItems().addAll(sports);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve sports from the database.");
        }
    }

    @FXML
    void save(ActionEvent event) {
        if (!validateInputs()) {
            return; // Arrêtez le traitement si les entrées ne sont pas valides
        }

        String coachFname = fname.getText();
        String coachLname = lname.getText();
        String coachPhone = phone.getText();
        String coachIdentity = identity.getText();
        String selectedSportName = sportIdField.getValue();

        try {
            int selectedSportId = getSportIdFromName(selectedSportName);
            if (selectedSportId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Selected sport not found.");
                return;
            }

            String query;
            if (selectedCoach == null) {
                query = "INSERT INTO Coach (fname, lname, phone, identity, sportID) VALUES (?, ?, ?, ?, ?)";
            } else {
                query = "UPDATE Coach SET fname = ?, lname = ?, phone = ?, identity = ?, sportID = ? WHERE coachID = ?";
            }

            try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setString(1, coachFname);
                preparedStatement.setString(2, coachLname);
                preparedStatement.setString(3, coachPhone);
                preparedStatement.setString(4, coachIdentity);
                preparedStatement.setInt(5, selectedSportId);

                if (selectedCoach != null) {
                    preparedStatement.setInt(6, selectedCoach.getCoachID());
                }

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Coach data saved successfully.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to save coach data.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    @FXML
    void clean(ActionEvent event) {
        fname.clear();
        lname.clear();
        phone.clear();
        identity.clear();
        sportIdField.getSelectionModel().clearSelection();
    }

    private int getSportIdFromName(String sportName) throws SQLException {
        int sportId = -1;

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT sportID FROM Sport WHERE Name = ?")) {

            preparedStatement.setString(1, sportName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                sportId = resultSet.getInt("sportID");
            }
        }

        return sportId;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();

        if (fname.getText().isEmpty() || !fname.getText().matches("[a-zA-Z]+")) {
            errorMessage.append("First name is required and must contain only letters.\n");
        }

        if (lname.getText().isEmpty() || !lname.getText().matches("[a-zA-Z]+")) {
            errorMessage.append("Last name is required and must contain only letters.\n");
        }

        if (phone.getText().isEmpty() || !phone.getText().matches("[0-9]+")) {
            errorMessage.append("Phone number is required and must contain only digits.\n");
        }

        if (identity.getText().isEmpty()) {
            errorMessage.append("Identity is required.\n");
        }

        if (!errorMessage.toString().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", errorMessage.toString());
            return false;
        }

        return true;
    }
}
