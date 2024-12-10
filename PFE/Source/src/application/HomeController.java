package application;

import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HomeController implements Initializable {

	@FXML
	private BarChart<String, Number> barChart;

	@FXML
	private LineChart<String, Number> lineChart;

	@FXML
	private PieChart pieChart;

	@FXML
	private TableView<Sale> salesTab;

	@FXML
	private TableColumn<Sale, String> cequipement;

	@FXML
	private TableColumn<Sale, String> cadherent;

	@FXML
	private TableColumn<Sale, Integer> cquantite;

	@FXML
	private TableColumn<Sale, Double> cprice;

	@FXML
	private TableColumn<Sale, Double> cpayed;

	@FXML
	private CheckBox notPayed;

	@FXML
	private TextField search;

	@FXML
	private FontAwesomeIcon vider;

	@FXML
	private TextField search2;

	@FXML
	private FontAwesomeIcon vider2;

	@FXML
	private Label revenue;

	@FXML
	private Label sales;

	@FXML
	private Label members;

	@FXML
	private Label equipement;

	@FXML
	private Label coachs;

	@FXML
	private Label age;

	@FXML
	private Label female;

	@FXML
	private Label male;

	@FXML
	private TableColumn<Membership, String> cduration;

	@FXML
	private TableColumn<Membership, String> cmember;

	@FXML
	private TableColumn<Membership, Double> cpayedship;

	@FXML
	private TableColumn<Membership, Double> cpriceship;

	@FXML
	private TableColumn<Membership, String> csport;

	@FXML
	private TableColumn<Membership, Date> cfrom;

	@FXML
	private TableColumn<Membership, Date> cto;

	@FXML
	private TableColumn<Membership, String> cFinishedAt;

	@FXML
	private CheckBox finished;

	@FXML
	private TableView<Membership> membershipTab;

	@FXML
	private CheckBox notPayedship; 

	@FXML
	private TextField searchship;

	@FXML
	private FontAwesomeIcon vidership;

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

	@FXML
	private Button reset;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		search.setFocusTraversable(false);
		vider.setVisible(false);

		search2.setFocusTraversable(false);
		vider2.setVisible(false);

		searchship.setFocusTraversable(false);
		vidership.setVisible(false);

		numberOfMembers();
		numberOfCoachs();
		numberOfEquipement();
		numberOfSales();
		totalRevenue();
		averageAge();
		femaleMemberCount();
		maleMemberCount();

		lineChart(lineChart);
		barChart(barChart);
		pieChart(pieChart);

		initializeSalesTab();
		salesTableView();
		notPayed.setOnAction(event -> salesTableView());
		search.textProperty().addListener((observable, oldValue, newValue) -> salesTableView());

		initializeMembershipsTab();
		membershipTableView();
		notPayedship.setOnAction(event -> membershipTableView());
		finished.setOnAction(event -> membershipTableView());
		searchship.textProperty().addListener((observable, oldValue, newValue) -> membershipTableView());

		boxes();
		initializeMemberTab();
		memberTableView();
		search2.textProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		saleBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		genderBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		subBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		statBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		ageBox.valueProperty().addListener((observable, oldValue, newValue) -> memberTableView());
		reset.setOnAction(event -> resetBoxes());
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

	public void initializeSalesTab() {
		cequipement.setCellValueFactory(new PropertyValueFactory<>("equipement"));
		cadherent.setCellValueFactory(new PropertyValueFactory<>("member"));
		cquantite.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		cprice.setCellValueFactory(new PropertyValueFactory<>("price"));
		cpayed.setCellValueFactory(new PropertyValueFactory<>("payed"));
	}

	public void initializeMembershipsTab() {
		cmember.setCellValueFactory(new PropertyValueFactory<>("member"));
		csport.setCellValueFactory(new PropertyValueFactory<>("sport"));
		cduration.setCellValueFactory(new PropertyValueFactory<>("duration"));
		cfrom.setCellValueFactory(new PropertyValueFactory<>("fromDate1"));
		cto.setCellValueFactory(new PropertyValueFactory<>("toDate"));
		cFinishedAt.setCellValueFactory(new PropertyValueFactory<>("finishedAt"));
		cpriceship.setCellValueFactory(new PropertyValueFactory<>("price"));
		cpayedship.setCellValueFactory(new PropertyValueFactory<>("payed"));
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

	public void lineChart(LineChart<String, Number> lineChart) {
		lineChart.getData().clear();

		LocalDate currentDate = LocalDate.now();
		Map<String, Integer> memberCounts = new LinkedHashMap<>(); // Using LinkedHashMap to preserve insertion order

		String sql = "SELECT MONTH(subscription_date) AS month, COUNT(*) AS count " + "FROM Member "
				+ "WHERE MONTH(subscription_date) = ? AND YEAR(subscription_date) = ? "
				+ "GROUP BY MONTH(subscription_date)";

		try (Connection connection = getConnectionDB();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int i = 0; i < 6; i++) {
				LocalDate startDate = currentDate.minusMonths(i);
				statement.setInt(1, startDate.getMonthValue());
				statement.setInt(2, startDate.getYear());

				try (ResultSet resultSet = statement.executeQuery()) {
					int totalCount = 0;

					while (resultSet.next()) {
						totalCount += resultSet.getInt("count");
					}

					String monthYear = startDate.getMonthValue() + "/" + startDate.getYear();
					memberCounts.put(monthYear, totalCount);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		List<String> keys = new ArrayList<>(memberCounts.keySet());
		for (int i = keys.size() - 1; i >= 0; i--) {
			String monthYear = keys.get(i);
			series.getData().add(new XYChart.Data<>(monthYear, memberCounts.get(monthYear)));
		}

		lineChart.getData().add(series);

		String lineStyle = "-fx-stroke: #ff4757;";
		String dataPointStyle = "-fx-background-color: #ff4757;";

		series.getNode().setStyle(lineStyle);
		for (Data<String, Number> data : series.getData()) {
			StackPane stackPane = (StackPane) data.getNode();
			stackPane.setStyle(dataPointStyle);
			stackPane.setPickOnBounds(true);

			Tooltip tooltip = new Tooltip(String.valueOf(data.getYValue()));
			tooltip.setStyle(
					"-fx-background-color: #ff4757; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: \"Arial\";");
			Tooltip.install(stackPane, tooltip);

			stackPane.setOnMouseEntered(event -> tooltip.show(stackPane, event.getScreenX(), event.getScreenY() + 10));
			stackPane.setOnMouseExited(event -> tooltip.hide());
		}

		lineChart.setAnimated(false);
	}

	public void barChart(BarChart<String, Number> barChart) {
		barChart.getData().clear();

		LocalDate currentDate = LocalDate.now();
		Map<String, Double> revenueMap = new LinkedHashMap<>();

		String sql = "SELECT MONTH(created_at) AS month, SUM(payed) AS totalRevenue "
				+ "FROM (SELECT created_at, payed FROM Membership " + "      UNION ALL "
				+ "      SELECT created_at, payed FROM Sale) AS combined "
				+ "WHERE YEAR(created_at) = ? AND MONTH(created_at) = ? " + "GROUP BY MONTH(created_at)";

		try (Connection connection = getConnectionDB();
				PreparedStatement statement = connection.prepareStatement(sql)) {

			for (int i = 0; i < 6; i++) {
				LocalDate startDate = currentDate.minusMonths(i);
				int year = startDate.getYear();
				int month = startDate.getMonthValue();

				statement.setInt(1, year);
				statement.setInt(2, month);

				try (ResultSet resultSet = statement.executeQuery()) {
					if (resultSet.next()) {
						double totalRevenue = resultSet.getDouble("totalRevenue");
						String monthYear = startDate.getMonthValue() + "/" + year;
						revenueMap.put(monthYear, totalRevenue);
					} else {
						String monthYear = month + "/" + year;
						revenueMap.put(monthYear, 0.0);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		XYChart.Series<String, Number> series = new XYChart.Series<>();
		List<String> keys = new ArrayList<>(revenueMap.keySet());
		for (int i = keys.size() - 1; i >= 0; i--) {
			String monthYear = keys.get(i);
			series.getData().add(new XYChart.Data<>(monthYear, revenueMap.get(monthYear)));
		}

		barChart.getData().add(series);

		for (XYChart.Data<String, Number> data : series.getData()) {
			StackPane stackPane = (StackPane) data.getNode();
			stackPane.setPickOnBounds(true);
			stackPane.setStyle(
					"-fx-background-color: #747d8c; -fx-background-radius: 10px; -fx-bar-fill: cornflowerblue; -fx-font-size: 12px; -fx-font-weight: bold; -fx-font-family: \"Arial\";");

			double yValue = data.getYValue().doubleValue();
			String formattedValue;
			if (yValue >= 1000000) {
				formattedValue = String.format("%.1fM", yValue / 1000000);
			} else if (yValue >= 1000) {
				formattedValue = String.format("%.0fK", yValue / 1000);
			} else {
				formattedValue = String.valueOf(yValue);
			}
			Label valueLabel = new Label(formattedValue);
			valueLabel.setTextFill(Color.WHITE);
			stackPane.setOnMouseEntered(event -> {
				valueLabel.setTranslateY(-10);
				stackPane.getChildren().add(valueLabel);
			});
			stackPane.setOnMouseExited(event -> stackPane.getChildren().remove(valueLabel));
		}

		barChart.setAnimated(false);
	}

	public void pieChart(PieChart pieChart) {
		pieChart.getData().clear();

		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT s.Name, COUNT(ms.memberID) AS MemberCount FROM Sport s LEFT JOIN member_sport ms ON s.sportID = ms.sportID GROUP BY s.sportID, s.Name";

			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {

				List<PieChart.Data> pieChartData = new ArrayList<>();

				while (resultSet.next()) {
					int memberCount = resultSet.getInt("MemberCount");
					String sportName = resultSet.getString("Name") + " " + memberCount;

					PieChart.Data data = new PieChart.Data(sportName, memberCount);
					pieChartData.add(data);
				}

				pieChart.getData().addAll(pieChartData);
				resultSet.close();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void handleSearchTextChange(KeyEvent event) {
		if (search.getText().isEmpty()) {
			vider.setVisible(false);
		} else {
			vider.setVisible(true);
		}
	}

	@FXML
	void handleViderClick(MouseEvent event) {
		search.clear();
		vider.setVisible(false);
		search.requestFocus();
	}

	@FXML
	void SearchTextChange(KeyEvent event) {
		if (searchship.getText().isEmpty()) {
			vidership.setVisible(false);
		} else {
			vidership.setVisible(true);
		}
	}

	@FXML
	void ViderClick(MouseEvent event) {
		searchship.clear();
		vidership.setVisible(false);
		searchship.requestFocus();
	}

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

	public void numberOfMembers() {
		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT COUNT(*) AS totalMembers FROM Member";
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					members.setText(resultSet.getInt("totalMembers") + "");
				}
				resultSet.close();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void numberOfCoachs() {
		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT COUNT(*) AS totalCoaches FROM Coach";
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					coachs.setText(resultSet.getInt("totalCoaches") + "");
				}
				resultSet.close();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void numberOfEquipement() {
		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT COUNT(*) AS totalEquipement FROM Equipement";
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					equipement.setText(resultSet.getInt("totalEquipement") + "");
				}
				resultSet.close();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void averageAge() {
		try (Connection connection = getConnectionDB();
				PreparedStatement statement = connection
						.prepareStatement("SELECT AVG(DATEDIFF(CURDATE(), birthday) / 365) AS avg_age FROM Member");
				ResultSet result = statement.executeQuery()) {

			if (result.next()) {
				double averageAge = result.getDouble("avg_age");
				age.setText(String.format("%.0f", averageAge));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void femaleMemberCount() {
		String query = "SELECT COUNT(*) FROM Member WHERE gender = 'female'";
		try (Connection connection = getConnectionDB();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			if (resultSet.next()) {
				int femaleCount = resultSet.getInt(1);
				female.setText(Integer.toString(femaleCount));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void maleMemberCount() {
		String query = "SELECT COUNT(*) FROM Member WHERE gender = 'male'";
		try (Connection connection = getConnectionDB();
				PreparedStatement statement = connection.prepareStatement(query);
				ResultSet resultSet = statement.executeQuery()) {

			if (resultSet.next()) {
				int maleCount = resultSet.getInt(1);
				male.setText(Integer.toString(maleCount));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void numberOfSales() {
		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT COUNT(*) AS totalSales FROM Sale";
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					sales.setText(resultSet.getInt("totalSales") + "");
				}
				resultSet.close();
				statement.close();
			}

			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void totalRevenue() {
		try (Connection connection = getConnectionDB()) {
			String sql = "SELECT SUM(payed) AS totalRevenue FROM (SELECT payed FROM Sale UNION ALL SELECT payed FROM Membership) AS total";
			try (PreparedStatement statement = connection.prepareStatement(sql);
					ResultSet resultSet = statement.executeQuery()) {
				if (resultSet.next()) {
					revenue.setText(resultSet.getDouble("totalRevenue") + "");
				}
				resultSet.close();
				statement.close();
			}
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void salesTableView() {
		salesTab.getItems().clear();
		String query;

		if (search.getText().isEmpty()) {
			query = "SELECT e.Name AS equipement, CONCAT(m.fName, ' ', m.lName) AS member, "
					+ "s.quantity, s.price, s.payed " + "FROM Sale s "
					+ "JOIN Equipement e ON s.equipementID = e.equipementID "
					+ "JOIN Member m ON s.memberID = m.memberID ";
			if (notPayed.isSelected()) {
				query += "WHERE s.payed < s.price ";
			}
		} else {
			query = "SELECT e.Name AS equipement, CONCAT(m.fName, ' ', m.lName) AS member, "
					+ "s.quantity, s.price, s.payed " + "FROM Sale s "
					+ "JOIN Equipement e ON s.equipementID = e.equipementID "
					+ "JOIN Member m ON s.memberID = m.memberID WHERE CONCAT(m.fName, ' ', m.lName) LIKE ?";
			if (notPayed.isSelected()) {
				query += "AND s.payed < s.price ";
			}
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
				sale.setEquipement(result.getString("equipement"));
				sale.setMember(result.getString("member"));
				sale.setQuantity(result.getInt("quantity"));
				sale.setPrice(result.getDouble("price"));
				sale.setPayed(result.getDouble("payed"));
				salesTab.getItems().add(sale);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void memberTableView() {
		memberTab.getItems().clear();
		String query;

		if (search2.getText().isEmpty()) {
			query = "SELECT" + "  m.subscription_date AS csubscription,"
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
			query = "SELECT" + "  m.subscription_date AS csubscription,"
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

	public void membershipTableView() {
		membershipTab.getItems().clear();
		String query;

		if (searchship.getText().isEmpty()) {
		    query = "SELECT CONCAT(mb.fName, ' ', mb.lName) AS member, " +
		            "s.Name AS sport, " +
		            "m.price, " +
		            "m.payed, " +
		            "m.fromDate, " +
		            "m.toDate, " +
		            "DATEDIFF(m.toDate, CURDATE()) AS finishedAt, " +
		            "DATEDIFF(m.toDate, m.fromDate) AS duration " +
		            "FROM Membership m " +
		            "JOIN Member mb ON m.memberID = mb.memberID " +
		            "JOIN Sport s ON m.sportID = s.sportID";
		    if (notPayedship.isSelected() || finished.isSelected()) {
		        query += " WHERE";
		    }
		    if (notPayedship.isSelected()) {
		        query += " m.payed < m.price";
		    }
		    if (notPayedship.isSelected() && finished.isSelected()) {
		        query += " AND";
		    }
		    if (finished.isSelected()) {
		        query += " DATEDIFF(m.toDate, CURDATE()) <= 0";
		    }
		} else {
		    query = "SELECT CONCAT(mb.fName, ' ', mb.lName) AS member, " +
		            "s.Name AS sport, " +
		            "m.price, " +
		            "m.payed, " +
		            "m.fromDate, " +
		            "m.toDate, " +
		            "DATEDIFF(m.toDate, CURDATE()) AS finishedAt, " +
		            "DATEDIFF(m.toDate, m.fromDate) AS duration " +
		            "FROM Membership m " +
		            "JOIN Member mb ON m.memberID = mb.memberID " +
		            "JOIN Sport s ON m.sportID = s.sportID " +
		            "JOIN (" +
		            "    SELECT memberID, MAX(toDate) AS maxToDate " +
		            "    FROM Membership " +
		            "    GROUP BY memberID" +
		            ") maxDates ON m.memberID = maxDates.memberID AND m.toDate = maxDates.maxToDate " +
		            "WHERE CONCAT(mb.fName, ' ', mb.lName) LIKE ?";
		    if (notPayedship.isSelected()) {
		        query += " AND m.payed < m.price";
		    }
		    if (finished.isSelected()) {
		        query += " AND DATEDIFF(m.toDate, CURDATE()) <= 0";
		    }
		}

		try (Connection connection = getConnectionDB()) {
			PreparedStatement statement = connection.prepareStatement(query);
			if (!searchship.getText().isEmpty()) {
				String text = "%" + searchship.getText().toLowerCase() + "%";
				statement.setString(1, text);
			}
			ResultSet result = statement.executeQuery();
			while (result.next()) {
				Membership membership = new Membership();
				membership.setMember(result.getString("member"));
				membership.setSport(result.getString("sport"));
				membership.setToDate(result.getDate("toDate"));
				membership.setFromDate1(result.getDate("fromDate"));

				membership.setDuration(result.getLong("duration") + " Days");
				membership.setFinishedAt(result.getLong("finishedAt") + " Days");
				membership.setPrice(result.getDouble("price"));
				membership.setPayed(result.getDouble("payed"));
				membershipTab.getItems().add(membership);
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

}