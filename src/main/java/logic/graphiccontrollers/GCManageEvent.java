package logic.graphiccontrollers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import logic.beans.BEvent;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class GCManageEvent extends EssentialGUI {
    @FXML
    protected DatePicker datePicker;
    @FXML
    protected TextField eventAddressTF;
    @FXML
    protected TextField eventNameTF;
    @FXML
    protected TextField eventHourTF;

    @FXML
    protected TextField eventMinutesTF;
    @FXML
    protected Button fileChooserBtn;
    @FXML
    protected Label pickedFileLabel;
    @FXML
    protected ChoiceBox<String> musicGenreBox;
    @FXML
    protected ChoiceBox<String> cityChoiceBox;
    @FXML
    protected ChoiceBox<String> provinceChoiceBox;

    protected BEvent eventBean;
    protected byte[] eventPicData;
    protected List<String> provincesList = new ArrayList<>();
    protected ObservableList<String> provincesObsList;
    protected List<String> citiesList = new ArrayList<>();
    protected ObservableList<String> citiesObsList;
    private final String[] musicGenres = {"Pop","Rock","Dance","Electronic","Techno","Reggaeton", "Metal", "Disco", "Tech house", "House", "Rap", "Trap"};
    protected final ObservableList<String> musicGenresList = FXCollections.observableArrayList(musicGenres);

    @FXML
    void pickImage(MouseEvent event) {
        FileChooser fc = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Immagini (*.png, *.jpg)", "*.png", "*jpg");
        fc.getExtensionFilters().addAll(imageFilter);
        File eventPicFile = fc.showOpenDialog(null);

        if (eventPicFile != null) {
            pickedFileLabel.setText(eventPicFile.getName());
        }

        //conversione da file ad array di bytes
        if (eventPicFile != null) {
            try {
                this.eventPicData = Files.readAllBytes(eventPicFile.toPath());
            } catch (IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
            }
        }
    }

    protected void setupProvinceBoxListener() {
        provinceChoiceBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            citiesList.clear();
            citiesList = cfacade.getCitiesList(String.valueOf(newValue));
            updateCityListView();
        });
    }

    protected void updateCityListView() {
        citiesObsList = FXCollections.observableArrayList(citiesList);
        cityChoiceBox.setItems(citiesObsList);
    }

    protected void setEventBean(BEvent bean) throws InvalidValueException, TextTooLongException {
        bean.setEventName(eventNameTF.getText());
        bean.setEventProvince(provinceChoiceBox.getValue());
        bean.setEventCity(cityChoiceBox.getValue());
        bean.setEventAddress(eventAddressTF.getText());
        bean.setEventMusicGenre(musicGenreBox.getValue());
        bean.setEventDate(datePicker.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        bean.setEventTime(eventHourTF.getText(), eventMinutesTF.getText());

        if (eventPicData != null) {
            bean.setEventPicData(eventPicData);
        }

        bean.setEventOrganizer(LoggedUser.getUserName());
        bean.setEventOrganizerID(LoggedUser.getUserID());
        bean.setEventPicPath(pickedFileLabel.getText());
    }


}
