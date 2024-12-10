package application;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SettingsController {

    private Main main = Main.getInstance();
    private User user = main.getUser();
    private ConfigurationHandler handler=new ConfigurationHandler();
    private Configuration configuration = main.getConfiguration();

    @FXML
    private ComboBox<String> currency;

    @FXML
    private Button save_currency_btn;

    @FXML
    private Button dele_user_btn;

    @FXML
    private Button exportdb_btn;

    @FXML
    private Button recover_btn;

    @FXML
    private TableView<User> users_table;
    
    @FXML
    private ContextMenu contextMenu;

    @FXML
    private TableColumn<User, String> fullname_column; 

    @FXML
    private TableColumn<User, String> username_column;
    
    @FXML
    private TableColumn<User, String> password_column; 

    @FXML
    private TableColumn<User, Character> authority_column; 
    
    @FXML
    private RadioButton notifications_off;

    @FXML
    private ToggleGroup notification_bool;

    @FXML
    private RadioButton notifications_on;

    @FXML
    private Button add_user_btn;

    private ObservableList<User> userList = FXCollections.observableArrayList(); // Declare ObservableList for User data

    @FXML
    void initialize() {
        if (user.getAuthority() != 'a') { 
            return;
        }
        

        fullname_column.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        username_column.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
        password_column.setCellValueFactory(cellData -> cellData.getValue().passwordProperty());
        authority_column.setCellValueFactory(cellData -> cellData.getValue().authorityProperty());

        // Load users into the table
        loadConfig();
        loadUsers();
        setupContextMenu();
        
        
        
    }
    
    

    private void loadUsers() {
        userList.clear(); // Clear existing data
        List<User> users = loadUsersFromDatabase();
        userList.addAll(users);
        users_table.setItems(userList);
    }
    
    private void loadConfig()
    {
    	
        
        handler.loadConfiguration();
        this.configuration= handler.getConfiguration();
        
        if(configuration.isNotificationsEnabled())
        	
        	notification_bool.selectToggle(notifications_on);
        else {
        	notification_bool.selectToggle(notifications_off);
        	
			
		} 
    }
    private List<User> loadUsersFromDatabase() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM User";

        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                User user = new User();
                user.setUserID(resultSet.getInt("userID"));
                user.setAuthority(resultSet.getString("authority").charAt(0));
                user.setFName(resultSet.getString("FName"));
                user.setLName(resultSet.getString("LName"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }


    @FXML
    void addUser(ActionEvent event) {
        try {
            // Load the user form FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/userform.fxml"));
            Parent root = loader.load();

            // Create a new stage
            Stage stage = new Stage();
            stage.setTitle("Add User");
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setScene(new Scene(root));

            // Show the stage
            stage.showAndWait();
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void setupContextMenu() {
        MenuItem deleteItem = new MenuItem("Delete");
        MenuItem modifyItem = new MenuItem("Modify");

        deleteItem.setOnAction(this::deleteUser);
        modifyItem.setOnAction(this::modifyUser);

        contextMenu = new ContextMenu(deleteItem, modifyItem);
        users_table.setContextMenu(contextMenu);
    }
    
    private void deleteUser(ActionEvent event) {
        User selectedUser = users_table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            String query = "DELETE FROM User WHERE UserID=?";
            try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, selectedUser.getUserID());
                preparedStatement.executeUpdate();
                // Refresh table after deletion
                loadUsers();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void modifyUser(ActionEvent event) {
        User selectedUser = users_table.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/userform.fxml"));
                Parent root = loader.load();
                UserFormController controller = loader.getController();
                controller.initData(selectedUser);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.showAndWait();
                loadUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    

    @FXML
    void displayHelp(ActionEvent event) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Settings Help");
		alert.setHeaderText("Help Information for Settings");
		alert.setContentText("This section allows you to configure various aspects of the application.\n\n"
				+ "For assistance or information on specific settings, please refer to the user guide or documentation.");

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);

		alert.showAndWait();
    }

    @FXML
    void exportUserData(ActionEvent event) {
        Stage stage = (Stage) exportdb_btn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
        fileChooser.getExtensionFilters().add(extFilter);
        String fileName = "export_data.sql"; // Change the file name as needed
        fileChooser.setInitialFileName(fileName);

        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                Connection connect = getConnectionDB();

                if (connect != null) {
                    exportTableData(connect, "SELECT * FROM Sport", writer);
                    exportTableData(connect, "SELECT * FROM Member", writer);
                    exportTableData(connect, "SELECT * FROM Coach", writer);
                    exportTableData(connect, "SELECT * FROM Equipement", writer);
                    exportTableData(connect, "SELECT * FROM Sale", writer);
                    exportTableData(connect, "SELECT * FROM Membership", writer);
                    exportTableData(connect, "SELECT * FROM plans", writer);
                    exportTableData(connect, "SELECT * FROM Schedule", writer);

                    connect.close();
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void exportTableData(Connection connection, String query, BufferedWriter writer) throws SQLException, IOException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                writer.write("INSERT INTO " + metaData.getTableName(1) + " (");
                for (int i = 1; i <= columnCount; i++) {
                    writer.write(metaData.getColumnName(i));
                    if (i < columnCount) {
                        writer.write(", ");
                    }
                }
                writer.write(") VALUES (");
                for (int i = 1; i <= columnCount; i++) {
                    Object value = resultSet.getObject(i);
                    if (value != null) {
                        writer.write("'" + value.toString() + "'");
                    } else {
                        writer.write("NULL");
                    }
                    if (i < columnCount) {
                        writer.write(", ");
                    }
                }
                writer.write(");\n");
            }
        }
    }


    @FXML
    void recoverData(ActionEvent event) {
        Stage stage = (Stage) recover_btn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select SQL File to Recover");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("SQL files (*.sql)", "*.sql");
        fileChooser.getExtensionFilters().add(extFilter);

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                 Connection connection = getConnectionDB()) {

                if (connection != null) {
                    StringBuilder queryBuilder = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        queryBuilder.append(line);
                        if (line.endsWith(";")) {
                            String query = queryBuilder.toString();
                            try {
                                executeSQL(connection, query);
                            } catch (SQLIntegrityConstraintViolationException e) {
                                System.err.println("Ignoring duplicate primary key error for query: " + query);
                            }
                            queryBuilder.setLength(0);
                        }
                    }
                }
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeSQL(Connection connection, String query) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(query);
        }
    }


    @FXML
    void turnOffNotifications(ActionEvent event) {
      configuration.setNotificationsEnabled(false);
      handler.saveConfiguration(configuration);
      

    } 

    @FXML
    void turnOnNotifications(ActionEvent event) {
    	configuration.setNotificationsEnabled(true);
    	handler.saveConfiguration(configuration);
    	
    }

    @FXML
    void versionInfo(ActionEvent event) {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Version Information");
		alert.setHeaderText("Application Version");

		String version = "1.0.0";
		alert.setContentText("You are currently using version: " + version);

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);

		alert.showAndWait();
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
    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    @FXML
    void configureDB(ActionEvent event) throws IOException {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formConfigDb.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root);
		Stage stage = new Stage();
		stage.setTitle("Modification of database configuration info");
		stage.setScene(scene);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
    }

}

