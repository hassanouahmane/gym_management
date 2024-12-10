// EquipementController.java
package application;

import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class EquipementController {

    @FXML
    private TableView<Equipement> tableView;

    @FXML
    private TableColumn<Equipement, String> equipementColumn;

    @FXML
    private TableColumn<Equipement, Integer> quantityColumn;

    @FXML
    private TableColumn<Equipement, Double> priceColumn;

    @FXML
    private TableColumn<Equipement, String> discriptionColumn;

    @FXML
    private TextField name;

    @FXML
    private TextField quantity2;

    @FXML
    private TextField price;

    @FXML
    private TextField discription;

    private Connection connection;
    @FXML
    private void initialize() {
        initializeDatabase();
        setupTableColumns();
        setupContextMenu();
    }

    private void setupTableColumns() {
        equipementColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        quantityColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getQuantity()).asObject());
        priceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrice()).asObject());
        discriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
    }
    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
            if (connection != null) {
                System.out.println("Database connection established.");
                
                // Charge les données du tableau à partir de la base de données
                populateEquipementTable();
            } else {
                System.err.println("Failed to establish database connection.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to the database.");
        }
    }


    public void populateEquipementTable() {
        tableView.getItems().clear(); // Clear the table before populating
        try {
            String query = "SELECT * FROM equipement";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Equipement equipement = new Equipement(
                        resultSet.getInt("equipementID"),
                        resultSet.getString("Name"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("price"),
                        resultSet.getString("description")
                );
                tableView.getItems().add(equipement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to retrieve equipment data from the database.");
        }
    }

    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");

        contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

        tableView.setContextMenu(contextMenu);

        editMenuItem.setOnAction(event -> {
            Equipement selectedEquipement = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipement != null) {
                openEditEquipementWindow(selectedEquipement);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No equipment selected.");
            }
        });

        deleteMenuItem.setOnAction(event -> {
            Equipement selectedEquipement = tableView.getSelectionModel().getSelectedItem();
            if (selectedEquipement != null) {
                showConfirmationDialog(selectedEquipement);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No equipment selected.");
            }
        });
    }

    private void showConfirmationDialog(Equipement selectedEquipement) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation Delete");
        alert.setContentText("Are you sure you want to delete this equipment?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                deleteEquipementFromDatabase(selectedEquipement);
            }
        });
    }

    private void openEditEquipementWindow(Equipement selectedEquipement) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/FormEquipement.fxml"));
            Parent root = loader.load();

            EquipementFormController controller = loader.getController();
            controller.initData(selectedEquipement, this, connection);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Edit Equipment");
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit Equipment view.");
        }
    }

    @FXML
    private void equipement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/FormEquipement.fxml"));
            Parent root = loader.load();

            EquipementFormController formController = loader.getController();
            formController.initData(null, this, connection);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Add Equipment");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Equipment Form view.");
        }
    }

    void updateEquipementInDatabase(Equipement equipement) {
        try {
            String query = "UPDATE equipement SET Name = ?, quantity = ?, price = ?, description = ? WHERE equipementID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, equipement.getName());
            preparedStatement.setInt(2, equipement.getQuantity());
            preparedStatement.setDouble(3, equipement.getPrice());
            preparedStatement.setString(4, equipement.getDescription());
            preparedStatement.setInt(5, equipement.getEquipementID());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment updated successfully.");
                populateEquipementTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update equipment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    private void deleteEquipementFromDatabase(Equipement equipement) {
        try {
            String query = "DELETE FROM equipement WHERE equipementID = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, equipement.getEquipementID());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Equipment deleted successfully.");
                populateEquipementTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete equipment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }
    @FXML
    private void sele(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/sele.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading select view.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
