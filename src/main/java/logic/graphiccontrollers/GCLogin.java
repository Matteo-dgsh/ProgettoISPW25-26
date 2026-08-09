package logic.graphiccontrollers;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import com.google.api.client.auth.oauth2.Credential;

import logic.exceptions.InvalidTokenValue;
import logic.utils.enums.Alerts;
import logic.controllers.GoogleLogin;
import logic.utils.LoggedUser;
import logic.view.EssentialGUI;
import logic.beans.BUserData;

public class GCLogin extends EssentialGUI{
    @FXML
    private PasswordField passwd;
    @FXML
    private TextField usrname;

    private boolean isGoogleAuth;

    @FXML
    public void initialize() {
        this.isGoogleAuth = false;
    }

    @FXML
    public void registerControl(MouseEvent event){
        changeGUI(event, "ClassicRegistration.fxml");
    }

    @FXML
    public void loginControl(MouseEvent event){
        BUserData userBean;
        try {
            if (isGoogleAuth) {
                //google login: apre il browser di sistema e attende in modo sincrono l'esito
                //dell'autenticazione OAuth (flusso "loopback", vedi GoogleLogin.performGoogleLogin)
                userBean = performGoogleAuth();
                if (userBean == null) {
                    //login Google annullato/fallito: l'eventuale messaggio e' gia' stato mostrato
                    return;
                }
            } else {
                //classic login
                userBean = new BUserData(this.usrname.getText(), this.passwd.getText());
            }
            if (cfacade.loginUser(userBean, this.isGoogleAuth) == 1) {
                switch (LoggedUser.getUserType()) {
                    case USER:
                        alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as a user");
                        changeGUI(event, "HomeUser.fxml");
                        break;
                    case ORGANIZER:
                        alert.displayAlertPopup(Alerts.INFORMATION, "Logged in successfully as an organizer");
                        changeGUI(event, "HomeOrg.fxml");
                        break;
                    default:
                        alert.displayAlertPopup(Alerts.ERROR, "Cannot load home page from login");
                }
            } else {
                if (!isGoogleAuth) {
                    alert.displayAlertPopup(Alerts.WARNING, "User not registered or wrong credentials. Please retry...");
                } else {
                    alert.displayAlertPopup(Alerts.WARNING, "User not registered using Google Auth! \nYou will be redirected to Registration Page");
                    LoggedUser.setUserName(userBean.getUsername());
                    changeGUI(event, "GoogleRegistration.fxml");
                }
            }
        } catch (RuntimeException e){
            alert.displayAlertPopup(Alerts.ERROR, "Runtime exception: " + e.getMessage());
        }
    }

    //Esegue il login Google vero e proprio (browser + server locale di callback) e ne ricava lo
    //username (la mail dell'account Google). Ritorna null (mostrando un alert) se l'utente annulla,
    //se client_secrets.json manca/e' invalido, o se il recupero della mail fallisce.
    private BUserData performGoogleAuth() {
        try {
            Credential credential = GoogleLogin.performGoogleLogin();
            if (credential == null) {
                alert.displayAlertPopup(Alerts.WARNING, "Google login is not configured correctly or was cancelled.");
                return null;
            }
            String email = GoogleLogin.getGoogleAccountEmail(credential);
            if (email == null) {
                alert.displayAlertPopup(Alerts.ERROR, "Could not retrieve your Google account email.");
                return null;
            }
            return new BUserData(email);
        } catch (InvalidTokenValue e) {
            alert.displayAlertPopup(Alerts.WARNING, "Google authentication failed. Please retry...");
            return null;
        }
    }

    @FXML
    public void googleLoginControl(MouseEvent event){
        this.isGoogleAuth = true;
        loginControl(event);
    }
}
