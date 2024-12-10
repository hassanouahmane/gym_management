package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SaleFormController implements Initializable {

	@FXML
	private Button clean;
	
	@FXML
	private Button save;

	@FXML
    private ComboBox<String> equipement;

    @FXML
    private ComboBox<String> member;

    @FXML
    private TextField payed;

    @FXML
    private TextField price;

    @FXML
    private TextField quantity;

	private Map<Integer, String> equipementsMap;

	private Map<Integer, String> membersMap;
	
	private Sale selectedSale;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		payed.setFocusTraversable(false);
		quantity.setFocusTraversable(false);
		price.setFocusTraversable(false);
		retrieveEquipementsFromDatabase();
		retrieveMembersFromDatabase();
		equipement.getSelectionModel().selectFirst();
		member.getSelectionModel().selectFirst();
	}

	private void retrieveEquipementsFromDatabase() {
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT equipementID, Name FROM Equipement ORDER BY Name");
				ResultSet resultSet = preparedStatement.executeQuery()) {

			equipementsMap = new LinkedHashMap<>();
			equipementsMap.put(0, "Select equipement");

			while (resultSet.next()) {
				int ID = resultSet.getInt("equipementID");
				String Name = resultSet.getString("Name");
				equipementsMap.put(ID, Name);
			}

			equipement.getItems().addAll(equipementsMap.values());
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve equipements from the database.");
		}
	}
	
	private void retrieveMembersFromDatabase() {
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT memberID, CONCAT(fName,' ',lName) as member FROM Member ORDER BY CONCAT(fName,' ',lName)");
				ResultSet resultSet = preparedStatement.executeQuery()) {

			membersMap = new LinkedHashMap<>();
			membersMap.put(0, "Select member");

			while (resultSet.next()) {
				int ID = resultSet.getInt("memberID");
				String Name = resultSet.getString("member");
				membersMap.put(ID, Name);
			}

			member.getItems().addAll(membersMap.values());
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve members from the database.");
		}
	}

	public static Connection getConnectionDB() {
		Main main = Main.getInstance();

		Connection connection = null;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			try {
				connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		return connection;
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
	    if (!validateInputs()) {
	        return; // Stop processing if inputs are not valid
	    }

	    String selectedEquipementName = equipement.getValue();
	    String selectedMemberName = member.getValue();
	    Double priceV = Double.valueOf(price.getText());
	    Double payedV = Double.valueOf(payed.getText());
	    int quantityV = Integer.valueOf(quantity.getText());

	    try {
	        int selectedEquipementId = getEquipementIdFromName(selectedEquipementName); // Retrieve sport ID
	        int selectedMemberId = getMemberIdFromName(selectedMemberName); // Retrieve coach ID

	        if (selectedEquipementId == -1) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Selected equipement not found.");
	            return;
	        }

	        if (selectedMemberId == -1) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Selected member not found.");
	            return;
	        }

	        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
	        PreparedStatement preparedStatement;

	        if (selectedSale == null) {
	            preparedStatement = connection.prepareStatement(
	                     "INSERT INTO Sale (equipementID, memberID, price, payed, quantity) VALUES (?, ?, ?, ?, ?)");
	        } else {
	            preparedStatement = connection.prepareStatement(
	                     "UPDATE Sale SET equipementID = ?, memberID = ?, price = ?, payed = ?, quantity = ? WHERE saleID = ?");
	            preparedStatement.setInt(6, selectedSale.getSaleID());
	        }

	        preparedStatement.setInt(1, selectedEquipementId);
	        preparedStatement.setInt(2, selectedMemberId);
	        preparedStatement.setDouble(3, priceV);
	        preparedStatement.setDouble(4, payedV);
	        preparedStatement.setInt(5, quantityV);

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected > 0) {
	            showAlert(Alert.AlertType.INFORMATION, "Success", "Sale data saved successfully.");
	        } else {
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save sale data.");
	        }

	        connection.close(); // Close connection
	    } catch (SQLException e) {
	        e.printStackTrace();
	        showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
	    }
	}

	
	private int getEquipementIdFromName(String Name) {
	    for (Map.Entry<Integer, String> entry : equipementsMap.entrySet()) {
	        if (entry.getValue().equals(Name)) {
	            return entry.getKey();
	        }
	    }
	    return -1; // If sport ID is not found
	}

	private int getMemberIdFromName(String Name) {
	    for (Map.Entry<Integer, String> entry : membersMap.entrySet()) {
	        if (entry.getValue().equals(Name)) {
	            return entry.getKey();
	        }
	    }
	    return -1; // If coach ID is not found
	}

	
	private boolean validateInputs() {
	    StringBuilder errorMessage = new StringBuilder();

	    // Validate coach selection
	    if (equipement.getValue() == null || equipement.getValue().isEmpty() || equipement.getValue().equals("Select equipement")) {
	        errorMessage.append("Please select a equipement.\n");
	    }

	    // Validate day selection
	    if (member.getValue() == null || member.getValue().isEmpty() || member.getValue().equals("Select member")) {
	        errorMessage.append("Please select a member.\n");
	    }

	    // Validate salle
	    if (price.getText().isEmpty()) {
	        errorMessage.append("Price is required.\n");
	    }
	    
	    if (payed.getText().isEmpty()) {
	        errorMessage.append("Payed is required.\n");
	    }
	    
	    if (quantity.getText().isEmpty()) {
	        errorMessage.append("Quantity is required.\n");
	    }

	    // Validate note
	    // No specific validation for note field. Assuming it's optional.

	    if (!errorMessage.toString().isEmpty()) {
	        showAlert(Alert.AlertType.ERROR, "Error", errorMessage.toString());
	        return false;
	    }

	    return true;
	}
	
	public void initData(Sale sale) {
		selectedSale = sale;
	    equipement.getSelectionModel().select(sale.getEquipement());
	    member.getSelectionModel().select(sale.getMember());
	    price.setText(String.valueOf(sale.getPrice()));
	    payed.setText(String.valueOf(sale.getPayed()));
	    quantity.setText(String.valueOf(sale.getQuantity()));
	}

	@FXML
	void clean(ActionEvent event) {
		price.clear();
		payed.clear();
		quantity.clear();
		equipement.getSelectionModel().selectFirst();
		member.getSelectionModel().selectFirst();
	}
}