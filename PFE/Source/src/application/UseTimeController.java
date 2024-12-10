package application;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class UseTimeController {

	@FXML
	private Button addBtn;

	@FXML
	private TableColumn<Schedule, String> ccoach;

	@FXML
	private TableColumn<Schedule, String> cday;

	@FXML
	private TableColumn<Schedule, LocalTime> cend;

	@FXML
	private TableColumn<Schedule, String> cnote;

	@FXML
	private TableColumn<Schedule, Integer> cscheduleId;

	@FXML
	private TableColumn<Schedule, LocalTime> cstart;

	@FXML
	private TableColumn<Schedule, String> csalle;

	@FXML
	private TableView<Schedule> scheduleTab;

	private Schedule selectedSchedule;

	public Sport sport;

	private static UseTimeController instance;
	
	public List<Schedule> schedules = new ArrayList<>();

	public void initialize() {
		instance = this;
		System.out.println(sport);
		if (sport != null) {
			initializeScheduleTab();
			schedules = scheduleTableView();
			addSchedulesToTableView(schedules);
			setupTableView();
		} else
			System.out.println("Empty sport");
		
	}

	public void initializeScheduleTab() {
		ccoach.setCellValueFactory(new PropertyValueFactory<>("coachName"));
		cscheduleId.setCellValueFactory(new PropertyValueFactory<>("scheduleID"));
		cday.setCellValueFactory(new PropertyValueFactory<>("dayOfWeek"));
		cend.setCellValueFactory(new PropertyValueFactory<>("endTime"));
		cstart.setCellValueFactory(new PropertyValueFactory<>("startTime"));
		cnote.setCellValueFactory(new PropertyValueFactory<>("note"));
		csalle.setCellValueFactory(new PropertyValueFactory<>("salle"));

		Map<String, String> dayOfWeekColors = new HashMap<>();
		dayOfWeekColors.put("Monday", "-fx-background-color: #e6f2ff;");
		dayOfWeekColors.put("Tuesday", "-fx-background-color: #ffeecc;");
		dayOfWeekColors.put("Wednesday", "-fx-background-color: #ffddcc;");
		dayOfWeekColors.put("Thursday", "-fx-background-color: #ccf2ff;");
		dayOfWeekColors.put("Friday", "-fx-background-color: #e6f2ee;");
		dayOfWeekColors.put("Saturday", "-fx-background-color: #fce6cc;");
		dayOfWeekColors.put("Sunday", "-fx-background-color: #ffffb3;");

		Callback<TableColumn<Schedule, String>, TableCell<Schedule, String>> cellFactory = new Callback<TableColumn<Schedule, String>, TableCell<Schedule, String>>() {
			@Override
			public TableCell<Schedule, String> call(TableColumn<Schedule, String> param) {
				TableCell<Schedule, String> cell = new TableCell<Schedule, String>() {
					@Override
					public void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
							setStyle("");
						} else {
							Schedule schedule = getTableView().getItems().get(getIndex());
							String dayOfWeek = schedule.getDayOfWeek();
							String color = dayOfWeekColors.get(dayOfWeek);
							setText(item);
							setStyle(color);
						}
					}
				};
				return cell;
			}
		};

		cday.setCellFactory(cellFactory);
		ccoach.setCellFactory(cellFactory);
		csalle.setCellFactory(cellFactory);
		cnote.setCellFactory(cellFactory);
		cscheduleId.setCellFactory(new Callback<TableColumn<Schedule, Integer>, TableCell<Schedule, Integer>>() {
			@Override
			public TableCell<Schedule, Integer> call(TableColumn<Schedule, Integer> param) {
				return new TableCell<Schedule, Integer>() {
					@Override
					protected void updateItem(Integer item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
							setStyle("");
						} else {
							Schedule schedule = getTableView().getItems().get(getIndex());
							String dayOfWeek = schedule.getDayOfWeek();
							String color = dayOfWeekColors.get(dayOfWeek);
							setText(String.valueOf(item));
							setStyle(color);
						}
					}
				};
			}
		});
		cstart.setCellFactory(new Callback<TableColumn<Schedule, LocalTime>, TableCell<Schedule, LocalTime>>() {
			@Override
			public TableCell<Schedule, LocalTime> call(TableColumn<Schedule, LocalTime> param) {
				return new TableCell<Schedule, LocalTime>() {
					@Override
					protected void updateItem(LocalTime item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
							setStyle("");
						} else {
							Schedule schedule = getTableView().getItems().get(getIndex());
							String dayOfWeek = schedule.getDayOfWeek();
							String color = dayOfWeekColors.get(dayOfWeek);
							setText(item.toString());
							setStyle(color);
						}
					}
				};
			}
		});
		cend.setCellFactory(new Callback<TableColumn<Schedule, LocalTime>, TableCell<Schedule, LocalTime>>() {
			@Override
			public TableCell<Schedule, LocalTime> call(TableColumn<Schedule, LocalTime> param) {
				return new TableCell<Schedule, LocalTime>() {
					@Override
					protected void updateItem(LocalTime item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setText(null);
							setStyle("");
						} else {
							Schedule schedule = getTableView().getItems().get(getIndex());
							String dayOfWeek = schedule.getDayOfWeek();
							String color = dayOfWeekColors.get(dayOfWeek);
							setText(item.toString());
							setStyle(color);
						}
					}
				};
			}
		});
	}

	public List<Schedule> scheduleTableView() {
		String query = "SELECT s.scheduleID, sp.sportID, sp.Name, c.coachID, CONCAT(c.fName, ' ', c.lName) AS coach, s.startTime, s.endTime, s.dayOfWeek, s.salle, s.note "
	            + "FROM Schedule s JOIN Sport sp ON s.sportID = sp.sportID JOIN Coach c ON s.coachID = c.coachID WHERE sp.sportID = ? ORDER BY s.dayOfWeek, s.startTime";

	    try (Connection connection = getConnectionDB();
	         PreparedStatement statement = connection.prepareStatement(query)) {

	        statement.setInt(1, sport.getSportID());
	        ResultSet result = statement.executeQuery();

	        while (result.next()) {
	            Schedule schedule = new Schedule();
	            schedule.setScheduleID(result.getInt("scheduleID"));
	            schedule.setSportID(result.getInt("sportID"));
	            schedule.setSportName(result.getString("Name"));
	            schedule.setCoachID(result.getInt("coachID"));
	            schedule.setCoachName(result.getString("coach"));
	            schedule.setStartTime(result.getTime("startTime").toLocalTime());
	            schedule.setEndTime(result.getTime("endTime").toLocalTime());
	            schedule.setSalle(result.getString("salle"));
	            int dayOfWeekInt = result.getInt("dayOfWeek");
	            DayOfWeek dayOfWeek = DayOfWeek.of(dayOfWeekInt);
	            schedule.setDayOfWeek(dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
	            schedule.setNote(result.getString("note"));
	            schedules.add(schedule);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return schedules;
	}

	public void addSchedulesToTableView(List<Schedule> schedules) {
	    scheduleTab.getItems().clear();
	    scheduleTab.getItems().addAll(schedules);
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
	void addSchedule(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formUseTime.fxml"));
			Parent root = loader.load();

			Scene scene = new Scene(root);

			Stage stage = new Stage();
			stage.setTitle("Ajout de coach");
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);

			stage.showAndWait();

			scheduleTableView();
		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Error loading Add schedule view.");
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
		contextMenu.getItems().addAll(editMenuItem, deleteMenuItem);

		scheduleTab.setContextMenu(contextMenu);

		editMenuItem.setOnAction(event -> {
			selectedSchedule = scheduleTab.getSelectionModel().getSelectedItem();
			if (selectedSchedule != null) {
				openEditSchedulehWindow(selectedSchedule);
			} else {
				showAlert(Alert.AlertType.WARNING, "Warning", "No schedule selected.");
			}
		});

		deleteMenuItem.setOnAction(event -> {
			selectedSchedule = scheduleTab.getSelectionModel().getSelectedItem();
			if (selectedSchedule != null) {
				showConfirmationDialog(selectedSchedule);
			} else {
				showAlert(Alert.AlertType.WARNING, "Warning", "No schedule selected.");
			}
		});
	}

	private void openEditSchedulehWindow(Schedule selectedSchedule) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/formUseTime.fxml"));
			Parent root = loader.load();

			UseTimeFormController formController = loader.getController();
			formController.initData(selectedSchedule);

			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.setTitle("Modification de schedule");
			stage.setScene(scene);
			stage.initModality(Modality.APPLICATION_MODAL);
			stage.showAndWait();

			scheduleTableView();

		} catch (IOException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Error loading Edit Schedule view.");
		}
	}

	private void showConfirmationDialog(Schedule schedule) {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Confirmation Delete");
		alert.setContentText("Are you sure you want to delete this schedule?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			deleteScheduleFromDatabase(schedule);
		}
	}

	private void deleteScheduleFromDatabase(Schedule schedule) {
		if (schedule == null) {
			showAlert(Alert.AlertType.ERROR, "Error", "Invalid schedule information.");
			return;
		}

		Integer scheduleID = schedule.getScheduleID();
		try (Connection connection = getConnectionDB();
				PreparedStatement preparedStatement = connection
						.prepareStatement("DELETE FROM Schedule WHERE scheduleID = ?")) {

			preparedStatement.setInt(1, scheduleID);

			int affectedRows = preparedStatement.executeUpdate();
			if (affectedRows > 0) {
				showAlert(Alert.AlertType.INFORMATION, "Success", "Schedule deleted successfully.");
				scheduleTableView();
			} else {
				showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete schedule.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Error", "Database error occurred: " + e.getMessage());
		}
	}

	public void setSport(Sport sp) {
		this.sport = sp;
	}

	public static UseTimeController getInstance() {
		return instance;
	}

	

}
