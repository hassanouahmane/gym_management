package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SaleController implements Initializable {

	@FXML
	private Button addBtn;

	@FXML
	private TableColumn<Sale, LocalDate> ccreatedAt;

	@FXML
	private TableColumn<Sale, String> cequipement;

	@FXML
	private TableColumn<Sale, String> cmember;

	@FXML
	private TableColumn<Sale, Integer> cpayed;

	@FXML
	private TableColumn<Sale, Double> cprice;

	@FXML
	private TableColumn<Sale, Double> cquantity;

	@FXML
	private TableColumn<Sale, Integer> csaleId;

	@FXML
	private Button export;

	@FXML
	private TableView<Sale> salesTab;

	@FXML
	private TextField search;

	@FXML
	private FontAwesomeIcon vider;

	private Sale selectedSale;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		search.setFocusTraversable(false);
		vider.setVisible(false);
		initializeSaleTab();
		saleTableView();
		search.textProperty().addListener((observable, oldValue, newValue) -> saleTableView());
		setupTableView();
	}

	@FXML
	void SearchText(KeyEvent event) {
		if (search.getText().isEmpty()) {
			vider.setVisible(false);
		} else {
			vider.setVisible(true);
		}
	}

	@FXML
	void Vider(MouseEvent event) {
		search.clear();
		vider.setVisible(false);
		search.requestFocus();
	}

	public void initializeSaleTab() {
		cequipement.setCellValueFactory(new PropertyValueFactory<>("equipement"));
		cmember.setCellValueFactory(new PropertyValueFactory<>("member"));
		cquantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		cprice.setCellValueFactory(new PropertyValueFactory<>("price"));
		cpayed.setCellValueFactory(new PropertyValueFactory<>("payed"));
		ccreatedAt.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
		csaleId.setCellValueFactory(new PropertyValueFactory<>("saleID"));
	}

	public void saleTableView() {
		salesTab.getItems().clear();
		String query;

		if (search.getText().isEmpty()) {
			query = "SELECT s.saleID, s.equipementID, e.Name AS equipement,s.memberID, CONCAT(m.fName, ' ', m.lName) AS member, "
					+ "s.quantity, s.price, s.payed, s.created_at " + "FROM Sale s "
					+ "JOIN Equipement e ON s.equipementID = e.equipementID "
					+ "JOIN Member m ON s.memberID = m.memberID ";
		} else {
			query = "SELECT s.saleID, s.equipementID, e.Name AS equipement,s.memberID, CONCAT(m.fName, ' ', m.lName) AS member, "
					+ "s.quantity, s.price, s.payed, s.created_at " + "FROM Sale s "
					+ "JOIN Equipement e ON s.equipementID = e.equipementID "
					+ "JOIN Member m ON s.memberID = m.memberID WHERE CONCAT(m.fName, ' ', m.lName) LIKE ?";
		}

		try (Connection connection = getConnectionDB()) {

			PreparedStatement statement = connection.prepareStatement(query);
			if (!search.getText().isEmpty()) {
				String text = search.getText().toLowerCase();
				statement.setString(1, "%" + text + "%");
			}
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Sale sale = new Sale();
				sale.setSaleID(result.getInt("saleID"));
				sale.setEquipementID(result.getInt("equipementID"));
				sale.setEquipement(result.getString("equipement"));
				sale.setMemberID(result.getInt("memberID"));
				sale.setMember(result.getString("member"));
				sale.setQuantity(result.getInt("quantity"));
				sale.setPrice(result.getDouble("price"));
				sale.setPayed(result.getDouble("payed"));
				sale.setCreatedAt(result.getDate("created_at").toLocalDate());
				salesTab.getItems().add(sale);
			}

		} catch (SQLException e) {
			e.printStackTrace();
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

	@FXML
	void addSale(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formSale.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);

			Stage stage = new Stage();
			stage.setTitle("Ajout de sale");
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.showAndWait();

			saleTableView();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Error loading Add sale view.");
		}
	}

	private void showAlert(Alert.AlertType alertType, String title, String content) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void setupTableView() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem editMenuItem = new MenuItem("Edit");
		MenuItem deleteMenuItem = new MenuItem("Delete");
		MenuItem receiptMenuItem = new MenuItem("Receipt");
		contextMenu.getItems().addAll(editMenuItem, deleteMenuItem, receiptMenuItem);

		salesTab.setContextMenu(contextMenu);

		editMenuItem.setOnAction(event -> {
			selectedSale = salesTab.getSelectionModel().getSelectedItem();
			if (selectedSale != null) {
				openEditSchedulehWindow(selectedSale);
			} else {
				showAlert(Alert.AlertType.WARNING, "Warning", "No sale selected.");
			}
		});

		deleteMenuItem.setOnAction(event -> {
			selectedSale = salesTab.getSelectionModel().getSelectedItem();
			if (selectedSale != null) {
				showConfirmationDialog(selectedSale);
			} else {
				showAlert(Alert.AlertType.WARNING, "Warning", "No sale selected.");
			}
		});

		receiptMenuItem.setOnAction(event -> {
			selectedSale = salesTab.getSelectionModel().getSelectedItem();
			if (selectedSale != null) {
				exportReceipt(selectedSale);
			} else {
				showAlert(Alert.AlertType.WARNING, "Warning", "No sale selected.");
			}
		});
	}

	private void openEditSchedulehWindow(Sale selectedSale) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formSale.fxml"));
			Parent root = loader.load();

			SaleFormController formSaleController = loader.getController();
			formSaleController.initData(selectedSale);

			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Modification de sale");
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			saleTableView();

		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit sale view.");
		}
	}

	private void showConfirmationDialog(Sale sale) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Confirmation Delete");
		alert.setContentText("Are you sure you want to delete this sale?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			deleteScheduleFromDatabase(sale);
		}
	}

	private void deleteScheduleFromDatabase(Sale sale) {
		if (sale == null) {
			showAlert(Alert.AlertType.ERROR, "Error", "Invalid sale information.");
			return;
		}

		Integer scheduleID = sale.getSaleID();
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection
						.prepareStatement("DELETE FROM Sale WHERE saleID = ?")) {

			preparedStatement.setInt(1, scheduleID);

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Sale deleted successfully.");
				saleTableView();
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete sale.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred: " + e.getMessage());
		}
	}

	@FXML
	void exportToCSV(ActionEvent event) {
		List<Sale> sales = salesTab.getItems();
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
		File file = fileChooser.showSaveDialog(salesTab.getScene().getWindow());

		if (file != null) {
			try (FileWriter writer = new FileWriter(file)) {
				int[] columnWidths = { 12, 30, 30, 32, 29, 46, 36};
				
				writer.append(String.format("%-" + columnWidths[0] + "s", "Sale ID"));
				writer.append(String.format("%-" + columnWidths[1] + "s", "Equipement Name"));
				writer.append(String.format("%-" + columnWidths[2] + "s", "Member Name"));
				writer.append(String.format("%-" + columnWidths[3] + "s", "Quantity"));
				writer.append(String.format("%-" + columnWidths[4] + "s", "Price"));
				writer.append(String.format("%-" + columnWidths[5] + "s", "Payed"));
				writer.append(String.format("%-" + columnWidths[6] + "s", "Created At"));
				writer.append("\n");
				
				int[] columnWidth = { 16, 46, 42, 35, 36, 38, 28};
				for (Sale sale : sales) {
					writer.append(String.format("%-" + columnWidth[0] + "s", String.valueOf(sale.getSaleID())));
					writer.append(String.format("%-" + columnWidth[1] + "s", sale.getEquipement()));
					writer.append(String.format("%-" + columnWidth[2] + "s", sale.getMember()));
					writer.append(String.format("%-" + columnWidth[3] + "s", String.valueOf(sale.getQuantity())));
					writer.append(String.format("%-" + columnWidth[4] + "s", String.valueOf(sale.getPrice())));
					writer.append(String.format("%-" + columnWidth[5] + "s", String.valueOf(sale.getPayed())));
					writer.append(String.format("%-" + columnWidth[6] + "s", String.valueOf(sale.getCreatedAt())));
					writer.append("\n");
				}

				showAlert(Alert.AlertType.INFORMATION, "Success", "Sales exported to CSV successfully.");
			} catch (IOException e) {
				e.printStackTrace();
				showAlert(Alert.AlertType.ERROR, "Error", "Error exporting Sales to CSV.");
			}
		}
	}

	void exportReceipt(Sale sale) {
	    String fileName = sale.getMember() + "_" + sale.getEquipement() + "_" + sale.getCreatedAt().toString().replaceAll("\\s", "_");
	    FileChooser fileChooser = new FileChooser();
	    fileChooser.setInitialFileName(fileName);
	    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
	    File file = fileChooser.showSaveDialog(salesTab.getScene().getWindow());

	    if (file != null) {
	        try (FileWriter writer = new FileWriter(file)) {
	            writer.append("Equipment Name : " + sale.getEquipement() + "\n");
	            writer.append("Member Name : " + sale.getMember() + "\n");
	            writer.append("Quantity : " + String.valueOf(sale.getQuantity()) + "\n");
	            writer.append("Price : " + String.valueOf(sale.getPrice()) + "\n");
	            writer.append("Paid : " + String.valueOf(sale.getPayed()) + "\n");
	            writer.append("Created At : " + String.valueOf(sale.getCreatedAt()) + "\n");

	            showAlert(Alert.AlertType.INFORMATION, "Success", "Sale exported to Text File successfully.");
	        } catch (IOException e) {
	            e.printStackTrace();
	            showAlert(Alert.AlertType.ERROR, "Error", "Error exporting Sale to Text File.");
	        }
	    }
	}
}
