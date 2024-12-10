package application;

import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.animations.FadeAnimation;
import tray.animations.PopupAnimation;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

public class MainController {
	private User user=Main.getInstance().getUser();
	private static MainController instance; 
	
	private final String SELECTED_BUTTON_CLASS = "selected-button";
	private final String UNSELECTED_BUTTON_CLASS = "unselected-button";
	
    @FXML
    private AnchorPane scene;

    @FXML
    private BorderPane main_border;

    @FXML
    private Button usr_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private Button home_btn;

    @FXML
    private Button coaches_btn;

    @FXML
    private Button member_btn;
    
    @FXML
    private Button store_btn;

    @FXML
    private Button gym_btn;
    
    @FXML
    private Button settings_btn;
    
    @FXML
    void initialize() {
    	
    	instance=this;
    	try {
    		usr_btn.setText(user.getFName()+" "+user.getLName());
			//switchCenter("/application/fxml/home.fxml");
    		SwitchToMember(null);
    		
    		if (user.getAuthority()!='a')
    		{
    			settings_btn.setVisible(false);
    		}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void showNotification(String notificationTitle, String notificationContext) {
        TrayNotification notification = new TrayNotification(notificationTitle, notificationContext, NotificationType.WARNING);
        notification.setAnimationType(AnimationType.POPUP);
        notification.showAndDismiss(Duration.seconds(2));
    }







    @FXML
    void LogOut(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout Confirmation"); 
        alert.setHeaderText("Are you sure you want to logout?");
        alert.initStyle(StageStyle.UTILITY);

        // Add buttons to the alert
        ButtonType buttonTypeYes = new ButtonType("Yes");
        ButtonType buttonTypeNo = new ButtonType("No");

        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeYes) {
                // Close the current stage
                Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                currentStage.close();

                // Show the login stage
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/login.fxml"));
                    Parent root = loader.load();
                    Stage loginStage = new Stage();
                    loginStage.setTitle("Login");
                    loginStage.setScene(new Scene(root));
                    loginStage.show();

                    System.out.println("User logged out.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (response == buttonTypeNo) {
                // User chose not to logout
                System.out.println("Logout canceled.");
            }
        });
    }
     
    public void switchCenter(String fxmlFileName) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
        Parent root = loader.load();

        

        if (main_border != null) {
            main_border.setCenter(root);
        } else {
            System.out.println("BorderPane with ID 'mainBorderPane' not found in the loaded FXML.");
        }
    }


    @FXML
    void SwitchToCoaches(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/coach.fxml");
        setButtonStyle(coaches_btn);
    }

    @FXML
    void SwitchToGym(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/gym.fxml");
        setButtonStyle(gym_btn);
    }

    @FXML
    void SwitchToHome(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/home.fxml");
        setButtonStyle(home_btn);
    }

    @FXML
    void SwitchToMember(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/adherent1.fxml");
        setButtonStyle(member_btn);
    }

    @FXML
    void SwitchToSettings(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/settings.fxml");
        setButtonStyle(settings_btn);
    }

    @FXML
    void SwitchToStore(ActionEvent event) throws IOException {
        switchCenter("/application/fxml/store.fxml");
        setButtonStyle(store_btn);
    }

    private void setButtonStyle(Button selectedButton) {
        Button[] allButtons = {home_btn, coaches_btn, member_btn, store_btn, gym_btn, settings_btn};
        for (Button button : allButtons) {
            if (button == selectedButton) {
                button.getStyleClass().clear();
                button.getStyleClass().add("sidebtn");
                button.getStyleClass().add("sidebtn-selected");
            } else {
                button.getStyleClass().clear();
                button.getStyleClass().add("sidebtn");
            }
        }
    }


    
    public void switchToProfil(Member member, Node previousView) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/fxml/member-profile.fxml"));
            Parent root = loader.load();

            MemberProfilController controller = loader.getController();

            controller.setMember(member);
            controller.fillMemberFields(0);
            controller.setBorderPane(main_border);
            controller.setPreviousView(previousView);

            if (main_border != null) {
                main_border.setCenter(root);
            } else {
                System.out.println("BorderPane with ID 'mainBorderPane' not found in the loaded FXML.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error loading Member Profile view.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static MainController getInstance() {
        return instance;
    }

	public void setConfiguration(Configuration configuration) {
		
	}
    


}
