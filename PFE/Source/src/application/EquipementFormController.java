// EquipementFormController.java
package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EquipementFormController {

    @FXML
    private TextField name;

    @FXML
    private TextField quantity2;

    @FXML
    private TextField price;

    @FXML
    private TextArea discription;

    private EquipementController equipementController;
    private Connection connection;
    private Equipement selectedEquipement;

    public void initData(Equipement equipement, EquipementController controller, Connection connection) {
        selectedEquipement = equipement;
        if (equipement != null) {
            name.setText(equipement.getName());
            quantity2.setText(String.valueOf(equipement.getQuantity()));
            price.setText(String.valueOf(equipement.getPrice()));
            discription.setText(equipement.getDescription());
        }
        equipementController = controller;
        this.connection = connection;
    }



        private void showAlert(Alert.AlertType alertType, String title, String content) {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        }

        


    @FXML
    void save(ActionEvent event) {
        String equipementName = name.getText();
        String equipementQuantityText = quantity2.getText();
        String equipementPriceText = price.getText();
        String equipementDescription = discription.getText();

        // Vérifier si les champs obligatoires sont vides
        if (equipementName.isEmpty() || equipementQuantityText.isEmpty() || equipementPriceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please fill in all required fields.");
            return; // Sortir de la méthode car les champs obligatoires sont vides
        }

        // Vérifier si les champs de quantité et de prix sont des nombres valides
        int equipementQuantity;
        double equipementPrice;
        try {
            equipementQuantity = Integer.parseInt(equipementQuantityText);
            equipementPrice = Double.parseDouble(equipementPriceText);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Please enter valid numbers for quantity and price.");
            return; // Sortir de la méthode car les nombres sont invalides
        }

        // Effectuer l'opération de sauvegarde dans la base de données
        try {
            String query;
            if (selectedEquipement == null) {
                query = "INSERT INTO equipement (Name, quantity, price, description) VALUES (?, ?, ?, ?)";
            } else {
                query = "UPDATE equipement SET Name = ?, quantity = ?, price = ?, description = ? WHERE equipementID = ?";
            }

            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, equipementName);
            preparedStatement.setInt(2, equipementQuantity);
            preparedStatement.setDouble(3, equipementPrice);
            preparedStatement.setString(4, equipementDescription);

            if (selectedEquipement != null) {
                preparedStatement.setInt(5, selectedEquipement.getEquipementID());
            }

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Equipment " + (selectedEquipement == null ? "added" : "updated") + " successfully.");
                alert.showAndWait();

                clean();
                equipementController.populateEquipementTable();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to " + (selectedEquipement == null ? "add" : "update") + " equipment.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while " + (selectedEquipement == null ? "adding" : "updating") + " equipment.");
        }
    }

    @FXML
    void clean() {
        name.clear();
        quantity2.clear();
        price.clear();
        discription.clear();
    }
}
