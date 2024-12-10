package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.TabPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;

public class GymController {
	private User user=Main.getInstance().getUser();
	private Main main=Main.getInstance();

    @FXML
    private TabPane tabpane;
     
     
    @FXML
    void new_sport(ActionEvent event) {
    	
 
    }
    
    @FXML
    void initialize()
    {
    	 if (tabpane != null) {
             load_sports(getAllSports());
         } else {
             System.err.println("TabPane 'tabpane' is null. Make sure it's properly initialized in the FXML file.");
         }
    }
    
    
    
    private void load_sports(List<Sport> sports) {
        for (Sport sport : sports) {
            Tab tab = new Tab();
            tab.setText(sport.getName());
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/sport.fxml"));
                Parent root = loader.load();
                
                SportController sportController = loader.getController();
                sportController.setSport(sport); 
                sportController.initialize();
                
                tab.setContent(root);
            } catch (IOException e) {
                e.printStackTrace();
            }
            
            tabpane.getTabs().add(tab); 
        }
    }


    public  List<Sport> getAllSports() {
        List<Sport> sports = new ArrayList<>();
        String query = "SELECT * FROM Sport ";
        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            
            while (resultSet.next()) {
                Sport sport = new Sport();
                sport.setSportID(resultSet.getInt("sportID"));
                sport.setName(resultSet.getString("Name"));
                
                sports.add(sport);
                System.out.println(sport);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Error adding membership. Please check your input and try again.");
        }

        return sports;
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
