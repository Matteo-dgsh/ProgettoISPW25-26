package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import logic.utils.enums.Alerts;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;

import java.util.ArrayList;
import java.util.List;

public class GCEditCity extends EssentialGUI {

    @FXML
    private ChoiceBox<String> cityChoiceBox;

    @FXML
    private ChoiceBox<String> provinceChoiceBox;

    private List<String> provincesList = new ArrayList<>();
    private ObservableList<String> provincesObsList;
    private List<String> citiesList = new ArrayList<>();


    @FXML
    void initialize(){
        //province e citta' choicebox
        if (provincesList.isEmpty()) {
            provincesList = cfacade.getProvincesList();
            provincesObsList = FXCollections.observableArrayList(provincesList);
        }
        this.provinceChoiceBox.setItems(provincesObsList);
        setupProvinceBoxListener();
        this.provinceChoiceBox.setValue(LoggedUser.getProvince());
        this.cityChoiceBox.setValue(LoggedUser.getCity());
    }

    @FXML
    void changeCityControl(MouseEvent event) {
        boolean res = alert.askChangeCityConfirmation();
        try{
            if (res){
                //conferma il cambiamento
                submitChange(event);
            } else {
                //annulla il cambiamento
                alert.displayAlertPopup(Alerts.WARNING, "City update cancelled.");
            }
        } catch (RuntimeException e) {
            alert.displayAlertPopup(Alerts.ERROR, e.getMessage());
        }
    }

    private void submitChange(MouseEvent event) {
        try {
            if (cfacade.changeUserCity(LoggedUser.getUserID(), this.provinceChoiceBox.getValue(), this.cityChoiceBox.getValue()) == 1) {
                alert.displayAlertPopup(Alerts.INFORMATION, "City updated successfully. Now you will fetch events in " + LoggedUser.getCity());
            }
        } catch (Exception e){
            alert.displayAlertPopup(Alerts.ERROR, "City update failed.");
        } finally {
            goToSettings(event);
        }
    }

    private void setupProvinceBoxListener() {
        provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            citiesList.clear();
            citiesList = cfacade.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    private void updateCityListView() {
        ObservableList<String> citiesObsList = FXCollections.observableArrayList(citiesList);
        cityChoiceBox.setItems(citiesObsList);
    }


}
