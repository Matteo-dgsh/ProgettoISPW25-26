package logic.view;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import logic.utils.*;
import logic.controllers.CFacade;
import logic.utils.enums.Alerts;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.PersistenceTypes;
import logic.utils.enums.UserTypes;

public class EssentialGUI extends Application implements NotificationView {
    private static final String APP_NAME = "NightAgent";
    private static final String PATH = "icons";
    private static final String LOGO_NAME = "favicon.png";
    protected static String sceneName;
    protected static Scene scene;
    protected static CFacade cfacade;
    protected AlertPopup alert;
    protected static EssentialGUI gui = new EssentialGUI();

    //dichiaro logger pubblico, globale e costante
    public static final Logger logger = Logger.getLogger(APP_NAME);

    public EssentialGUI() {
        this.alert = new AlertPopup();
    }

    @Override
    public void start(Stage stage) {
        try {
            CFacade.setNotiGraphic(this);
            stage.setTitle(APP_NAME);
            String absolutePath = setAbsolutePath();
            Image logoImage = new Image(absolutePath);
            stage.getIcons().add(logoImage);
            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load absolute path of app icon\n", e);
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Cannot load EssentialGUI due to illegal argument into logo image\n", e);
        }
    }

    private String setAbsolutePath() {
        try {
            return getClass().getResource("/" + PATH + "/" + LOGO_NAME).toExternalForm();
        } catch (NullPointerException e) {
            throw new NullPointerException();
        }
    }

    public static void setScene(String newScene) {
        sceneName = newScene;
    }

    //cfacade non e' piu' istanziato eager in dichiarazione: va creato SOLO dopo che PersistenceClass.setPersistenceType(...)
    //e' stato impostato (dalla schermata di scelta modalita' o dall'argomento da CLI), altrimenti CNotification/i DAO
    //verrebbero costruiti col tipo di persistenza di default sbagliato
    public static void initCfacade() {
        cfacade = new CFacade();
    }

    public void nextGuiOnClick(MouseEvent event) {
        Stage next = (Stage) ((Node) event.getSource()).getScene().getWindow();
        start(next);
    }

    public static void loadApp() {
        try {
            URL loc = EssentialGUI.class.getResource(sceneName);
            Parent root = null;
            if (loc != null) {
                root = FXMLLoader.load(loc);
            }
            scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(EssentialGUI.class.getResource("application.css")).toExternalForm());
        } catch (IOException | NullPointerException e) {
            logger.log(Level.SEVERE, "Cannot load scene\n", e);
        }
    }


    public void changeGUI(MouseEvent event, String newScene) {
        setScene(newScene);
        loadApp();
        nextGuiOnClick(event);
    }

    //chiede all'avvio se usare l'interfaccia grafica o quella a riga di comando (CLI.java, gia' esistente
    //e indipendente). Se lo standard input non e' disponibile/interattivo (es. eseguibile jlink lanciato
    //con doppio click) si ricade in automatico sulla GUI, senza bloccare l'avvio.
    private static boolean askForCliMode() {
        System.out.println("Avvia in modalita':");
        System.out.println("1) Interfaccia grafica (default)");
        System.out.println("2) Riga di comando (CLI)");
        System.out.print("Scelta: ");
        //NB: niente try-with-resources qui: chiudere questo Scanner chiuderebbe anche System.in,
        //impedendo poi a CLI.java (che legge a sua volta da System.in) di funzionare.
        try {
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String choice = scanner.nextLine().trim();
            return "2".equals(choice) || "cli".equalsIgnoreCase(choice);
        } catch (Exception e) {
            logger.info("Nessun input interattivo disponibile: avvio in modalita' grafica di default");
            return false;
        }
    }

    public static void main(String[] args) {
        if (askForCliMode()) {
            CLI.main(args);
            return;
        }

        //se viene passato un argomento da CLI, la modalita' di persistenza e' gia' nota: la impostiamo subito,
        //inizializziamo cfacade DI CONSEGUENZA (fix del bug per cui cfacade veniva creato eager prima che
        //PersistenceClass.setPersistenceType fosse mai chiamato) e si salta direttamente al Login.
        //Altrimenti si parte dalla schermata di scelta Demo/Normale, che imposta la persistenza e chiama
        //initCfacade() al click del bottone.
        if (args.length > 0) {
            if ("JDBC".equals(args[0])) {
                logger.info("NightAgent started with JDBC persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
            } else if ("FileSystem".equals(args[0])) {
                logger.info("NightAgent started with FileSystem persistence logic");
                PersistenceClass.setPersistenceType(PersistenceTypes.FILE_SYSTEM);
            }
            initCfacade();
            setScene("Login.fxml");
        } else {
            logger.info("NightAgent started: waiting for mode selection (Demo/Normale)");
            setScene("ModeSelection.fxml");
        }

        loadApp();
        launch(args);
    }

    public void goToHome(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "HomeUser.fxml");
        } else {
            changeGUI(event, "HomeOrg.fxml");
        }
    }

    public void goToNotifications(MouseEvent event) {
        changeGUI(event, "Notifications.fxml");
    }

    public void goToYourEvents(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "YourEventsUser.fxml");
        } else {
            changeGUI(event, "YourEventsOrg.fxml");
        }
    }

    public void goToSettings(MouseEvent event) {
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            changeGUI(event, "SettingsUser.fxml");
        } else {
            changeGUI(event, "SettingsOrg.fxml");
        }
    }

    @Override
    public void showNotification(NotificationTypes type) {
        Platform.runLater(() -> {
            if (type.equals(NotificationTypes.EVENT_ADDED)) {
                alert.displayAlertPopup(Alerts.INFORMATION, "New event in your city!\nCheck your events page.");
            } else if (type.equals(NotificationTypes.USER_EVENT_PARTICIPATION)) {
                alert.displayAlertPopup(Alerts.INFORMATION, "New user participating to your event.");
            }
        });
    }
}
