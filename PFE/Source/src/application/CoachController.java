package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CoachController {

    @FXML
    private TableView<Coach> tableView;

    @FXML
    private TableColumn<Coach, Integer> coachIdColumn;

    @FXML
    private TableColumn<Coach, String> fnameColumn;

    @FXML
    private TableColumn<Coach, String> lnameColumn;

    @FXML
    private TableColumn<Coach, String> phoneColumn;

    @FXML
    private TableColumn<Coach, String> sportNameColumn; // Changed to String for sport name

    @FXML
    private TableColumn<Coach, String> identityColumn;

    private Coach selectedCoach;

    @FXML
    void addcoach(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formcoach.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Ajout de coach");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            tableView.getItems().clear();
            loadCoaches();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Add Coach view.");
        }
    }

    @FXML
    void exportToCSV(ActionEvent event) {
        List<Coach> coachs = tableView.getItems();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.append("CoachID,SportName,LastName,FirstName,Phone,Identity\n");

                for (Coach coach : coachs) {
                    writer.append(escapeAndQuote(String.valueOf(coach.getCoachID())) + ",");
                    writer.append(escapeAndQuote(coach.getSportName()) + ",");
                    writer.append(escapeAndQuote(coach.getLname()) + ",");
                    writer.append(escapeAndQuote(coach.getFname()) + ",");
                    writer.append(escapeAndQuote(coach.getPhone()) + ",");
                    writer.append(escapeAndQuote(coach.getIdentity()) + "\n");

                } 

                showAlert(Alert.AlertType.INFORMATION, "Success", "Coachs exported to CSV successfully.");
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Error exporting Coachs to CSV.");
            }
        }
    }

    // M�thode pour �chapper les caract�res sp�ciaux et entourer la valeur par des guillemets
    private String escapeAndQuote(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }


    @FXML
    void initialize() {
        setupTableView();
        loadCoaches();
    }

    private void setupTableView() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

        tableView.setContextMenu(contextMenu);

        editMenuItem.setOnAction(event -> {
            selectedCoach = tableView.getSelectionModel().getSelectedItem();
            if (selectedCoach != null) {
                openEditCoachWindow(selectedCoach);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No coach selected.");
            }
        });

        deleteMenuItem.setOnAction(event -> {
            selectedCoach = tableView.getSelectionModel().getSelectedItem();
            if (selectedCoach != null) {
                showConfirmationDialog(selectedCoach);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No coach selected.");
            }
        });

        lnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLname()));
        fnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFname()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        sportNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSportName())); // Utiliser SimpleStringProperty pour le nom du sport
        identityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentity()));
        coachIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getCoachID()).asObject());
    }

    private void openEditCoachWindow(Coach selectedCoach) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formcoach.fxml"));
            Parent root = loader.load();

            CoachFormController formCoachController = loader.getController();
            formCoachController.initData(selectedCoach);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modification de coach");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            tableView.getItems().clear();
            loadCoaches();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit Coach view.");
        }
    }

    private void loadCoaches() {
        List<Coach> coaches = getAllCoaches();
        tableView.getItems().addAll(coaches);
    }

    private List<Coach> getAllCoaches() {
        List<Coach> coaches = new ArrayList<>();
        String query = "SELECT c.*, s.Name AS SportName FROM Coach c INNER JOIN Sport s ON c.sportID = s.sportID";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Coach coach = new Coach();
                coach.setLname(resultSet.getString("lName"));
                coach.setFname(resultSet.getString("fName"));
                coach.setPhone(resultSet.getString("phone"));
                coach.setIdentity(resultSet.getString("identity"));
                coach.setCoachID(resultSet.getInt("CoachID"));
                coach.setsportIdField(resultSet.getInt("sportID"));

                // R�cup�rer le nom du sport depuis la base de donn�es
                coach.setSportName(resultSet.getString("SportName"));

                coaches.add(coach);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error fetching coaches from database.");
        }

        return coaches;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showConfirmationDialog(Coach coach) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation Delete");
        alert.setContentText("Are you sure you want to delete this coach?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteCoachFromDatabase(coach);
        }
    }

    private void deleteCoachFromDatabase(Coach coach) {
        if (coach == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid coach information.");
            return;
        }

        Integer coachID = coach.getCoachID();
        if (coachID == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid coach ID.");
            return;
        }

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Coach WHERE coachID = ?")) {

            preparedStatement.setInt(1, coachID);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Coach deleted successfully.");
                tableView.getItems().clear();
                loadCoaches();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete coach.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred: " + e.getMessage());
        }
    }
}
