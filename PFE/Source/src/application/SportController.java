package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;
import javafx.scene.control.Alert.AlertType;

public class SportController {
    Main main = Main.getInstance();
    private Sport sport;
    private List<Plan> plans;

    @FXML
    private UseTimeController useTimeController;

    @FXML
    private TextField plan_name1; 

    @FXML
    private TextField plan_duration1;

    @FXML
    private TextField plan_price1;

    @FXML
    private TextField plan_name2;

    @FXML
    private TextField plan_duration2;

    @FXML
    private TextField plan_price2;

    @FXML
    private TextField plan_name3;

    @FXML
    private TextField plan_duration3;

    @FXML
    private TextField plan_price3;
    
    @FXML
    private VBox schedule_container;

    public void initialize() {
        if (sport != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/useTime.fxml"));
            try {
                Parent root = loader.load();
                UseTimeController useTimeController = loader.getController();
                useTimeController.setSport(sport);
                useTimeController.initialize();
                schedule_container.getChildren().add(root); // Add the imported usetime to the VBox
                this.plans = getAllPlans();
                showPlans(this.plans); 
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("SportController: Sport object is null.");
        }
    }

    public void showPlans(List<Plan> plans) {
    	if(plans==null)
    		return;
        plan_name1.setText(plans.get(0).getName());
        plan_duration1.setText("" + plans.get(0).getDuration());
        plan_price1.setText("" + plans.get(0).getPrice());

        plan_name2.setText(plans.get(1).getName());
        plan_duration2.setText("" + plans.get(1).getDuration());
        plan_price2.setText("" + plans.get(1).getPrice());

        plan_name3.setText(plans.get(2).getName());
        plan_duration3.setText("" + plans.get(2).getDuration());
        plan_price3.setText("" + plans.get(2).getPrice());

    }

    public List<Plan> getAllPlans() {
    
        List<Plan> plans = new ArrayList<Plan>();

        String query = "SELECT * FROM plans WHERE sportID = ?";

        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(),
                main.getDb_pwd());
                PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, sport.getSportID());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Plan plan = new Plan();
                plan.setPlanID(resultSet.getInt("planID"));
                plan.setSportID(resultSet.getInt("sportID"));
                plan.setName(resultSet.getString("Name"));
                plan.setPrice(resultSet.getDouble("price"));
                plan.setDuration(resultSet.getInt("duration"));
                plans.add(plan);

                System.out.println(plan);
      
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return plans;
    }

    @FXML
    void save_plan1(ActionEvent event) {
        savePlan(this.plans.get(0).getPlanID(), plan_name1.getText(), Integer.parseInt(plan_duration1.getText()),
                Double.parseDouble(plan_price1.getText()));
    }

    @FXML
    void save_plan2(ActionEvent event) {
        savePlan(this.plans.get(1).getPlanID(), plan_name2.getText(), Integer.parseInt(plan_duration2.getText()),
                Double.parseDouble(plan_price2.getText()));
    }

    @FXML
    void save_plan3(ActionEvent event) {
        savePlan(this.plans.get(2).getPlanID(), plan_name3.getText(), Integer.parseInt(plan_duration3.getText()),
                Double.parseDouble(plan_price3.getText()));
    }

    private void savePlan(int planNumber, String name, int duration, double price) {
        String updateQuery = "UPDATE plans SET Name = ?, duration = ?, price = ? WHERE sportID = ? AND planID = ?";
        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(),
                main.getDb_pwd());
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, duration); 
            preparedStatement.setDouble(3, price);
            preparedStatement.setInt(4, sport.getSportID());
            preparedStatement.setInt(5, planNumber);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Plan " + planNumber + " updated successfully.");
            } else {
                System.out.println("No plan updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setSport(Sport sp) {
    	this.sport=sp;
    }
}
