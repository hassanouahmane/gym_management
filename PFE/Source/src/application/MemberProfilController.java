package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class MemberProfilController {
	public Member member=null;
	private Node previousView;
	private BorderPane main_border;
	
	@FXML
    private Button back_btn;

    @FXML
    private ImageView image;

    @FXML
    private Label fullname;

    @FXML
    private TextField prenom_txt;

    @FXML
    private TextField nom_txt;

    @FXML
    private TextField phone_txt;

    @FXML
    private DatePicker birthday_txt;

    @FXML
    private TextField age_txt;

    @FXML
    private TextField cin_txt;

    @FXML
    private Label first_subscription;

    @FXML
    private Label sports;

    @FXML
    private Label statut;

    @FXML
    private TextArea note_txt;
    
    @FXML
    public void initialize() {
    	 
    	
    }
    public void fillMemberFields(int memberId) {
        Member member = getMemberInfo(memberId,this.member);

        if (member != null) {
            prenom_txt.setText(member.getFname());
            nom_txt.setText(member.getLname());
            phone_txt.setText(member.getPhone());
            birthday_txt.setValue(member.getBirthday());
            member.setAge();
            age_txt.setText(""+member.getAge()+" ans");
            cin_txt.setText(member.getIdentity());
            note_txt.setText(member.getDescription());
            fullname.setText(member.getFname()+" "+member.getLname());
            statut.setText(member.getStat());
            sports.setText(member.getSport());
            
            
        } else {
            showAlert("Error", "Member not found", AlertType.ERROR);
        }
    }

    

    
    public static Member getMemberInfo(int memberId,Member mem) {
    	System.out.println(mem);
    	if(mem!=null)
    		return mem;
    	
        String url = "jdbc:mysql://localhost:3306/fitpro";
        String user = "root";
        String password = "";

        String query = "SELECT * FROM Member WHERE memberID = ?";

        try (Connection connection = DriverManager.getConnection(url, user, password);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, memberId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                Member member = new Member();
                member.setMemberID(resultSet.getInt("memberID"));
                member.setFname(resultSet.getString("fname"));
                member.setLname(resultSet.getString("lname"));
                member.setPhone(resultSet.getString("phone"));
                member.setBirthday(resultSet.getDate("birthday").toLocalDate());
                member.setAddress(resultSet.getString("address"));
                member.setParent_fullname(resultSet.getString("parent_fullname"));
                member.setParent_phone(resultSet.getString("parent_phone"));
                member.setGender(resultSet.getString("gender"));
                member.setIdentity(resultSet.getString("identity"));
                member.setSubscription_date(resultSet.getTimestamp("subscription_date"));
                member.setDescription(resultSet.getString("description"));
                member.setAge();
                
                return member;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private void showAlert(String title, String content, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public void setMember(Member member) {
		this.member = member;
	}
    
    @FXML
    void closeProfil(ActionEvent event) {
        // Code to switch back to the previous page
        // For example, you might use the main border pane to set its center to the previous view
        if (previousView != null) {
            main_border.setCenter(previousView);
        }
    }
    
    void setPreviousView(Node view)
    {
    	this.previousView=view;
    }
    void setBorderPane(BorderPane pane)
    {
    	this.main_border=pane;
    }
	
    


}
