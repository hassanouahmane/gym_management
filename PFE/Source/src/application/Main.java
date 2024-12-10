package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;

public class Main extends Application {
    private static Main instance;
    private ConfigurationHandler configHandler = new ConfigurationHandler();
    private Configuration configuration;
    private User user = new User();
    private String db_url = "jdbc:mysql://localhost:3306/fitpro";
    private String db_usr = "root";
    private String db_pwd = "";

    @Override
    public void start(Stage primaryStage) {
        user.setFName("Mohamed");
        user.setLName("Razi");
        user.setUserID(1);
        user.setAuthority('a');
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/main.fxml"));
            Parent root = loader.load();

            // Pass the configuration to the controller
            MainController mainController = loader.getController();
            mainController.setConfiguration(configuration);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("FitPRO - Login");
            primaryStage.show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Main getInstance() {
        return instance;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void init() throws Exception {
        super.init();
        instance = this;
        loadConfiguration();
    }

    private void loadConfiguration() throws IOException {
        configuration = configHandler.loadConfiguration();
        if (configuration == null) {
            System.out.println("Creating default configuration...");
            configuration = new Configuration(); 
            configuration.setCurrency("MAD");
            configuration.setNotificationsEnabled(true);
            configHandler.saveConfiguration(configuration);
        }
    }


    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public String getDb_usr() {
        return db_usr;
    }

    public void setDb_usr(String db_usr) {
        this.db_usr = db_usr;
    }

    public String getDb_pwd() {
        return db_pwd;
    }

    public void setDb_pwd(String db_pwd) {
        this.db_pwd = db_pwd;
    }

	public User getUser() {
		return this.user;
	}
}
