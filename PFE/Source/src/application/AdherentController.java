package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdherentController {

    @FXML
    private TableView<Member> tableView;

    @FXML
    private TableColumn<Member, Integer> memberIdColumn;

    @FXML
    private TableColumn<Member, String> fnameColumn;

    @FXML
    private TableColumn<Member, String> lnameColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, String> genderColumn;

    @FXML
    private TableColumn<Member, LocalDate> birthdayColumn;

    @FXML
    private TableColumn<Member, String> identityColumn;

    @FXML
    private TableColumn<Member, Timestamp> subscriptionDateColumn;

    @FXML
    private TableColumn<Member, String> descriptionColumn;
    @FXML
    private TableColumn<Member, String> emailColumn;


    private MainController mainController=MainController.getInstance(); 

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

            tableView.getItems().clear();
            loadMembers();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Add User view.");
        }
    }
    @FXML
    void exportToCSV(ActionEvent event) {
        List<Member> members = tableView.getItems();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        java.io.File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) { 
                // En-têtes CSV 
                writer.append("MemberID,LastName,FirstName,Phone,Gender,Birthday,Identity,SubscriptionDate,Note,Email\n");

                // Insertion des données des membres
                for (Member member : members) {
                    // Styler les données avec des guillemets et des virgules
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

    // Méthode pour échapper les caractères spéciaux et entourer la valeur par des guillemets
    private String escapeAndQuote(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }




    @FXML
    void initialize() {
        setupTableView();
        loadMembers();
    }

    private void setupTableView() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem profilMenuItem = new MenuItem("Voir profil");
     
        MenuItem adherenceMenuItem = new MenuItem("Payer adherence");

        contextMenu.getItems().addAll(profilMenuItem, editMenuItem, deleteMenuItem, adherenceMenuItem);

        tableView.setContextMenu(contextMenu);

        editMenuItem.setOnAction(event -> {
            Member selectedMember = tableView.getSelectionModel().getSelectedItem();
            if (selectedMember != null) {
                openEditUserWindow(selectedMember);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No user selected.");
            }
        });

        deleteMenuItem.setOnAction(event -> {
            Member selectedMember = tableView.getSelectionModel().getSelectedItem();
            if (selectedMember != null) {
                showConfirmationDialog(selectedMember);
            } else {
                showAlert(Alert.AlertType.WARNING, "Warning", "No user selected.");
            }
        });




        // Payer adh�rence menu item clicked
        adherenceMenuItem.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/membership.fxml"));
                Parent root = loader.load();

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
        // profile item 
        profilMenuItem.setOnAction(event -> {
            try {
                // Get the selected item from the table view
                Member selectedMember = this.tableView.getSelectionModel().getSelectedItem();
                
                // Get the scene from the event source (MenuItem)
                Scene scene = ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow().getScene();
                
                // Get the root node from the scene
                Node previousView = scene.getRoot();
                
                // Call switchToProfil method with the selected member and previous view
                mainController.switchToProfil(selectedMember, previousView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        memberIdColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getMemberID()).asObject());
        lnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLname()));
        fnameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFname()));
        phoneColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhone()));
        genderColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGender()));

        birthdayColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getBirthday()));
        identityColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIdentity()));
        subscriptionDateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSubscription_date()));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        emailColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

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

            // Refresh the table after editing
            tableView.getItems().clear();
            loadMembers();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit User view.");
        }
    }

    private void loadMembers() {
        List<Member> members = getAllMembers();
        tableView.getItems().addAll(members);
    }

    private List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM Member";

        try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Member member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setLname(resultSet.getString("lName"));
                member.setFname(resultSet.getString("fName"));
                member.setPhone(resultSet.getString("phone"));
                
                member.setGender(resultSet.getString("gender"));
                member.setBirthday(resultSet.getDate("birthday").toLocalDate());
                member.setIdentity(resultSet.getString("identity"));
                member.setSubscription_date(resultSet.getTimestamp("subscription_date"));
                member.setDescription(resultSet.getString("note"));
                member.setEmail(resultSet.getString("email")); // Ajout de l'email
                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error fetching members from database.");
        }

        return members;
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
                tableView.getItems().clear();
                loadMembers();
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
        this.mainController = mainController;
    }
}
