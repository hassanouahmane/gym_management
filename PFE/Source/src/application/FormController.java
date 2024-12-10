package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.*;

public class FormController {

    @FXML
    private TextField fname;

    @FXML
    private TextField lname;

    @FXML
    private TextField phone;

    @FXML
    private TextField email;

    @FXML
    private RadioButton male_gender;

    @FXML
    private RadioButton female_gender;

    @FXML
    private TextField identity;

    @FXML
    private DatePicker subscription_dat;

    @FXML
    private TextArea description;

    @FXML
    private DatePicker birth_date;

    @FXML
    private ComboBox<String> Sport;

    private Member selectedMember;

    @FXML
    private void initialize() {
        loadSports();
    }

    public void initData(Member member) {
        selectedMember = member;
        fname.setText(member.getFname());
        lname.setText(member.getLname());
        phone.setText(member.getPhone());
        if (member.getGender().equals("Male")) {
            male_gender.setSelected(true);
        } else if (member.getGender().equals("Female")) {
            female_gender.setSelected(true);
        }
        identity.setText(member.getIdentity());
        subscription_dat.setValue(member.getSubscription_date().toLocalDateTime().toLocalDate());
        description.setText(member.getDescription());
        birth_date.setValue(member.getBirthday());

        if (member.getEmail() != null && !member.getEmail().isEmpty()) {
            email.setText(member.getEmail());
        } else {
            email.setText("");
        }

        if (member.getSport() != null && !member.getSport().isEmpty()) {
            Sport.setValue(member.getSport());
        }
    }

    @FXML
    void save(ActionEvent event) {
        if (validateInputs()) {
            if (selectedMember == null) {
                insertData();
            } else {
                updateData();
            }
            showSuccessAlert();
        } else {
            showErrorAlert();
        }
    }

    private boolean validateInputs() {
        if (fname.getText().isEmpty() || lname.getText().isEmpty() || phone.getText().isEmpty() ||
                identity.getText().isEmpty() || subscription_dat.getValue() == null || birth_date.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the required fields.");
            return false;
        }
        return true;
    }
 
    private void insertData() {
        String query = "INSERT INTO Member (fName, lName, phone, gender, birthday, identity, subscription_date, note, email) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, fname.getText());
            preparedStatement.setString(2, lname.getText());
            preparedStatement.setString(3, phone.getText());
            preparedStatement.setString(4, male_gender.isSelected() ? "Male" : "Female");
            preparedStatement.setDate(5, java.sql.Date.valueOf(birth_date.getValue()));
            preparedStatement.setString(6, identity.getText());
            preparedStatement.setDate(7, java.sql.Date.valueOf(subscription_dat.getValue()));
            preparedStatement.setString(8, description.getText());
            preparedStatement.setString(9, email.getText());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) { 
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int memberId = generatedKeys.getInt(1);

                        // Insertion des sports s�lectionn�s dans la table member_sport
                        insertMemberSports(memberId);
                    }
                }
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add member.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    private void updateData() {
        String query = "UPDATE Member SET fName=?, lName=?, phone=?, gender=?, birthday=?, identity=?, subscription_date=?, note=?, email=? WHERE memberID=?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, fname.getText());
            preparedStatement.setString(2, lname.getText());
            preparedStatement.setString(3, phone.getText());
            preparedStatement.setString(4, male_gender.isSelected() ? "Male" : "Female");
            preparedStatement.setDate(5, java.sql.Date.valueOf(birth_date.getValue()));
            preparedStatement.setString(6, identity.getText());
            preparedStatement.setDate(7, java.sql.Date.valueOf(subscription_dat.getValue()));
            preparedStatement.setString(8, description.getText()); // Utilisation de la colonne note
            preparedStatement.setString(9, email.getText()); // Ajout de l'email
            preparedStatement.setInt(10, selectedMember.getMemberID()); // Utilisez l'ID du membre s�lectionn�

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert();
        }
    }

    private void insertMemberSports(int memberId) {
        String insertSportQuery = "INSERT INTO member_sport (memberID, sportID) VALUES (?, ?)";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(insertSportQuery)) {

            String selectedSport = Sport.getValue();
            int sportId = getSportId(selectedSport); // Obtention de l'ID du sport s�lectionn�

            preparedStatement.setInt(1, memberId);
            preparedStatement.setInt(2, sportId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getSportId(String sportName) {
        int sportId = 0;
        String query = "SELECT sportID FROM sport WHERE Name = ?";
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, sportName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    sportId = resultSet.getInt("sportID");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sportId;
    }

    private void loadSports() {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT Name FROM sport");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Sport.getItems().add(resultSet.getString("Name"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccessAlert() {
        showAlert(Alert.AlertType.INFORMATION, "Success", "Member saved successfully.");
    }

    private void showErrorAlert() {
        showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all the required fields.");
    }

    @FXML
    void clean(ActionEvent event) {
        fname.clear();
        lname.clear();
        phone.clear();
        email.clear();
        identity.clear();
        subscription_dat.setValue(null);
        description.clear();
        birth_date.setValue(null);
        Sport.getSelectionModel().clearSelection();
        male_gender.setSelected(false);
        female_gender.setSelected(false);
    }
}
