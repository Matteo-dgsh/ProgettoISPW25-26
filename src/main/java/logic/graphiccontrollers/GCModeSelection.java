package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import logic.utils.PersistenceClass;
import logic.utils.enums.PersistenceTypes;
import logic.view.EssentialGUI;

public class GCModeSelection extends EssentialGUI {

    private static final String LOGIN_FXML = "Login.fxml";

    @FXML
    public void demoModeControl(MouseEvent event) {
        logger.info("NightAgent started in Demo mode: all data kept in-memory, nothing is persisted");
        PersistenceClass.setPersistenceType(PersistenceTypes.IN_MEMORY);
        initCfacade();
        changeGUI(event, LOGIN_FXML);
    }

    @FXML
    public void normalModeControl(MouseEvent event) {
        logger.info("NightAgent started in Normal mode: JDBC persistence");
        PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
        initCfacade();
        changeGUI(event, LOGIN_FXML);
    }

    @FXML
    public void fileSystemModeControl(MouseEvent event) {
        logger.info("NightAgent started in File System mode: notifications persisted to CSV, everything else via JDBC");
        PersistenceClass.setPersistenceType(PersistenceTypes.FILE_SYSTEM);
        initCfacade();
        changeGUI(event, LOGIN_FXML);
    }
}
