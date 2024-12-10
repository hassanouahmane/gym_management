package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;

public class AdherentController1 {

	@FXML
    private Button adduserbtn;

    @FXML
    private Button csv;

    @FXML
    private TextField search2;

    @FXML
    private FontAwesomeIcon vider2;


    @FXML
    private Button reset;

    @FXML
	private TableColumn<Member, Integer> cbirthday;

	@FXML
	private TableColumn<Member, String> cgender;

	@FXML
	private TableColumn<Member, String> cidentity;

	@FXML
	private TableColumn<Member, String> cmember2;

	@FXML
	private TableColumn<Member, String> cphone;

	@FXML
	private TableColumn<Member, Integer> csale;

	@FXML
	private TableColumn<Member, String> csport2;

	@FXML
	private TableColumn<Member, String> cstat;

	@FXML
	private TableColumn<Member, Date> csubscription;

	@FXML
	private TableView<Member> memberTab;

	@FXML
	private ComboBox<String> saleBox;

	@FXML
	private ComboBox<String> statBox;

	@FXML
	private ComboBox<String> subBox;

	@FXML
	private ComboBox<String> genderBox;

	@FXML
	private ComboBox<String> ageBox;
	
	private MainController mainController=MainController.getInstance(); 


    @FXML
    void SearchText(KeyEvent event) {
        if (search2.getText().isEmpty()) {
            vider2.setVisible(false);
        } else {
            vider2.setVisible(true);
        }
    }

    @FXML
    void Vider(MouseEvent event) {
        search2.clear();
        vider2.setVisible(false);
        search2.requestFocus();
    }

    @FXML
    void adduser(ActionEvent event) {
    	try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formadherent.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = new Stage();
            stage.setTitle("Ajout d'utilisateur");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            memberTableView();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Add User view.");
        }
    }

    @FXML
    void exportToCSV(ActionEvent event) {
    	List<Member> members = memberTab.getItems();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(memberTab.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) { 
                // En-têtes CSV 
                writer.append("MemberID,LastName,FirstName,Phone,Gender,Birthday,Identity,SubscriptionDate,Note,Email\n");

                for (Member member : members) {
                	writer.append(escapeAndQuote(String.valueOf(member.getMemberID())) + ",");
                    writer.append(escapeAndQuote(member.getLname()) + ",");
                    writer.append(escapeAndQuote(member.getFname()) + ",");
                    writer.append(escapeAndQuote(member.getPhone()) + ",");
                    writer.append(escapeAndQuote(member.getGender()) + ",");
                    writer.append(escapeAndQuote(member.getBirthday().toString()) + ",");
                    writer.append(escapeAndQuote(member.getIdentity()) + ",");
                    writer.append(escapeAndQuote(member.getSubscription_date().toString()) + ",");
                    writer.append(escapeAndQuote(member.getDescription()) + ",");
                    writer.append(escapeAndQuote(member.getEmail()) + "\n");
                }

                showAlert(Alert.AlertType.INFORMATION, "Success", "Members exported to CSV successfully.");
            } catch (IOException e) { 
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "Error exporting members to CSV.");
            }
        }
    }
    private String escapeAndQuote(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    @FXML
    void initialize() {
    	
    	 ContextMenu contextMenu = new ContextMenu();

    	    // Create menu items
    	    MenuItem editMenuItem = new MenuItem("Edit");
    	    MenuItem deleteMenuItem = new MenuItem("Delete");
    	    MenuItem profilMenuItem = new MenuItem("Voir profil");
    	    MenuItem adherenceMenuItem = new MenuItem("Payer adhérence");

    	    contextMenu.getItems().addAll(profilMenuItem, editMenuItem, deleteMenuItem, adherenceMenuItem);

    	    memberTab.setContextMenu(contextMenu);

    	    editMenuItem.setOnAction(event -> {
    	    	
    	        Member selectedMember = memberTab.getSelectionModel().getSelectedItem();
    	        if (selectedMember != null) {
    	        	 Member updatedMember = getMemberByID(selectedMember.getMemberID());
    	            openEditUserWindow(updatedMember);
    	        } else {
    	            showAlert(Alert.AlertType.WARNING, "Warning", "No user selected.");
    	        }
    	    });

    	    deleteMenuItem.setOnAction(event -> {
    	        Member selectedMember = memberTab.getSelectionModel().getSelectedItem();
    	        if (selectedMember != null) {
    	            showConfirmationDialog(selectedMember);
    	        } else {
    	            showAlert(Alert.AlertType.WARNING, "Warning", "No user selected.");
    	        }
    	    });

    	    adherenceMenuItem.setOnAction(event -> {
    	        try {
    	        	
    	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/membership.fxml"));
    	            Parent root = loader.load();

    	            Member selectedMember = memberTab.getSelectionModel().getSelectedItem();
    	            Member updatedMember = getMemberByID(selectedMember.getMemberID());
    	            
    	            MembershipController membershipController = loader.getController();
    	            
    	            membershipController.initialize(updatedMember);

    	            Scene scene = new Scene(root);
    	            Stage stage = new Stage();
    	            stage.setTitle("Payer adhérence");
    	            stage.setScene(scene);
    	            stage.initModality(Modality.APPLICATION_MODAL);
    	            stage.showAndWait();
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Adherence Payment view.");
    	        }
    	    });


    	    profilMenuItem.setOnAction(event -> {
    	        try {
    	            Member selectedMember = memberTab.getSelectionModel().getSelectedItem();
    	            
    	            Member updatedMember = getMemberByID(selectedMember.getMemberID());
    	            
    	            Scene scene = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow().getScene();
    	            
    	            Node previousView = scene.getRoot();
    	            
    	            mainController.switchToProfil(updatedMember, previousView);
    	        } catch (IOException e) {
    	            e.printStackTrace();
    	        }
    	    });
    	    
    	search2.setFocusTraversable(false);
		vider2.setVisible(false);
		search2.textProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		reset.setOnAction(event -> resetBoxes());
		
		initializeMemberTab();
        memberTableView();
        boxes();
        search2.textProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		saleBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		genderBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		subBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		statBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		ageBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		reset.setOnAction(event -> resetBoxes());
    }
    
    public void boxes() {
		List<String> saleItems = Arrays.asList("Sale", "0", "Under 10", "Over 10");
		saleBox.getItems().addAll(saleItems);
		saleBox.getSelectionModel().selectFirst();

		List<String> statItems = Arrays.asList("Stat", "Active", "Inactive");
		statBox.getItems().addAll(statItems);
		statBox.getSelectionModel().selectFirst();

		List<String> subItems = Arrays.asList("Subscription", "Today", "Yesterday", "This month", "This year",
				"Last year");
		subBox.getItems().addAll(subItems);
		subBox.getSelectionModel().selectFirst();

		List<String> genderItems = Arrays.asList("Gender", "Male", "Female");
		genderBox.getItems().addAll(genderItems);
		genderBox.getSelectionModel().selectFirst();

		List<String> ageItems = Arrays.asList("Age", "Under 18", "Over 18");
		ageBox.getItems().addAll(ageItems);
		ageBox.getSelectionModel().selectFirst();
	}
    
    public void resetBoxes() {
		ageBox.getSelectionModel().clearSelection();
		ageBox.getSelectionModel().selectFirst();

		statBox.getSelectionModel().clearSelection();
		statBox.getSelectionModel().selectFirst();

		genderBox.getSelectionModel().clearSelection();
		genderBox.getSelectionModel().selectFirst();

		subBox.getSelectionModel().clearSelection();
		subBox.getSelectionModel().selectFirst();

		saleBox.getSelectionModel().clearSelection();
		saleBox.getSelectionModel().selectFirst();

	}


    private void openEditUserWindow(Member selectedMember) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formadherent.fxml"));
            Parent root = loader.load();

            FormController formController = loader.getController();
            formController.initData(selectedMember); // Pass the controller instance to the FormController

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modification d'utilisateur");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

         
            memberTableView();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit User view.");
        }
    }
    
    public void initializeMemberTab() {
		cbirthday.setCellValueFactory(new PropertyValueFactory<>("age"));
		csport2.setCellValueFactory(new PropertyValueFactory<>("sport"));
		cgender.setCellValueFactory(new PropertyValueFactory<>("gender"));
		cphone.setCellValueFactory(new PropertyValueFactory<>("phone"));
		cmember2.setCellValueFactory(new PropertyValueFactory<>("fullname"));
		csale.setCellValueFactory(new PropertyValueFactory<>("sale"));
		csubscription.setCellValueFactory(new PropertyValueFactory<>("subscription"));
		cstat.setCellValueFactory(new PropertyValueFactory<>("stat"));
		cidentity.setCellValueFactory(new PropertyValueFactory<>("identity"));
	}
    

    public void memberTableView() {
		memberTab.getItems().clear();
		String query;

		if (search2.getText().isEmpty()) {
			query = "SELECT" + "  m.memberID,  m.subscription_date AS csubscription,"
					+ "  IF(CURDATE() BETWEEN mm.fromDate AND mm.toDate, 'Active', 'Inactive') AS cstat,"
					+ "  GROUP_CONCAT(DISTINCT s.Name ORDER BY s.Name SEPARATOR ', ') AS csport2,"
					+ "  COUNT(sale.saleID) AS csale," + "  m.phone AS cphone,"
					+ "  CONCAT(m.fName, ' ', m.lName) AS cmember2," + "  m.identity AS cidentity,"
					+ "  m.gender AS cgender," + "  m.birthday AS cbirthday" + " FROM" + "  Member m"
					+ "  LEFT JOIN Membership mm ON m.memberID = mm.memberID"
					+ "  LEFT JOIN Sale sale ON m.memberID = sale.memberID"
					+ "  LEFT JOIN member_sport ms ON m.memberID = ms.memberID"
					+ "  LEFT JOIN Sport s ON ms.sportID = s.sportID";
			if (statBox.getSelectionModel().getSelectedItem() != null
					&& !statBox.getSelectionModel().getSelectedItem().equals("Stat")) {
				query += " WHERE";
				String statCondition = "";
				if (statBox.getSelectionModel().getSelectedItem().equals("Active")) {
					statCondition = " CURDATE() BETWEEN mm.fromDate AND mm.toDate";
				} else if (statBox.getSelectionModel().getSelectedItem().equals("Inactive")) {
					statCondition = " NOT (CURDATE() BETWEEN mm.fromDate AND mm.toDate)";
				}
				query += statCondition;
			}
			query += " GROUP BY m.memberID";
			Boolean conditionAdded = false;
			if ((ageBox.getSelectionModel().getSelectedItem() != null
					&& !ageBox.getSelectionModel().getSelectedItem().equals("Age"))
					|| (genderBox.getSelectionModel().getSelectedItem() != null
							&& !genderBox.getSelectionModel().getSelectedItem().equals("Gender"))
					|| (subBox.getSelectionModel().getSelectedItem() != null
							&& !subBox.getSelectionModel().getSelectedItem().equals("Subscription"))
					|| (saleBox.getSelectionModel().getSelectedItem() != null
							&& !saleBox.getSelectionModel().getSelectedItem().equals("Sale"))) {
				query += " HAVING";
			}
			if (ageBox.getSelectionModel().getSelectedItem() != null
					&& !ageBox.getSelectionModel().getSelectedItem().equals("Age")) {
				String ageCondition = "";
				if (ageBox.getSelectionModel().getSelectedItem().equals("Under 18")) {
					ageCondition = " m.birthday > DATE_SUB(CURDATE(), INTERVAL 18 YEAR)";
				} else if (ageBox.getSelectionModel().getSelectedItem().equals("Over 18")) {
					ageCondition = " m.birthday <= DATE_SUB(CURDATE(), INTERVAL 18 YEAR)";
				}
				query += (conditionAdded ? " AND" : "") + ageCondition;
				conditionAdded = true;
			}
			if (genderBox.getSelectionModel().getSelectedItem() != null
					&& !genderBox.getSelectionModel().getSelectedItem().equals("Gender")) {
				String statCondition = "";
				if (genderBox.getSelectionModel().getSelectedItem().equals("Female")) {
					statCondition = " m.gender Like 'female'";
				} else if (genderBox.getSelectionModel().getSelectedItem().equals("Male")) {
					statCondition = " m.gender Like 'male'";
				}
				query += (conditionAdded ? " AND" : "") + statCondition;
				conditionAdded = true;
			}
			if (subBox.getSelectionModel().getSelectedItem() != null
					&& !subBox.getSelectionModel().getSelectedItem().equals("Subscription")) {
				String subCondition = "";
				if (subBox.getSelectionModel().getSelectedItem().equals("Today")) {
					subCondition = " DATE(m.subscription_date) = DATE(NOW())";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("Yesterday")) {
					subCondition = " DATE(m.subscription_date) = DATE_SUB(NOW(), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("This month")) {
					subCondition = " DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 1 MONTH) AND DATE(m.subscription_date) < DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("This year")) {
					subCondition = " DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 1 YEAR) AND DATE(m.subscription_date) < DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("Last year")) {
					subCondition = " DATE(m.subscription_date) < DATE_SUB(NOW(), INTERVAL 1 YEAR) AND DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 2 YEAR)";
				}
				query += (conditionAdded ? " AND" : "") + subCondition;
				conditionAdded = true;
			}
			if (saleBox.getSelectionModel().getSelectedItem() != null
					&& !saleBox.getSelectionModel().getSelectedItem().equals("Sale")) {
				String saleCondition = "";
				if (saleBox.getSelectionModel().getSelectedItem().equals("Under 10")) {
					saleCondition = " COUNT(sale.saleID) < 10";
				} else if (saleBox.getSelectionModel().getSelectedItem().equals("Over 10")) {
					saleCondition = " COUNT(sale.saleID) >= 10";
				} else if (saleBox.getSelectionModel().getSelectedItem().equals("0")) {
					saleCondition = " COUNT(sale.saleID) = 0";
				}
				query += (conditionAdded ? " AND" : "") + saleCondition;
				conditionAdded = true;
			}
		} else {
			query = "SELECT" + " m.memberID, m.subscription_date AS csubscription,"
					+ "  IF(CURDATE() BETWEEN mm.fromDate AND mm.toDate, 'Active', 'Inactive') AS cstat,"
					+ "  GROUP_CONCAT(DISTINCT s.Name ORDER BY s.Name SEPARATOR ', ') AS csport2,"
					+ "  COUNT(sale.saleID) AS csale," + "  m.phone AS cphone,"
					+ "  CONCAT(m.fName, ' ', m.lName) AS cmember2," + "  m.identity AS cidentity,"
					+ "  m.gender AS cgender," + "  m.birthday AS cbirthday" + " FROM" + "  Member m"
					+ "  LEFT JOIN Membership mm ON m.memberID = mm.memberID"
					+ "  LEFT JOIN Sale sale ON m.memberID = sale.memberID"
					+ "  LEFT JOIN member_sport ms ON m.memberID = ms.memberID"
					+ "  LEFT JOIN Sport s ON ms.sportID = s.sportID" + " WHERE CONCAT(m.fName, ' ', m.lName) LIKE ?";

			if (statBox.getSelectionModel().getSelectedItem() != null
					&& !statBox.getSelectionModel().getSelectedItem().equals("Stat")) {
				String statCondition = "";
				if (statBox.getSelectionModel().getSelectedItem().equals("Active")) {
					statCondition = " AND CURDATE() BETWEEN mm.fromDate AND mm.toDate";
				} else if (statBox.getSelectionModel().getSelectedItem().equals("Inactive")) {
					statCondition = " AND NOT (CURDATE() BETWEEN mm.fromDate AND mm.toDate)";
				}
				query += statCondition;
			}
			query += " GROUP BY m.memberID";
			Boolean conditionAdded = false;
			if ((ageBox.getSelectionModel().getSelectedItem() != null
					&& !ageBox.getSelectionModel().getSelectedItem().equals("Age"))
					|| (genderBox.getSelectionModel().getSelectedItem() != null
							&& !genderBox.getSelectionModel().getSelectedItem().equals("Gender"))
					|| (subBox.getSelectionModel().getSelectedItem() != null
							&& !subBox.getSelectionModel().getSelectedItem().equals("Subscription"))
					|| (saleBox.getSelectionModel().getSelectedItem() != null
							&& !saleBox.getSelectionModel().getSelectedItem().equals("Sale"))) {
				query += " HAVING";
			}
			if (ageBox.getSelectionModel().getSelectedItem() != null
					&& !ageBox.getSelectionModel().getSelectedItem().equals("Age")) {
				String ageCondition = "";
				if (ageBox.getSelectionModel().getSelectedItem().equals("Under 18")) {
					ageCondition = " m.birthday > DATE_SUB(CURDATE(), INTERVAL 18 YEAR)";
				} else if (ageBox.getSelectionModel().getSelectedItem().equals("Over 18")) {
					ageCondition = " m.birthday <= DATE_SUB(CURDATE(), INTERVAL 18 YEAR)";
				}
				query += (conditionAdded ? " AND" : "") + ageCondition;
				conditionAdded = true;
			}
			if (genderBox.getSelectionModel().getSelectedItem() != null
					&& !genderBox.getSelectionModel().getSelectedItem().equals("Gender")) {
				String statCondition = "";
				if (genderBox.getSelectionModel().getSelectedItem().equals("Female")) {
					statCondition = " m.gender Like 'female'";
				} else if (genderBox.getSelectionModel().getSelectedItem().equals("Male")) {
					statCondition = " m.gender Like 'male'";
				}
				query += (conditionAdded ? " AND" : "") + statCondition;
				conditionAdded = true;
			}
			if (subBox.getSelectionModel().getSelectedItem() != null
					&& !subBox.getSelectionModel().getSelectedItem().equals("Subscription")) {
				String subCondition = "";
				if (subBox.getSelectionModel().getSelectedItem().equals("Today")) {
					subCondition = " DATE(m.subscription_date) = DATE(NOW())";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("Yesterday")) {
					subCondition = " DATE(m.subscription_date) = DATE_SUB(NOW(), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("This month")) {
					subCondition = " DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 1 MONTH) AND DATE(m.subscription_date) < DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("This year")) {
					subCondition = " DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 1 YEAR) AND DATE(m.subscription_date) < DATE_ADD(LAST_DAY(NOW()), INTERVAL 1 DAY)";
				} else if (subBox.getSelectionModel().getSelectedItem().equals("Last year")) {
					subCondition = " DATE(m.subscription_date) < DATE_SUB(NOW(), INTERVAL 1 YEAR) AND DATE(m.subscription_date) >= DATE_SUB(NOW(), INTERVAL 2 YEAR)";
				}
				query += (conditionAdded ? " AND" : "") + subCondition;
				conditionAdded = true;
			}
			if (saleBox.getSelectionModel().getSelectedItem() != null
					&& !saleBox.getSelectionModel().getSelectedItem().equals("Sale")) {
				String saleCondition = "";
				if (saleBox.getSelectionModel().getSelectedItem().equals("Under 10")) {
					saleCondition = " COUNT(sale.saleID) < 10";
				} else if (saleBox.getSelectionModel().getSelectedItem().equals("Over 10")) {
					saleCondition = " COUNT(sale.saleID) >= 10";
				} else if (saleBox.getSelectionModel().getSelectedItem().equals("0")) {
					saleCondition = " COUNT(sale.saleID) = 0";
				}
				query += (conditionAdded ? " AND" : "") + saleCondition;
				conditionAdded = true;
			}
		}

		try (Connection connection = getConnectionDB()) {

			PreparedStatement statement = connection.prepareStatement(query);
			if (!search2.getText().isEmpty()) {
				String text = search2.getText().toLowerCase();
				statement.setString(1, "%" + text + "%");
			}
			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				Member member = new Member();
				
				member.setMemberID(resultSet.getInt("m.memberID"));
				member.setSubscription(resultSet.getDate("csubscription"));
				member.setStat(resultSet.getString("cstat"));
				member.setSport(resultSet.getString("csport2"));
				member.setSale(resultSet.getInt("csale"));
				member.setPhone(resultSet.getString("cphone"));
				member.setFullname(resultSet.getString("cmember2"));
				member.setIdentity(resultSet.getString("cidentity"));
				member.setGender(resultSet.getString("cgender"));
				member.setBirthday(resultSet.getDate("cbirthday").toLocalDate());
				member.setAge();

				memberTab.getItems().add(member);
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

    private void showConfirmationDialog(Member member) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Confirmation Delete");
        alert.setContentText("Are you sure you want to delete this user?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            deleteMemberFromDatabase(member);
        }
    }

    private void deleteMemberFromDatabase(Member member) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM Member WHERE memberID = ?")) {

            preparedStatement.setInt(1, member.getMemberID());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member deleted successfully.");
                // Refresh the table after deletion
                memberTableView();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete member.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }

    // Inside AdherentController
    void updateMemberInDatabase(Member member) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement("UPDATE Member SET lName=?, fName=?, phone=?, gender=?, birthday=?, identity=?, subscription_date=?, note=? ,email=? WHERE memberID=?")) {

            preparedStatement.setString(1, member.getLname());
            preparedStatement.setString(2, member.getFname());
            preparedStatement.setString(3, member.getPhone());
            preparedStatement.setString(4, member.getGender());

            preparedStatement.setDate(5, java.sql.Date.valueOf(member.getBirthday()));
            preparedStatement.setString(6, member.getIdentity());
            preparedStatement.setTimestamp(7, member.getSubscription_date());
            preparedStatement.setString(8, member.getDescription());
            preparedStatement.setString(9, member.getEmail());

            preparedStatement.setInt(10, member.getMemberID());

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member updated successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update member.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
        }
    }


    // Setter for main controller
    public void setMainController(MainController mainController) {
        // Implementation remains the same
    }
    private Member getMemberByID(int memberID) {
        Member member = null;
        String query = "SELECT * FROM Member WHERE memberID = ?";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setLname(resultSet.getString("lName"));
                member.setFname(resultSet.getString("fName"));
                member.setPhone(resultSet.getString("phone"));
                member.setGender(resultSet.getString("gender"));
                member.setBirthday(resultSet.getDate("birthday").toLocalDate());
                member.setIdentity(resultSet.getString("identity"));
                member.setSubscription_date(resultSet.getTimestamp("subscription_date"));
                member.setDescription(resultSet.getString("note"));
                member.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error fetching member from database.");
        }

        return member;
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
}
