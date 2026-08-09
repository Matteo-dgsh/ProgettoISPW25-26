package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import logic.view.EssentialGUI;

import logic.utils.LoggedUser;
import logic.utils.enums.UserTypes;

public class GCProfile extends EssentialGUI{

    @FXML
    private Label birthDate;

    @FXML
    private Label city;

    @FXML
    private Label firstName;

    @FXML
    private Label gender;

    @FXML
    private Label lastName;

    @FXML
    private Label province;

    @FXML
    private Label userName;

    @FXML
    private Label userType;

    @FXML
    public void initialize() {
        UserTypes type;
        if (LoggedUser.getUserType().equals(UserTypes.USER)){
            type = UserTypes.USER;
        }else{
            type = UserTypes.ORGANIZER;
        }
        this.userName.setText(LoggedUser.getUserName());
        this.firstName.setText(LoggedUser.getFirstName());
        this.lastName.setText(LoggedUser.getLastName());
        this.birthDate.setText(LoggedUser.getBirthDate());
        this.gender.setText(LoggedUser.getGender());
        this.province.setText(LoggedUser.getProvince());
        this.city.setText(LoggedUser.getCity());
        this.userType.setText(type.toString());
    }

}
