package logic.controllers;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;

import logic.exceptions.InvalidTokenValue;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

import static logic.view.EssentialGUI.logger;

public class GoogleLogin {
    private GoogleLogin(){
        //empty
    }

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CLIENT_SECRETS_FILE_PATH = "client_secrets.json";
    private static final String SCOPES = "https://www.googleapis.com/auth/userinfo.email";

    //Avvia il login Google con il flusso "loopback" raccomandato da Google (redirect verso un server HTTP
    //locale temporaneo su 127.0.0.1), che ha sostituito il vecchio flusso "out-of-band" (copia/incolla
    //manuale del codice di autorizzazione), dismesso da Google per tutti i client a partire dal 2023.
    //Apre il browser di sistema, l'utente autorizza l'app, e AuthorizationCodeInstalledApp intercetta
    //automaticamente il codice tramite il server locale e lo scambia con le Credential: tutto in modo
    //sincrono e senza bisogno di finestre/input manuali da parte dell'utente.
    //Uso apposta un DataStore in memoria (non su file): se salvassimo le Credential su disco, un
    //secondo login Google (anche in run diversi dell'app) riuserebbe silenziosamente l'account salvato
    //la prima volta, senza piu' chiedere quale account Google usare - comportamento sbagliato soprattutto
    //in modalita' Demo, dove ci si aspetta che ogni avvio riparta "pulito".
    //Ritorna le Credential se il login va a buon fine, null se client_secrets.json manca o e' invalido.
    public static Credential performGoogleLogin() throws InvalidTokenValue {
        try {
            InputStream in = GoogleLogin.class.getClassLoader().getResourceAsStream(CLIENT_SECRETS_FILE_PATH);
            if (in == null) {
                logger.severe("client_secrets.json not found in resources! Cannot start Google login");
                return null;
            }
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, Collections.singletonList(SCOPES))
                    .setDataStoreFactory(new MemoryDataStoreFactory())
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        } catch (IOException | GeneralSecurityException e) {
            throw new InvalidTokenValue("Google authentication failed: ", e);
        }
    }

    public static String getGoogleAccountEmail(Credential cred){
        JsonFactory jsonFactory = cred.getJsonFactory();

        return getEmailFromGoogle(cred, jsonFactory);
    }

    private static String getEmailFromGoogle(Credential credential, JsonFactory jsonFactory){
        try {
            //Make a request to Google's UserInfo API to get the user's email
            String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo";

            // Crea una richiesta GET all'endpoint UserInfo
            HttpRequest userInfoRequest = credential.getTransport().createRequestFactory()
                    .buildGetRequest(new GenericUrl(userInfoEndpoint));

            // Aggiunge l'intestazione di autorizzazione alle richieste
            credential.initialize(userInfoRequest);

            // Esegui la richiesta e ottieni la risposta
            HttpResponse userInfoResponse = userInfoRequest.execute();

            // Parsa la risposta JSON per ottenere l'indirizzo email
            JsonObjectParser parser = new JsonObjectParser(jsonFactory);
            Map<String, Object> userInfo = parser.parseAndClose(userInfoResponse.getContent(), Charset.defaultCharset(), Map.class);

            return (String) userInfo.get("email");
        } catch (IOException e){
            //getEmailFromGoogle failed
            logger.severe(e.getMessage());
            return null;
        }
    }

}
