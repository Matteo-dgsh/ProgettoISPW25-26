package logic.utils;

import logic.model.MEvent;
import logic.model.MGroup;
import logic.model.MGroupMessage;
import logic.model.MUser;
import logic.model.Notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Datastore condiviso usato dalle implementazioni "in memory" dei DAO quando l'app
 * gira in modalita' Demo (PersistenceTypes.IN_MEMORY): nessun dato viene scritto
 * su database o su file, tutto vive solo in queste collezioni statiche e viene perso
 * alla chiusura dell'applicazione.
 */
public class InMemoryDataStore {

    private InMemoryDataStore() {
        //empty
    }

    public record Participation(int userID, int eventID) {
    }

    //USERS
    public static final Map<Integer, MUser> users = new HashMap<>();
    public static final Map<Integer, String> userStatus = new HashMap<>();
    private static final AtomicInteger userIdCounter = new AtomicInteger(1);

    //EVENTS
    public static final Map<Integer, MEvent> events = new HashMap<>();
    private static final AtomicInteger eventIdCounter = new AtomicInteger(1);

    //PARTECIPAZIONI (UserEvent)
    public static final List<Participation> participations = new ArrayList<>();

    //GRUPPI
    public static final Map<Integer, MGroup> groupsById = new HashMap<>();
    public static final Map<Integer, Set<Integer>> groupMembers = new HashMap<>();
    private static final AtomicInteger groupIdCounter = new AtomicInteger(1);

    //CHAT
    public static final Map<Integer, List<MGroupMessage>> chatMessages = new HashMap<>();

    //NOTIFICHE
    public static final List<Notification> notifications = new ArrayList<>();
    private static final AtomicInteger notificationIdCounter = new AtomicInteger(1);

    //PROVINCE/CITTA' (dati di riferimento statici, non generati dall'utente)
    public static final Map<String, List<String>> citiesByProvince = new HashMap<>();

    static {
        citiesByProvince.put("Roma", new ArrayList<>(List.of("Roma", "Ostia", "Fiumicino")));
        citiesByProvince.put("Milano", new ArrayList<>(List.of("Milano", "Sesto San Giovanni", "Monza")));
        citiesByProvince.put("Napoli", new ArrayList<>(List.of("Napoli", "Pozzuoli", "Casoria")));
        citiesByProvince.put("Torino", new ArrayList<>(List.of("Torino", "Moncalieri")));
        citiesByProvince.put("Firenze", new ArrayList<>(List.of("Firenze", "Prato")));
    }

    public static int nextUserID() {
        return userIdCounter.getAndIncrement();
    }

    public static int nextEventID() {
        return eventIdCounter.getAndIncrement();
    }

    public static int nextGroupID() {
        return groupIdCounter.getAndIncrement();
    }

    public static int nextNotificationID() {
        return notificationIdCounter.getAndIncrement();
    }

}
