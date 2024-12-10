package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.util.StringConverter;

public class MembershipController {
	private Main main=Main.getInstance();
    private User user = main.getUser();
     
	private Member member=new Member();
	
	private List<Sport> sport_list=new ArrayList<Sport>();
	private List<Plan>  plans= new ArrayList<Plan>();
 

    @FXML
    private ComboBox<Sport> type_sport;

    @FXML
    private Label reste;

    @FXML
    private Label payee;

    @FXML
    private Label prix;

    @FXML
    private RadioButton duree1;

    @FXML
    private ToggleGroup duree;

    @FXML
    private RadioButton duree2;

    @FXML
    private RadioButton duree3;

    @FXML
    private RadioButton duree4;

    @FXML
    private DatePicker duree4_entry;

    @FXML
    private TextField price;

    @FXML
    private Label fullname;

	private boolean isDuree4Selected = false;

	@FXML
	public void initialize(Member member) {
		if(member!=null)
		{this.member=member;
		System.out.println(member);
		
		sport_list=getAllSports();
		ObservableList<Sport> sportsObservableList = FXCollections.observableArrayList(sport_list);
		fullname.setText("Member: "+member.getFname()+" "+member.getLname());
		

        type_sport.setItems(sportsObservableList);
        getAllPlans();
        duree1.setText(this.plans.get(0).getName());
        duree2.setText(this.plans.get(1).getName());
        duree3.setText(this.plans.get(2).getName());
        
        type_sport.setValue(sport_list.get(0));
        duree4_entry.setValue(LocalDate.now().plusMonths(1).withDayOfMonth(1));
        setUpListeners();
        updateResteLabel();}
		
		
	}
	
	private void setUpListeners() {
        duree4.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                isDuree4Selected = newValue;

                duree4_entry.setDisable(!isDuree4Selected);

                updateResteLabel();
            }
        });


        duree1.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });

        duree2.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });

        duree3.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });
        
        price.textProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });
        
        duree4_entry.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });
        type_sport.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateResteLabel();
        });
        


    }
	

	

	private void updateResteLabel() {
		 double payedValue = Float.parseFloat(price.getText());
		    double calculatedPrice = 0;

		    boolean isDuree1Selected = duree1.isSelected();
		    boolean isDuree2Selected = duree2.isSelected();
		    boolean isDuree3Selected = duree3.isSelected();
		    boolean isDuree4Selected = duree4.isSelected();

		    Sport selectedSport = type_sport.getSelectionModel().getSelectedItem();

		    if (selectedSport == null) {
		        showAlert(AlertType.ERROR, "Error", "Please select a sport.");
		        return;
		    }

		    if (isDuree1Selected) {
		        Plan plan = selectedSport.getPlans().get(0);
		        calculatedPrice = plan.getPrice();
		    } else if (isDuree2Selected) {
		        Plan plan = selectedSport.getPlans().get(1);
		        calculatedPrice = plan.getPrice();
		    } else if (isDuree3Selected) {
		        Plan plan = selectedSport.getPlans().get(2);
		        calculatedPrice = plan.getPrice();
		    } else if (isDuree4Selected) {
		        LocalDate dt = duree4_entry.getValue();
		        if (dt != null) {
		            LocalDate today = LocalDate.now();
		            long noOfDays = ChronoUnit.DAYS.between(today, dt);
		            calculatedPrice = (int) (noOfDays * 20 / 3);
		        } else {
		            showAlert(AlertType.ERROR, "Error", "Please select a date for Duree4.");
		            return;
		        }
		    }

		    double prixValue = calculatedPrice;
		    double payeeValue = payedValue;

		    reste.setText("Reste: " + (calculatedPrice - payedValue));
		    reste.setVisible((calculatedPrice - payedValue) != 0);

		    prix.setText("Prix: " + prixValue);
		    payee.setText("Payee: " + payeeValue);
		
	}

	@FXML
	void submit_membership(ActionEvent event) {
	    Membership membership = new Membership();

	    if (price.getText().isEmpty()) {
	        showAlert(AlertType.ERROR, "Error", "Please enter a valid price.");
	        return;
	    }

	    double payed = Float.parseFloat(price.getText());
	    double calculatedPrice = 0;
	    int numberOfMonths = 0;

	    boolean isDuree1Selected = duree1.isSelected();
	    boolean isDuree2Selected = duree2.isSelected();
	    boolean isDuree3Selected = duree3.isSelected();
	    boolean isDuree4Selected = duree4.isSelected();

	    Sport selectedSport = type_sport.getSelectionModel().getSelectedItem();

	    if (selectedSport == null) {
	        showAlert(AlertType.ERROR, "Error", "Please select a sport.");
	        return;
	    }

	    if (isDuree1Selected) {
	        Plan plan = selectedSport.getPlans().get(0);
	        calculatedPrice = plan.getPrice();
	        numberOfMonths = plan.getDuration()/30;
	    } else if (isDuree2Selected) {
	        Plan plan = selectedSport.getPlans().get(1);
	        calculatedPrice = plan.getPrice();
	        numberOfMonths = plan.getDuration()/30;
	    } else if (isDuree3Selected) {
	        Plan plan = selectedSport.getPlans().get(2);
	        calculatedPrice = plan.getPrice();
	        numberOfMonths = plan.getDuration()/30;
	    } else if (isDuree4Selected) {
	        LocalDate dt = duree4_entry.getValue();
	        if (dt != null) { 
	            LocalDate today = LocalDate.now();
	            long noOfDays = ChronoUnit.DAYS.between(today, dt);
	            calculatedPrice = (int) (noOfDays * 20 / 3);
	            numberOfMonths = (int) (noOfDays / 30); 
	        } else {
	            showAlert(AlertType.ERROR, "Error", "Please select a date for Duree4.");
	            return;
	        }
	    }

	    java.sql.Date membershipToDate = java.sql.Date.valueOf(LocalDate.now().plusMonths(numberOfMonths));

	    membership.setSportID(selectedSport.getSportID());
	    membership.setPayed(payed);
	    membership.setPrice(calculatedPrice);
	    membership.setToDate(membershipToDate);
	    System.out.println(membership);
	    insertMembership(member, membership);
	}

	

	
	public  void insertMembership(Member member, Membership membership) {
	    String query = "INSERT INTO Membership (memberID, userID,price, payed, toDate, note, sportID, fromDate) VALUES (?, ?,  ?, ?, ?, ?, ?, ?)";

	    try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        preparedStatement.setInt(1, member.getMemberID());
	        preparedStatement.setInt(2,user.getUserID()); 
	        preparedStatement.setDouble(3, membership.getPrice());
	        preparedStatement.setDouble(4, membership.getPayed());
	        preparedStatement.setDate(5, membership.getToDate());
	        preparedStatement.setString(6, membership.getNote());
	        preparedStatement.setInt(7, membership.getSportID());
	        preparedStatement.setString(8, membership.getFromDate().toString());

	        preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	private static void showAlert(AlertType alertType, String title, String content) {
	    Alert alert = new Alert(alertType);
	    alert.setTitle(title);
	    alert.setHeaderText(null);
	    alert.setContentText(content);
	    alert.showAndWait();
	}
	public  List<Sport> getAllSports() {
        List<Sport> sports = new ArrayList<>();
        String query = "SELECT * FROM Sport sp LEFT JOIN member_sport ms ON ms.sportID=sp.sportID WHERE ms.memberID=?; ";
        try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
        	preparedStatement.setInt(1, member.getMemberID());
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
	public Member getMemberByID(int memberID) {
	    Member member = null;
	    String query = "SELECT * FROM Member WHERE memberID = ?";

	    try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {

	        preparedStatement.setInt(1, memberID);
	        ResultSet resultSet = preparedStatement.executeQuery();

	        if (resultSet.next()) {
	            member = new Member();
	            member.setMemberID(resultSet.getInt("memberID"));
	            member.setFname(resultSet.getString("fname"));
	            member.setLname(resultSet.getString("lname"));
	            member.setPhone(resultSet.getString("phone"));
	            member.setBirthday(resultSet.getDate("birthday").toLocalDate());
	            System.out.println(member);
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        showAlert(AlertType.ERROR, "Error", "Error retrieving member. Please try again.");
	    }

	    return member;
	}

	
	public void getAllPlans() {
	    
	    for (Sport sp : sport_list) {
	    	List<Plan> plans = new ArrayList<>();
			
		
	    String query = "SELECT * FROM plans WHERE sportID=?";

	    try (Connection connection = DriverManager.getConnection(main.getDb_url(), main.getDb_usr(), main.getDb_pwd());
	         PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	    	preparedStatement.setInt(1, sp.getSportID());
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
	        sp.setPlans(plans);
	        this.plans=plans;
	        

	    } catch (SQLException e) {
	        e.printStackTrace();
	        showAlert(AlertType.ERROR, "Error", "Error retrieving plans from the database.");
	    }
	    }
	}
	
	 
	
}
