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

public class UseTimeFormController implements Initializable {

	@FXML
	private Button clean;
	
	@FXML
	private Button save;

	@FXML
	private ComboBox<String> coach;

	@FXML
	private ComboBox<String> day;

	@FXML
	private TextField end;

	@FXML
	private TextArea note;

	@FXML
	private TextField salle;

	@FXML
	private ComboBox<String> sport;

	@FXML
	private TextField start;

	private Map<Integer, String> sportsMap;

	private Map<Integer, String> daysMap;

	private Map<Integer, String> coachesMap;
	
	private Schedule selectedSchedule;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		end.setFocusTraversable(false);
		start.setFocusTraversable(false);
		note.setFocusTraversable(false);
		salle.setFocusTraversable(false);
		retrieveSportsFromDatabase();
		retrieveCoachesFromDatabase();
		Days();
	}

	private void retrieveSportsFromDatabase() {
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT sportID, Name FROM Sport");
				ResultSet resultSet = preparedStatement.executeQuery()) {

			sportsMap = new HashMap<>();
			sportsMap.put(0, "Select sport");

			while (resultSet.next()) {
				int sportID = resultSet.getInt("sportID");
				String sportName = resultSet.getString("Name");
				sportsMap.put(sportID, sportName);
			}

			sport.getItems().addAll(sportsMap.values());
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve sports from the database.");
		}
	}
	
	private void retrieveCoachesFromDatabase() {
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection.prepareStatement("SELECT coachID, CONCAT(fName,' ',lName) as coach FROM Coach");
				ResultSet resultSet = preparedStatement.executeQuery()) {

			coachesMap = new HashMap<>();
			coachesMap.put(0, "Select coach");

			while (resultSet.next()) {
				int sportID = resultSet.getInt("coachID");
				String sportName = resultSet.getString("coach");
				coachesMap.put(sportID, sportName);
			}

			coach.getItems().addAll(coachesMap.values());
		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve sports from the database.");
		}
	}

	private void Days() {

		daysMap = new HashMap<>();
		daysMap.put(0, "Select day");

		daysMap.put(1, "Monday");
		daysMap.put(2, "Tuesday");
		daysMap.put(3, "Wednesday");
		daysMap.put(4, "Thursday");
		daysMap.put(5, "Friday");
		daysMap.put(6, "Saturday");
		daysMap.put(7, "Sunday");

		day.getItems().addAll(daysMap.values());

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

	    String selectedSportName = sport.getValue();
	    String selectedCoachName = coach.getValue();
	    String selectedDay = day.getValue();
	    String startTimeStr = start.getText();
	    String endTimeStr = end.getText();
	    String salleStr = salle.getText();
	    String noteStr = note.getText();

	    try {
	        int selectedSportId = getSportIdFromName(selectedSportName); // Retrieve sport ID
	        int selectedCoachId = getCoachIdFromName(selectedCoachName); // Retrieve coach ID
	        int selectedDayIndex = getDayIndexByName(selectedDay); // Retrieve day index
	        LocalTime startTime = LocalTime.parse(startTimeStr);
	        LocalTime endTime = LocalTime.parse(endTimeStr);

	        if (selectedSportId == -1) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Selected sport not found.");
	            return;
	        }

	        if (selectedCoachId == -1) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Selected coach not found.");
	            return;
	        }
	        
	        if (selectedDayIndex == -1) {
	            showAlert(Alert.AlertType.ERROR, "Error", "Selected day not found.");
	            return;
	        }

	        Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/fitpro", "root", "");
	        PreparedStatement preparedStatement;

	        if (selectedSchedule == null) {
	            // Insert operation
	            preparedStatement = connection.prepareStatement(
	                     "INSERT INTO Schedule (sportID, coachID, startTime, endTime, dayOfWeek, salle, note) VALUES (?, ?, ?, ?, ?, ?, ?)");
	        } else {
	            // Edit operation
	            preparedStatement = connection.prepareStatement(
	                     "UPDATE Schedule SET sportID = ?, coachID = ?, startTime = ?, endTime = ?, dayOfWeek = ?, salle = ?, note = ? WHERE scheduleID = ?");
	            preparedStatement.setInt(8, selectedSchedule.getScheduleID());
	        }

	        preparedStatement.setInt(1, selectedSportId);
	        preparedStatement.setInt(2, selectedCoachId);
	        preparedStatement.setTime(3, Time.valueOf(startTime));
	        preparedStatement.setTime(4, Time.valueOf(endTime));
	        preparedStatement.setInt(5, selectedDayIndex);
	        preparedStatement.setString(6, salleStr);
	        preparedStatement.setString(7, noteStr);

	        int rowsAffected = preparedStatement.executeUpdate();
	        if (rowsAffected > 0) {
	            showAlert(Alert.AlertType.INFORMATION, "Success", "Schedule data saved successfully.");
	        } else {
	            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save schedule data.");
	        }

	        connection.close(); // Close connection
	    } catch (SQLException e) {
	        e.printStackTrace();
	        showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred.");
	    }
	}

	
	private int getSportIdFromName(String sportName) {
	    for (Map.Entry<Integer, String> entry : sportsMap.entrySet()) {
	        if (entry.getValue().equals(sportName)) {
	            return entry.getKey();
	        }
	    }
	    return -1; // If sport ID is not found
	}

	private int getCoachIdFromName(String coachName) {
	    for (Map.Entry<Integer, String> entry : coachesMap.entrySet()) {
	        if (entry.getValue().equals(coachName)) {
	            return entry.getKey();
	        }
	    }
	    return -1; // If coach ID is not found
	}

	private int getDayIndexByName(String dayName) {
	    for (Map.Entry<Integer, String> entry : daysMap.entrySet()) {
	        if (entry.getValue().equals(dayName)) {
	            return entry.getKey();
	        }
	    }
	    return -1; // If day index is not found
	}

	
	private boolean validateInputs() {
	    StringBuilder errorMessage = new StringBuilder();

	    // Validate coach selection
	    if (coach.getValue() == null || coach.getValue().isEmpty() || coach.getValue().equals("Select coach")) {
	        errorMessage.append("Please select a coach.\n");
	    }

	    // Validate day selection
	    if (day.getValue() == null || day.getValue().isEmpty() || day.getValue().equals("Select day")) {
	        errorMessage.append("Please select a day.\n");
	    }

	    // Validate start time
	    if (start.getText().isEmpty() || !start.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
	        errorMessage.append("Start time is required and must be in HH:mm format.\n");
	    }

	    // Validate end time
	    if (end.getText().isEmpty() || !end.getText().matches("([01]?[0-9]|2[0-3]):[0-5][0-9]")) {
	        errorMessage.append("End time is required and must be in HH:mm format.\n");
	    }

	    // Validate sport selection
	    if (sport.getValue() == null || sport.getValue().isEmpty() || sport.getValue().equals("Select sport")) {
	        errorMessage.append("Please select a sport.\n");
	    }

	    // Validate salle
	    if (salle.getText().isEmpty()) {
	        errorMessage.append("Salle is required.\n");
	    }

	    // Validate note
	    // No specific validation for note field. Assuming it's optional.

	    if (!errorMessage.toString().isEmpty()) {
	        showAlert(Alert.AlertType.ERROR, "Error", errorMessage.toString());
	        return false;
	    }

	    return true;
	}
	
	public void initData(Schedule schedule) {
		selectedSchedule = schedule;
	    sport.getSelectionModel().select(schedule.getSportName());
	    coach.getSelectionModel().select(schedule.getCoachName());
	    day.getSelectionModel().select(schedule.getDayOfWeek());
	    start.setText(schedule.getStartTime().toString());
	    end.setText(schedule.getEndTime().toString());
	    salle.setText(schedule.getSalle());
	    note.setText(schedule.getNote());
	}

	@FXML
	void clean(ActionEvent event) {
		start.clear();
		end.clear();
		salle.clear();
		note.clear();
		sport.getSelectionModel().clearSelection();
		sport.getSelectionModel().selectFirst();
		coach.getSelectionModel().clearSelection();
		coach.getSelectionModel().selectFirst();
		day.getSelectionModel().clearSelection();
		day.getSelectionModel().selectFirst();
	}
}