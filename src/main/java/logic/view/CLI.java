package logic.view;

import com.google.api.client.auth.oauth2.Credential;

import logic.beans.*;
import logic.controllers.CFacade;
import logic.controllers.GoogleLogin;
import logic.exceptions.*;
import logic.utils.*;
import logic.utils.enums.NotificationTypes;
import logic.utils.enums.PersistenceTypes;
import logic.utils.enums.UserTypes;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CLI implements NotificationView, ChatView {
    private static final Logger logger = Logger.getLogger("NightAgent");

    private static final String GROUPNAME = "Group name: ";
    private static final String NOGROUP = "No group";
    private static final String YOUREVENTSUSER = "YourEventsUser";
    private static CFacade cFacade;
    private static BUserData bUserData;
    private static final String[] MUSIC_GENRES = {"Pop", "Rock", "Dance", "Electronic", "Techno", "Reggaeton", "Metal", "Disco", "Tech house", "House", "Rap", "Trap"};
    private static final List<String> commands = new ArrayList<>();
    private static final String[] COMMANDS_LIST = {"/commands", "/home", "/events", "/notifications", "/settings", "/quit"};
    private static final BufferedReader READER = new BufferedReader(new InputStreamReader(System.in));
    private static String filePath;
    private static final String DATE_PATTERN = "dd-MM-yyyy";
    private static final String CITY = "City: ";
    private static final String GOBACK = "1. Go back\n";
    private static final String COMMANDS_HELP = "Use /commands to view a list of commands which you can use to navigate into application pages";
    private static final String ASCII_LOGO = """

                ####-    +##+   ###-                 ###.          +++.        -####-                                               .++-
                #####-   +##+   ...      ...   ..    ###  ...     .###-..     .######.        ..   ..       ....       ..   ...    .+###...
                ###+##+  +##+   ###-   -#####+###-   ###+#####+  +########    ###.-###     -##########   .+######+.   +###+#####. .########.
                ###..##+ +##+   ###-  +###.  -###-   ###+. .###-   ###-      +##+  +##+   .###-  .###+  -###.  .###.  +###.  +###   -###
                ###- .##++##+   ###-  ###+    ###-   ###.   ###-   ###.     -###---+###-  +###    +##+  +##########+  +##+   -###   -###
                ###-  .#####+   ###-  -###. .+###-   ###.   ###-   ###-    .############. .###-  -###+  -###.     .   +##+   -###   -###.
                ###-    ####+   ###-   -+####-###-   ###.   ###-   -#####- ###+      +###  .+####++###   .+########   +##+   -###    +#####
                                     .###.                                                ###-      .....
                               +#######+.                                          .#######+-
                                 ....                                                ....
                    """;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static Integer timesClicked;
    private static Integer nParticipants;

    private static void initializeControllers() {
        CLI view = new CLI();
        cFacade = new CFacade();
        CFacade.setNotiGraphic(view);
        bUserData = new BUserData();
        commands.addAll(List.of(COMMANDS_LIST));
    }


    public static void spacer(int times) {
        for (int i = 0; i < times; i++) {
            System.out.println();
        }
    }

    private static void handleCommand(String command) {
        switch (command) {
            case "/commands":
                spacer(1);
                System.out.println("Command list:");
                for (String s : COMMANDS_LIST) {
                    System.out.println(s);
                }
                spacer(1);
                break;
            case "/home":
                loadHome();
                break;
            case "/events":
                loadEvents();
                break;
            case "/notifications":
                loadNotifications();
                break;
            case "/settings":
                loadSettings();
                break;
            case "/quit":
                signOut();
                break;
            default:
                break;
        }
        spacer(1);
    }

    private static void loadSettings() {
        acquireInputCycleSettings(LoggedUser.getUserType());
        spacer(2);
    }

    private static void acquireInputCycleSettings(UserTypes type){
        boolean valid = false;
        do {
            showSettings(type);
            String value = acquireInput();
            if (value != null) {
                checkCommands(value);

                valid = type.equals(UserTypes.USER) ? handleSettingsInputUser(value) : handleSettingsInputOrg(value);
            }
        } while (!valid);
    }

    private static boolean checkCommands(String value){
        //verifico comandi generali
        if (commands.contains(value)) {
            handleCommand(value);
            return true;
        }
        return false;
    }

    private static boolean handleSettingsInputOrg(String value) {
        switch (value) {
            case "a":
                showProfileInfo();
                return true;
            case "b":
                showContacts();
                return true;
            case "c":
                showHelp();
                return true;
            case "d":
                signOut();
                return true;
            default:
                return false;
        }
    }

    private static boolean handleSettingsInputUser(String value) {
        switch (value) {
            case "a":
                showProfileInfo();
                return true;

            case "b":
                changeCity();
                return true;

            case "c":
                showContacts();
                return true;

            case "d":
                showHelp();
                return true;

            case "e":
                signOut();
                return true;

            default:
                return false;
        }
    }

    private static String acquireInput() {
        String input;
        try {
            input = CLI.READER.readLine();
            return input;
        } catch (IOException e) {
            logger.severe("Error during input acquisition");
            return null;
        }
    }

    private static void showSettings(UserTypes type) {
        if (type.equals(UserTypes.USER)) {
            System.out.println("Settings:   \n" +
                    "a. Your profile\n" +
                    "b. Change city\n" +
                    "c. Contact us\n" +
                    "d. Help & FAQs\n" +
                    "e. Sign Out");
        } else if (type.equals(UserTypes.ORGANIZER)) {
            System.out.println("Settings:   \n" +
                    "a. Your profile\n" +
                    "b. Contact us\n" +
                    "c. Help & FAQs\n" +
                    "d. Sign Out");
        }
        spacer(1);
        System.out.println(COMMANDS_HELP);
    }


    private static void signOut() {
        spacer(3);
        System.out.println(ASCII_LOGO);
        spacer(2);
        System.out.println("Logging out from NightAgent. Goodbye! ;)");
        spacer(3);
        cFacade.signOut();
        loadApp();
    }

    private static void showHelp() {
        spacer(1);
        System.out.println("+++++++++++ Help & FAQs +++++++++++");
        spacer(1);
        System.out.println("Matteo Della Giovampaola");
        System.out.println("Emanuele Moscetta");
        spacer(1);

        //torno indietro
        loadSettings();
    }

    private static void showContacts() {
        spacer(1);
        System.out.println("+++++++++++ Contact us +++++++++++");
        spacer(1);
        System.out.println("Matteo Della Giovampaola");
        System.out.println("Emanuele Moscetta");
        spacer(1);

        //torno indietro
        loadSettings();
    }

    private static void showProfileInfo() {
        spacer(1);
        System.out.println("+++++++++++ Profile info +++++++++++");
        spacer(1);
        System.out.println("Username: " + LoggedUser.getUserName());
        System.out.println("First name: " + LoggedUser.getFirstName());
        System.out.println("Last name: " + LoggedUser.getLastName());
        System.out.println("Province: " + LoggedUser.getProvince());
        System.out.println(CITY + LoggedUser.getCity());
        System.out.println("Birth date: " + LoggedUser.getBirthDate());
        System.out.println("Gender: " + LoggedUser.getGender());
        System.out.println("User type: " + LoggedUser.getUserType());
        spacer(1);

        //go back
        loadSettings();
    }

    private static void changeCity() {
        //change city page
        String newProvince = null;
        String newCity = null;
        List<String> provinces = cFacade.getProvincesList();
        List<String> cities = null;
        boolean valid = false;

        spacer(1);
        System.out.println("+++++++++++ Change city +++++++++++");
        spacer(1);

        try {

            System.out.print("New province: ");
            do {
                String tempVal = READER.readLine();
                if (provinces.contains(tempVal)) {
                    newProvince = tempVal;
                    valid = true;
                }
            } while (!valid);
            valid = false;

            //faccio la retrieve delle città della nuova provincia scelta1
            cities = cFacade.getCitiesList(newProvince);

            System.out.print("New city: ");
            do {
                String tempVal = READER.readLine();
                if (cities.contains(tempVal)) {
                    newCity = tempVal;
                    valid = true;
                }
            } while (!valid);

            spacer(1);

            if (cFacade.changeUserCity(LoggedUser.getUserID(), newProvince, newCity) == 1) {
                System.out.println("City updated successfully");
            } else {
                System.out.println("City update failed. Retry...");
            }
        } catch (IOException e) {
            logger.severe("IOException in ChangeCity");
        }

        //go back
        loadSettings();
    }

    private static void loadHome() {
        spacer(3);

        System.out.println("Welcome to NightAgent!");
        spacer(1);
        System.out.println(COMMANDS_HELP);
        spacer(1);

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            List<BEvent> eventsList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCHomeUser");
            List<BEvent> futureEvents = fetchFutureEvents(eventsList);
            List<BGroup> groupsList = cFacade.retrieveGroups(futureEvents);

            if (!eventsList.isEmpty()) {
                System.out.println("Events in your city. \nWrite event name to show event info!");
                printFutureEvents(futureEvents, groupsList);
                acquireInputCycleHome(LoggedUser.getUserType(), futureEvents);
            } else {
                System.out.println("No events in your city");
                System.out.println("Please wait new events to be added in your city. Use /commands to navigate!");
                spacer(1);

                waitCommands();
            }
            spacer(3);
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println("You can add a new event or navigate in other menus:");
            System.out.println("1. Add new event");
            acquireInputCycleHome(LoggedUser.getUserType(), null);

            spacer(3);
        }
    }

    private static void acquireInputCycleHome(UserTypes type, List<BEvent> futureEvents){
        boolean valid = false;
        do {
            String value = acquireInput();
            if (value != null) {
                checkCommands(value);

                switch (type){
                    case USER:
                        valid = handleEventNameInput(value, futureEvents);
                        break;
                    case ORGANIZER:
                        if (value.equals("1")) {
                            addEventPage();
                            valid = true;
                        }
                        break;
                    default:
                        break;
                }
            }
        } while (!valid);
    }

    private static boolean handleEventNameInput(String value, List<BEvent> futureEvents) {
        for (BEvent bEvent : futureEvents) {
            if (bEvent.getEventName().equals(value)) {
                showEventInfo(bEvent,"Home", false);
                return true;
            }
        }
        return false;
    }

    private static void printFutureEvents(List<BEvent> futureEvents, List<BGroup> groupsList) {
        for (int i = 0; i < futureEvents.size(); i++) {
            System.out.println(i + 1 + ". " + futureEvents.get(i).getEventName() + " | " + (groupsList.get(i).getGroupID() != null ? (GROUPNAME + groupsList.get(i).getGroupName()) : NOGROUP));
        }
    }

    private static List<BEvent> fetchFutureEvents(List<BEvent> eventList) {
        List<BEvent> futureEvents = new ArrayList<>();
        for (BEvent event : eventList) {
            String eventDateString = event.getEventDate();
            LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));
            if (LocalDate.now(ZoneId.systemDefault()).minusDays(1).isBefore(date)) {
                futureEvents.add(event);
            }
        }
        return futureEvents;
    }

    private static void addEventPage() {
        BEvent bEventBean = new BEvent();
        modifyEventBean(bEventBean, "Add");

        try {
            if (cFacade.addEvent(bEventBean)) {
                System.out.println("Event added successfully");
            } else {
                System.out.println("Event adding failed. Returning home...");
            }
        } catch (EventAlreadyAdded e) {
            logger.warning("Event already added with this name: " + e.getEventName());
        }

        //torno alla home org
        loadHome();
    }

    private static void modifyEventBean(BEvent eventBean, String action){
        System.out.println(action + " event\n");

        if(action.equals("Edit")) {
            System.out.println("Press enter to maintain previous value!");
        }
        List<String> provinces = cFacade.getProvincesList();

        if(action.equals("Add")){
            addEventBean(eventBean, provinces);
        } else {
            editEventBean(eventBean, provinces);
        }
    }

    private static void addEventBean(BEvent eventBean, List<String> provinces){
        try {
            System.out.println("Event name:");
            eventBean.setEventName(acquireInput());

            System.out.println("Event province:");
            acquireProvinceInputAndModifyBean(provinces, eventBean, false);

            List<String> cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: ");
            acquireCityInputAndModifyBean(cities, eventBean, false);

            System.out.println("Event date:");
            acquireDateAndModifyBean(eventBean, false);

            System.out.println("Event address:");
            acquireAddressAndSetBean(eventBean);

            System.out.println("Choose music genre from list:");
            printMusicGenres();
            acquireMusicGenreAndModifyBean(eventBean, false);

            System.out.println("Event time: HH:mm");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            acquireTimeAndSetBean(eventBean, timeFormat);

            //imposto immagine per l'evento
            setEventImage(eventBean);

            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
            eventBean.setEventPicPath(filePath);

        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
    }

    private static void editEventBean(BEvent eventBean, List<String> provinces){
        try {
            String newVal;
            System.out.println("Event name: (previous: " + eventBean.getEventName() + ")");
            newVal = acquireInput();
            if (newVal != null && !newVal.isEmpty()) {
                eventBean.setEventName(newVal);
            }

            System.out.println("Event province: (previous: " + eventBean.getEventProvince() + ")");
            acquireProvinceInputAndModifyBean(provinces, eventBean, true);

            List<String> cities = cFacade.getCitiesList(eventBean.getEventProvince());
            System.out.println("Event city: (previous: " + eventBean.getEventCity() + ")");
            acquireCityInputAndModifyBean(cities, eventBean, true);

            System.out.println("Event date: (previous: " + eventBean.getEventDate() + ")");
            acquireDateAndModifyBean(eventBean, true);

            System.out.println("Event address: (previous: " + eventBean.getEventAddress() + ")");
            newVal = acquireInput();
            if (newVal != null && !newVal.isEmpty()) {
                eventBean.setEventAddress(newVal);
            }

            System.out.println("Choose music genre from list: (previous: " + eventBean.getEventMusicGenre() + ")");
            printMusicGenres();
            acquireMusicGenreAndModifyBean(eventBean, true);

            System.out.println("Event time: HH:mm (previous: " + eventBean.getEventTime() + ")");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            acquireTimeAndEditBean(eventBean, timeFormat);

            spacer(1);

            System.out.print("Do you want to change event image? Write 'y' or 'Y if you want to update event image");
            String decision = READER.readLine();
            if (decision.equalsIgnoreCase("y")) {
                setEventImage(eventBean);
            }

            eventBean.setEventOrganizer(LoggedUser.getUserName());
            eventBean.setEventOrganizerID(LoggedUser.getUserID());
        } catch (IOException e) {
            logger.severe(() -> "IOException: " + e.getMessage());
        } catch (InvalidValueException | TextTooLongException e) {
            logger.warning(e.getMessage());
        }
    }

    private static void setEventImage(BEvent eventBean) throws InvalidValueException {
        //prendi file immagine - pickFileData() cicla finche' non ottiene un file valido, quindi qui
        //fileData e filePath sono sempre valorizzati (mai null)
        byte[] fileData = pickFileData();
        eventBean.setEventPicData(fileData);
        eventBean.setEventPicPath(filePath);
    }

    private static void acquireTimeAndSetBean(BEvent eventBean, SimpleDateFormat timeFormat) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            valid = timeParseAndCheck(timeFormat, eventBean, val);
        }
    }

    private static void acquireTimeAndEditBean(BEvent eventBean, SimpleDateFormat timeFormat) {
        boolean valid = false;
        boolean toEdit = true;
        while (!valid) {
            String val = acquireInput();
            if (val != null && val.isEmpty()) {
                toEdit = false;
                valid = true;
            }
            if (toEdit) {
                valid = timeParseAndCheck(timeFormat, eventBean, val);
            }
        }
    }

    private static boolean timeParseAndCheck(SimpleDateFormat timeFormat, BEvent eventBean, String val){
        if (val == null) {
            return false;
        }
        try {
            timeFormat.parse(val);
        } catch (ParseException e) {
            logger.severe("Invalid time format.");
            return false;
        }
        //qua ci arriva solo se il time viene parsato correttamente
        String[] parts = val.split(":");
        if (parts.length != 2) {
            logger.severe("Invalid time format.");
            return false;
        }
        return setTimeToBean(eventBean, parts[0], parts[1]);
    }

    private static boolean setTimeToBean(BEvent eventBean, String hours, String minutes) {
        try {
            eventBean.setEventTime(hours, minutes);
            return true;
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
            return false;
        }
    }

    private static void acquireMusicGenreAndModifyBean(BEvent eventBean, boolean isEditing){
        boolean valid = false;
        boolean found = false;
        while (!valid) {
            String val = acquireInput();
            if(isEditing){
                if (val != null && val.isEmpty()) {
                    break;
                }
                found = checkIfGenreIsValid(val);
            } else {
                if (val != null) {
                    found = checkIfGenreIsValid(val);
                }
            }
            if (found) {
                valid = setGenreToBean(val, eventBean);
            }
        }
    }

    private static boolean setGenreToBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventMusicGenre(val);
            return true;
        } catch (InvalidValueException e) {
            logger.info(e.getMessage());
            return false;
        }
    }

    private static boolean checkIfGenreIsValid(String val) {
        for (String musicGenre : MUSIC_GENRES) {
            if (musicGenre.equals(val)) {
                return true;
            }
        }
        return false;
    }

    private static void printMusicGenres() {
        for (int i = 0; i < MUSIC_GENRES.length; i++) {
            System.out.println(i + 1 + ". " + MUSIC_GENRES[i]);
        }
    }

    private static void acquireAddressAndSetBean(BEvent eventBean) {
        try {
            eventBean.setEventAddress(acquireInput());
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void acquireDateAndModifyBean(BEvent eventBean, boolean isEditing) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        boolean valid = false;
        boolean toEdit = true;
        while (!valid) {
            String val = acquireInput();
            if(isEditing) {
                if (val != null && val.isEmpty()) {
                    valid = true;
                    toEdit = false;
                }
                if (toEdit && convertDateAndSetEventBean(val, formatter, eventBean)) {
                    valid = true;
                }
            } else {
                valid = convertDateAndSetEventBean(val, formatter, eventBean);
            }
        }
    }

    private static boolean convertDateAndSetEventBean(String val, DateTimeFormatter formatter, BEvent eventBean) {
        try {
            // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
            LocalDate eventDate = LocalDate.parse(val, formatter);
            eventBean.setEventDate(eventDate.format(formatter));
            return true;
        } catch (Exception e) {
            // Gestire il caso in cui l'input non sia nel formato corretto
            logger.severe("Invalid date format.");
            return false;
        }
    }

    private static void acquireCityInputAndModifyBean(List<String> cities, BEvent eventBean, boolean isEditing) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if(isEditing && val != null && val.isEmpty()){
                break;
            }
            if (cities.contains(val)) {
                setCityInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static void setCityInBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventCity(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void acquireProvinceInputAndModifyBean(List<String> provinces, BEvent eventBean, boolean isEditing) {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if(isEditing && val != null && val.isEmpty()) {
                break;
            }
            if (provinces.contains(val)) {
                setProvinceInBean(val, eventBean);
                valid = true;
            }
        }
    }

    private static void setProvinceInBean(String val, BEvent eventBean) {
        try {
            eventBean.setEventProvince(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    //Niente piu' JFileChooser (finestra grafica Swing): in un'interfaccia a riga di comando puo' non
    //comparire mai a video (dipende dall'ambiente grafico/toolkit) senza dare nessun errore, dando
    //l'impressione che il programma sia bloccato. Si chiede direttamente il percorso del file come testo.
    private static byte[] pickFileData() {
        System.out.println("Event image: write the full file path (e.g. C:\\images\\poster.jpg)");
        byte[] fileData = null;
        boolean valid = false;
        while (!valid) {
            String path = acquireInput();
            if (path == null || path.isBlank()) {
                System.out.println("Invalid path, please retry:");
            } else {
                File selectedFile = new File(path.trim());
                if (!selectedFile.isFile()) {
                    System.out.println("File not found, please retry:");
                } else {
                    try {
                        fileData = Files.readAllBytes(selectedFile.toPath());
                        filePath = selectedFile.getAbsolutePath();
                        System.out.println("File path: " + filePath);
                        System.out.println("File size: " + fileData.length + " bytes");
                        valid = true;
                    } catch (IOException e) {
                        logger.severe(e.getMessage());
                        System.out.println("Could not read the file, please retry:");
                    }
                }
            }
        }
        return fileData;
    }

    private static void showEventInfo(BEvent bEvent, String lastPage, boolean isPassed) {
        printEventInfo(bEvent);
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            spacer(1);
            boolean isEventParticipated = cFacade.checkPreviousEventParticipation(bEvent);
            if (isEventParticipated) {
                System.out.println(GOBACK +
                        "2. Remove event participation");
            } else {
                System.out.println(GOBACK +
                        "2. Plan event participation");
            }

            int ret = 0;
            if(lastPage.equals(YOUREVENTSUSER)) {
                //stampa opzioni gruppo solo se ti trovavi nella pagina your events
                ret = handleGroupOptions(bEvent);
            }
            cycleForUserInputShowEvent(isEventParticipated, lastPage, bEvent, ret);

            //ritorno sulla pagina degli eventi
            loadEvents();
        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER) && !isPassed) {
            spacer(1);
            System.out.println(GOBACK +
                    "2. Edit event\n" +
                    "3. Delete event");
            cycleForOrgInputFutureEvent(bEvent);

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println(GOBACK +
                    "2. Show analytics");
            cycleForOrgInputPastEvent(bEvent);
        }
    }

    private static int handleGroupOptions(BEvent eventBean) {
        BGroup groupBean = cFacade.getGroupByEventID(eventBean.getEventID());
        if(groupBean.getGroupID() == null){
            System.out.println("3. Create group");
            return 1;
        } else if(!cFacade.userInGroup(LoggedUser.getUserID(), groupBean.getGroupID())){
            System.out.println("3. Join group");
            return 2;
        } else {
            System.out.println("3. Group chat");
            return 3;
        }
    }

    private static void cycleForOrgInputPastEvent(BEvent bEvent) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null) {
                checkCommands(value);

                valid = handlePastEventOrgInput(value, bEvent);

            }

        }
    }

    private static boolean handlePastEventOrgInput(String value, BEvent bEvent) {
        if (value.equals("1")){
            loadEvents();
            return true;
        }else if(value.equals("2")){
            //shows past event analytics
            printEventAnalytics(bEvent);
            return true;
        }else{
            return false;
        }
    }

    private static void cycleForOrgInputFutureEvent(BEvent bEvent) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null) {
                valid = handleShowEventInfoOrgInput(value, bEvent);
            }
        }
    }

    private static void cycleForUserInputShowEvent(boolean isEventParticipated, String lastPage, BEvent bEvent, int ret) {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null) {
                valid = handleShowEventInfoUserInput(value, isEventParticipated, lastPage, bEvent, ret);
            }
        }
    }

    private static boolean handleShowEventInfoOrgInput(String value, BEvent bEvent) {
        switch (value) {
            case "1":
                loadEvents();
                return true;
            case "2":
                //opens procedure to edit event
                modifyEventBean(bEvent, "Edit");
                if (cFacade.editEvent(bEvent)) {
                    System.out.println("Event edited successfully!");
                    loadEvents();
                    return true;
                } else {
                    logger.severe("Event edit failed!");
                    return false;
                }
            case "3":
                spacer(1);
                if (deleteEvent(bEvent.getEventID())) {
                    System.out.println("Event deleted successfully!");
                } else {
                    logger.severe("Event deletion failed!");
                }
                //sono organizer torno per forza nella pagina degli eventi creati
                loadEvents();
                return true;
            default:
                return false;
        }

    }


    private static boolean handleShowEventInfoUserInput(String value, boolean isEventParticipated, String lastPage, BEvent bEvent, int ret) {
        if (value.equals("1")) {
            showCorrectBackPageUser(lastPage);
            return true;
        } else if (value.equals("2") && isEventParticipated) {
            if (cFacade.removeEventParticipation(bEvent)) {
                System.out.println("Event participation removed successfully!");
                loadHome();
                return true;
            } else {
                logger.severe("Event participation removal failed!");
                return false;
            }
        } else if (value.equals("2")) {
            if (participateToEvent(bEvent)) {
                System.out.println("Event participation successful!");
                loadHome();
                return true;
            } else {
                logger.severe("Event participation failed!");
                return false;
            }
        } else if (ret != 0 && value.equals("3")){
            switch (ret){
                case 1:
                    //create group
                    createGroup(bEvent);
                    break;
                case 2:
                    //join group
                    joinGroup(bEvent);
                    break;
                case 3:
                    //group chat
                    openGroupChat(bEvent);
                    break;
                default:
                    break;
            }
            return true;
        }
        return false;
    }

    private static void showCorrectBackPageUser(String lastPage) {
        if (lastPage.equals("Home")) {
            //torno nella home
            loadHome();
        } else if (lastPage.equals(YOUREVENTSUSER)) {
            loadEvents();
        }
    }

    private static boolean participateToEvent(BEvent bEvent) {
        try {
            return cFacade.participateToEvent(bEvent);
        } catch (EventAlreadyDeleted e) {
            logger.severe(e.getMessage());
            return false;
        }
    }


    private static void printEventInfo(BEvent bEvent) {
        System.out.println("Event info:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println(CITY + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());
    }

    private static boolean deleteEvent(int eventID) {
        System.out.println("Are you sure you want to delete this event?\nWrite 'y' to confirm or anything else to cancel.");
        spacer(1);
        try {
            String val = READER.readLine();
            if (val != null && val.equalsIgnoreCase("y")) {
                if (!cFacade.deleteEvent(eventID)) {
                    logger.severe("Failed to delete event. Retry...");
                    return false;
                }
            } else {
                return false;
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
        return true;
    }

    private static void loadNotifications() {
        spacer(1);
        System.out.println("Notifications");


        List<BNotification> notifications = cFacade.retrieveNotifications(LoggedUser.getUserID());
        if (!notifications.isEmpty()) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getMessageType() == NotificationTypes.EVENT_ADDED) {
                    System.out.println(i + 1 + ". New event called " + cFacade.getEventNameByEventID(notifications.get(i).getEventID()) + " in your city!");
                } else if (notifications.get(i).getMessageType() == NotificationTypes.USER_EVENT_PARTICIPATION) {
                    System.out.println(i + 1 + ". New user " + cFacade.getUsernameByID(notifications.get(i).getNotifierID()) + " participating to your event " + cFacade.getEventNameByEventID(notifications.get(i).getEventID()));
                }
            }

            spacer(1);
            System.out.println("Press 1 to delete a notification or write /commands to view all possibile commands.");
            spacer(1);
        } else {
            System.out.println("No notifications to show.");
            System.out.println("Write /commands to view all possibile commands.");
        }

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //verifico comandi generali
                checkCommands(value);

                //cancello la notifica
                if (value.equals("1")) {
                    valid = true;
                    deleteNotification(notifications);

                    //ricarico le notifiche aggiornate
                    loadNotifications();
                }
            } catch (IOException e) {
                logger.severe("IOException in loadNotifications");
            }
        } while (!valid);
    }

    private static void deleteNotification(List<BNotification> notifications) {
        boolean valid = false;

        spacer(1);
        System.out.print("Choose the number of the notification you want to delete: ");

        try {
            do {
                String value = READER.readLine();

                // Converte la stringa in un numero intero
                int index = getNotificationIndex(value);
                if (index == -1) {
                    continue;
                }
                // Verifica se l'indice è valido
                if (index >= 1 && index <= notifications.size()) {
                    cFacade.deleteNotification(notifications.get(index - 1).getNotificationID(), notifications, index - 1);
                    valid = true;

                    //ricarica notifications
                    loadNotifications();
                } else {
                    logger.severe(() -> "Index range non valid. If must be between 1 and " + (notifications.size()));
                }
            } while (!valid);
        } catch (IOException e) {
            logger.severe("IOException in deleteNotification");
        }
    }

    private static int getNotificationIndex(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.severe("Index format not valid...");
            return -1;
        }
    }

    private static void loadEvents() {
        spacer(3);

        System.out.println("Your events");
        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            System.out.println("1. Events with planned participation\n" + "2. Past events");
            cycleForUserLoadEvents();

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            System.out.println("1. Events created by you\n" + "2. Past events\n" + "3. Analytics");
            cycleForOrgLoadEvents();

        }
    }

    private static void cycleForOrgLoadEvents() {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null && value.equals("1")) {
                valid = true;
                showEvents(false);
            } else if (value != null && value.equals("2")) {
                valid = true;
                showEvents(true);
            } else if (value != null && value.equals("3")) {
                //apro la pagina analytics con la lista degli eventi passati
                showAnalytics(showEvents(true));
            }
        }
    }

    private static void cycleForUserLoadEvents() {
        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }
            if (value != null && value.equals("1")) {
                valid = true;
                showEvents(false);
            } else if (value != null && value.equals("2")) {
                valid = true;
                showEvents(true);
            }
        }
    }

    private static void showAnalytics(List<BEvent> pastEvents) {
        spacer(3);
        System.out.println("Analytics");
        spacer(1);

        //le analytics si vedono solo sugli eventi gia' passati/conclusi (ha senso solo li' un dato
        //definitivo di partecipazione). Se la lista e' vuota (es. hai creato un evento ma la sua data
        //e' futura, non ancora conclusa) usciamo subito invece di restare in un ciclo che non potrebbe
        //mai essere soddisfatto, dato che non c'e' nessun nome evento che possa mai fare match.
        if (pastEvents.isEmpty()) {
            System.out.println("No past events available for analytics yet.");
            System.out.println("Analytics are shown only for events whose date has already passed.");
            spacer(1);
            waitCommands();
            return;
        }

        printEventsList(pastEvents, null,true);
        spacer(1);
        System.out.println("Write event name to view its analytics:");
        System.out.println(COMMANDS_HELP);
        spacer(1);

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //vedo eventuali comandi generali
                checkCommands(value);

                for (BEvent bEvent : pastEvents) {
                    if (bEvent.getEventName().equals(value)) {
                        valid = true;
                        printEventAnalytics(bEvent);
                    }
                }
            } catch (IOException e) {
                logger.severe("IOException in showAnalytics");
            }
        } while (!valid);
        spacer(1);

    }

    private static void printEventAnalytics(BEvent bEvent) {
        System.out.println("Event analytics:");
        System.out.println("Name: " + bEvent.getEventName());
        System.out.println(CITY + bEvent.getEventCity());
        System.out.println("Music genre: " + bEvent.getEventMusicGenre());
        System.out.println("Organizer: " + bEvent.getEventOrganizer());
        System.out.println("Time: " + bEvent.getEventTime());
        System.out.println("Date: " + bEvent.getEventDate());
        System.out.println("Address: " + bEvent.getEventAddress());

        int participations = cFacade.retrieveParticipationsToEvent(bEvent.getEventID());
        if (timesClicked == null) {
            timesClicked = RANDOM.nextInt(5000 - participations + 1) + participations;
            nParticipants = RANDOM.nextInt(participations + 1);
        }

        System.out.println("Participants: " + participations);
        System.out.println("Planned participations: " + nParticipants);
        System.out.println("Times clicked: " + timesClicked);

        spacer(1);
        System.out.println(GOBACK + "2. Export analytics as TXT file");

        boolean valid = false;
        do {
            try {
                String value = READER.readLine();

                //verifico comandi generali
                checkCommands(value);


                if (value != null && value.equals("1")) {
                    loadEvents();
                    valid = true;
                } else if (value != null && value.equals("2")) {
                    exportAnalyticsFile(bEvent, participations);
                    valid = true;
                }
            } catch (IOException e) {
                logger.severe("IOException in analytics input acquisition");
            }
        } while (!valid);
    }

    private static void exportAnalyticsFile(BEvent bEvent, Integer participations) {
        //popolo il bean con i dati dell'analisi presi dal bean Event che è superclass di bean Analysis
        BAnalytics analytics = null;
        try {
            analytics = new BAnalytics(bEvent);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe("Error during analytics bean setup");
        }
        if (analytics != null) {
            analytics.setTimesClicked(timesClicked);
            analytics.setParticipants(nParticipants);
            analytics.setPlannedParticipations(participations);
        }

        if(cFacade.exportAnalyticsFile(analytics)){
            logger.info("Analytics file exported as a .txt successfully!\nYou can find it into exportedAnalytics folder.");
        } else {
            logger.severe("Analytics file export failed!\n Retry...");
        }
    }

    private static List<BEvent> showEvents(boolean isPassed) {
        spacer(3);
        List<BEvent> eventList = new ArrayList<>();

        if (LoggedUser.getUserType().equals(UserTypes.USER)) {
            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsUser");
            eventList = populateEventList(isPassed, tempList); //isPassed = true -> show past events; isPassed = false -> show upcoming events
            checksOnEventListUser(eventList, isPassed);

        } else if (LoggedUser.getUserType().equals(UserTypes.ORGANIZER)) {
            List<BEvent> tempList = cFacade.retrieveEvents(LoggedUser.getUserType(), "GCYourEventsOrg");
            eventList = populateEventList(isPassed, tempList);
            checksOnEventListOrg(eventList, isPassed);

        }
        spacer(3);

        return eventList;
    }

    private static void checksOnEventListOrg(List<BEvent> eventList, boolean isPassed) {
        if (!eventList.isEmpty()) {
            if (!isPassed) {
                System.out.println("Your organized events. \nWrite event name to show event info, edit or delete it!");
            } else {
                System.out.println("Your past events. \nWrite event name to show event info!");
            }
            eventsPrintAndAcquireInput(eventList, null, isPassed);

        } else {
            System.out.println("No organized events!");
            System.out.println("To add a new event go to /home and write 1 to add event.\n Then follow instructions to finish the procedure.");
            spacer(1);

            waitCommands();
        }
    }

    private static void checksOnEventListUser(List<BEvent> eventList, boolean isPassed) {
        List<BGroup> groupsList = null;
        if (!eventList.isEmpty()) {
            if (!isPassed) {
                System.out.println("Your events. \nWrite event name to show event info!");
                 groupsList = cFacade.retrieveGroups(eventList);
            } else {
                System.out.println("Your past events. \nWrite event name to show event info!");
            }
            eventsPrintAndAcquireInput(eventList, groupsList, isPassed);
        } else {
            System.out.println("No planned events!");
            System.out.println("To plan participation to an event in your city go to /home and write the name of an available event.\n Then follow instructions to Plan event participation.");
            spacer(1);

            waitCommands();
        }
    }

    private static void eventsPrintAndAcquireInput(List<BEvent> eventList, List<BGroup> groupsList, boolean isPassed) {
        printEventsList(eventList, groupsList, isPassed);
        spacer(1);
        System.out.println(COMMANDS_HELP);
        spacer(1);

        boolean valid = false;
        while (!valid) {
            String value = acquireInput();
            if (value != null && commands.contains(value)) {
                handleCommand(value);
            }

            valid = checkIfEventNameEntered(value, eventList, isPassed);
        }
    }

    private static boolean checkIfEventNameEntered(String value, List<BEvent> eventList, boolean isPassed) {
        for (BEvent bEvent : eventList) {
            if (bEvent.getEventName().equals(value)) {
                showEventInfo(bEvent, YOUREVENTSUSER, isPassed);
                return true;
            }
        }
        return false;
    }

    private static List<BEvent> populateEventList(boolean isPassed, List<BEvent> tempList) {
        List<BEvent> eventList = new ArrayList<>();
        if (!isPassed) {
            for (BEvent bEvent : tempList) {
                String eventDateString = bEvent.getEventDate();
                LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));

                if (LocalDate.now(ZoneId.systemDefault()).minusDays(1).isBefore(date)) {
                    //aggiungo alla lista solo gli eventi futuri (today is before event date)
                    eventList.add(bEvent);
                }
            }
        } else {
            for (BEvent bEvent : tempList) {
                String eventDateString = bEvent.getEventDate();
                LocalDate date = LocalDate.parse(eventDateString, DateTimeFormatter.ofPattern(DATE_PATTERN));

                if (!LocalDate.now(ZoneId.systemDefault()).minusDays(1).isBefore(date)) {
                    //aggiungo alla lista solo gli eventi passati (today is NOT before event date)
                    eventList.add(bEvent);
                }
            }
        }
        return eventList;
    }

    private static void printEventsList(List<BEvent> eventList, List<BGroup> groupsList, boolean isPassed) {
        if(!isPassed && LoggedUser.getUserType().equals(UserTypes.USER)){
            //solo l'utente può vedere i gruppi
            for (int i = 0; i < eventList.size(); i++) {
                System.out.println(i + 1 + ". " + eventList.get(i).getEventName() + " | " + groupOptions(groupsList.get(i)));
            }
        } else {
            //showAnalytics, pastEvents (user o organizer), futureEvents organizer
            for (int i = 0; i < eventList.size(); i++) {
                System.out.println(i + 1 + ". " + eventList.get(i).getEventName());
            }
        }
    }

    private static String groupOptions(BGroup bGroup){
        String groupName = (bGroup.getGroupID() != null) ? bGroup.getGroupName() : NOGROUP;
        String groupOption;
        if(!groupName.equals(NOGROUP)){
            if(cFacade.userInGroup(LoggedUser.getUserID(), bGroup.getGroupID())){
                groupOption = "Group chat";
            } else {
                groupOption = "Join group";
            }
            return GROUPNAME + groupName + " | " + groupOption;
        }
        return groupName;
    }

    private static void createGroup(BEvent eventBean){
        spacer(1);
        System.out.print("Insert group name: ");
        String value = acquireInput();
        spacer(1);

        try {
            if(cFacade.createGroup(value, eventBean.getEventID())){
                logger.info("Successfully created and joined new group");
            } else {
                logger.severe("Error while joining group");
            }
        } catch (GroupAlreadyCreated e) {
            logger.severe(e.getMessage());
        } catch (InvalidGroupName e) {
            logger.warning(e.getMessage());
        }
    }

    private static void joinGroup(BEvent eventBean) {
        //JOIN GROUP
        BGroup groupBean = cFacade.getGroupByEventID(eventBean.getEventID());

        if(!cFacade.joinGroup(groupBean.getGroupID())) {
            logger.severe("Error while joining group");
        }
    }

    private static void openGroupChat(BEvent eventBean){
        //imposto la chat graphic view
        setChatGraphic();

        //trovo il groupID dal beanEvent
        BGroup groupBean = cFacade.getGroupByEventID(eventBean.getEventID());

        System.out.println("+++++++++ Group chat +++++++++++");
        System.out.println(GROUPNAME + cFacade.getGroupNameByGroupID(groupBean.getGroupID()));


        List<BMessage> messages = cFacade.retrieveGroupChat(groupBean.getGroupID());
        for (BMessage message : messages) {
            System.out.println(cFacade.getUsernameByID(message.getSenderID()) + ": " + message.getMessage());
        }

        spacer(1);
        System.out.println("You can send messages to the group!");
        spacer(1);
        System.out.println(GOBACK + "2. Leave group\n");
        spacer(1);

        boolean exit = false;
        do {
            String message = acquireInput();

            if (message != null && message.equals("1")) {
                exit = true;
            } else if (message != null && message.equals("2")){
                if(cFacade.leaveGroup(groupBean.getGroupID())){
                    logger.info("Group left successfully!");
                    exit = true;
                } else {
                    logger.warning("Error while leaving group");
                }
            } else {
                BMessage messageBean = new BMessage(LoggedUser.getUserID(), message, groupBean.getGroupID());
                if(!cFacade.sendMessageToGroup(messageBean)){
                    logger.warning("Error while sending message");
                    exit = true;
                }
            }
        } while (!exit);

        //termina la funzione torno in YourEventsUser
        cFacade.setChatGraphic(null);
    }

    private static void setChatGraphic() {
        CLI view = new CLI();
        cFacade.setChatGraphic(view);
    }


    private static void waitCommands() {
        boolean waiting = true;
        try {
            while (waiting) {
                String value = READER.readLine();
                if(checkCommands(value)){
                    waiting = false;
                }
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    public static void loadApp() {

        boolean valid = false;
        while (!valid) {
            spacer(3);
            System.out.println(ASCII_LOGO);
            System.out.println("Login or register");
            System.out.println("1. Login");
            System.out.println("2. Login with Google");
            System.out.println("3. Register new account");

            String inputLine = acquireInput();
            valid = handleFrontPageInput(inputLine);
            //NB: niente READER.close() qui - e' uno stream condiviso (static final) usato per tutta la
            //durata della sessione CLI (login/registrazione, home, settings, eventi, ecc.). Chiuderlo qui
            //rompeva ogni lettura successiva (System.out spammava "Error during input acquisition" appena
            //si tornava a mostrare questo menu, es. dopo un tentativo fallito o un logout).
        }
    }


    private static boolean handleFrontPageInput(String inputLine) {
        if (inputLine != null && inputLine.equals("1")) {
            return loginUsernameAndPassword();
        } else if (inputLine != null && inputLine.equals("2")) {
            return googleLogin();
        } else if (inputLine != null && inputLine.equals("3")) {
            return registerUserStandard();
        }
        return false;
    }

    private static boolean registerUserStandard() {
        try {
            if (registerUser(false) == 1) {
                spacer(1);
                //la registrazione da sola non crea una sessione (LoggedUser resta vuoto): si torna al
                //menu di login/registrazione per far accedere l'utente con le credenziali appena create,
                //stessa logica gia' usata per la registrazione via Google poco sotto
                System.out.println("Registration complete. Please login with your new account.");
                spacer(1);
                loadApp();
                return true;
            }
        } catch (RuntimeException e) {
            logger.severe(e.getMessage());
            return false;
        }
        return false;
    }

    private static boolean googleLogin() {
        System.out.println("Opening google login page in your browser...");
        spacer(1);

        try {
            //apre il browser e attende in modo sincrono l'esito dell'autenticazione OAuth
            //(flusso "loopback", vedi GoogleLogin.performGoogleLogin)
            Credential credential = GoogleLogin.performGoogleLogin();
            if (credential == null) {
                System.out.println("Google login is not configured correctly or was cancelled.");
                return false;
            }
            String email = GoogleLogin.getGoogleAccountEmail(credential);
            if (email == null) {
                System.out.println("Could not retrieve your Google account email.");
                return false;
            }
            bUserData = new BUserData(email);

            //login user
            int res = cFacade.loginUser(bUserData, true);
            spacer(1);
            if (res == 1) {
                System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                loadHome();
                return true;
            } else {
                System.out.println("User not registered with Google. \nYou will be redirected to registration page.");
                if (registerUser(true) == 1) {
                    System.out.println("Successfully registered with Google");
                }
                //ricarico tutta l'interfaccia
                loadApp();
                return true;
            }
        } catch (InvalidTokenValue e) {
            logger.info("Invalid token value...");
            //ricarico l'interfaccia
            loadApp();
        }
        return false;
    }

    private static boolean loginUsernameAndPassword() {
        while (true) {
            System.out.println("Insert username: ");
            setUsernameToBean(acquireInput());

            System.out.println("Insert password: ");
            setPasswordToBean(acquireInput());
            int res = cFacade.loginUser(bUserData, false);
            spacer(1);
            if (res == 1) {
                System.out.println("Logged in successfully as " + LoggedUser.getUserType());
                loadHome();
                return true;
            } else {
                System.out.println("Wrong credentials. Retry...");
            }
        }
    }

    private static void setPasswordToBean(String s) {
        try {
            bUserData.setPassword(s);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void setUsernameToBean(String s) {
        try {
            bUserData.setUsername(s);
        } catch (InvalidValueException | TextTooLongException e) {
            logger.severe(e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeControllers();

        System.out.print("Choose the persistence logic!\n Type 'JDBC' or 'FileSystem' -> ");
        try {
            boolean valid = false;

            do {
                String val = READER.readLine();

                if (val.equalsIgnoreCase("jdbc") || val.equalsIgnoreCase("filesystem")) {
                    valid = true;
                    if (val.equalsIgnoreCase("jdbc")) {
                        PersistenceClass.setPersistenceType(PersistenceTypes.JDBC);
                    } else {
                        PersistenceClass.setPersistenceType(PersistenceTypes.FILE_SYSTEM);
                    }
                } else {
                    System.out.println("Invalid option. Please enter 'JDBC' or 'FileSystem'.");
                }
            } while (!valid);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }

        loadApp();
    }

    private static int registerUser(boolean isGoogleAuth) {
        try {


            spacer(1);
            System.out.println("Registration procedure");
            spacer(1);

            populateRegistrationBean(isGoogleAuth);


            System.out.println("First name: ");
            bUserData.setFirstName(READER.readLine());
            System.out.println("Last name: ");
            bUserData.setLastName(READER.readLine());
            System.out.println("Gender: ");
            System.out.println("a. Male\n" + "b. Female\n" + "c. Other");
            chooseGenderCycle();

            System.out.println("Province: ");
            List<String> provinces = cFacade.getProvincesList();
            boolean valid = false;
            do {
                String val = READER.readLine();
                if (provinces.contains(val)) {
                    bUserData.setProvince(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;

            System.out.println(CITY);
            List<String> cities = cFacade.getCitiesList(bUserData.getProvince());
            do {
                String val = READER.readLine();
                if (cities.contains(val)) {
                    bUserData.setCity(val);
                    valid = true;
                }
            } while (!valid);
            valid = false;


            System.out.println("Birth date: dd-MM-yyyy");
            acquireDateCycle();

            System.out.println("User type: ");
            System.out.println("a. USER\n" + "b. ORGANIZER");
            do {
                String val = READER.readLine();
                switch (val) {
                    case "a":
                        bUserData.setType(UserTypes.USER);
                        valid = true;
                        break;
                    case "b":
                        bUserData.setType(UserTypes.ORGANIZER);
                        valid = true;
                        break;
                    default:
                        break;
                }
            } while (!valid);

            if (cFacade.registerUser(bUserData)) {
                System.out.println("User registration completed successfully! Return to login page.");
            } else {
                System.out.println("Error in registration procedure. Retry...");
                return 0;
            }
        } catch (IOException e) {
            logger.severe(e.getMessage());
        } catch (UsernameAlreadyTaken e) {
            logger.warning("Username already taken! Change it and retry registration...");
            return 0;
        } catch (InvalidValueException | TextTooLongException | MinimumAgeException e) {
            logger.warning(e.getMessage());
            return 0;
        }
        return 1;
    }

    private static boolean convertDateAndSetUserBean(String dateString, DateTimeFormatter formatter) {
        try {
            // Convertire la stringa in un oggetto LocalDate utilizzando il formatter
            LocalDate birthDate = LocalDate.parse(dateString, formatter);
            bUserData.setBirthDate(birthDate);
            return true;
        } catch (Exception e) {
            // Gestire il caso in cui l'input non sia nel formato corretto
            logger.severe("Invalid birth date format.");
            return false;
        }
    }

    private static void acquireDateCycle() {
        boolean valid = false;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        while (!valid) {
            String val = acquireInput();
            valid = convertDateAndSetUserBean(val, formatter);
        }
    }

    private static void chooseGenderCycle() {
        boolean valid = false;
        while (!valid) {
            String val = acquireInput();
            if (val != null) {
                switch (val) {
                    case "a":
                        setGenderToBean("Male");
                        valid = true;
                        break;
                    case "b":
                        setGenderToBean("Female");
                        valid = true;
                        break;
                    case "c":
                        setGenderToBean("Other");
                        valid = true;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private static void setGenderToBean(String val) {
        try {
            bUserData.setGender(val);
        } catch (InvalidValueException e) {
            logger.severe(e.getMessage());
        }
    }

    private static void populateRegistrationBean(boolean isGoogleAuth) {
        if (!isGoogleAuth) {
            //classic registration
            System.out.println("Username: ");
            setUsernameToBean(acquireInput());

            System.out.println("Password: ");
            setPasswordToBean(acquireInput());
        } else {
            setPasswordToBean("google_account_hidden_psw");
        }
    }

    @Override
    public void showNotification(NotificationTypes notificationType) {
        String value = null;
        if (notificationType.equals(NotificationTypes.EVENT_ADDED)) {
            value = "new event in your city!";
        } else if (notificationType.equals(NotificationTypes.USER_EVENT_PARTICIPATION)) {
            value = "new user participation to your event!";
        }
        spacer(1);
        System.out.println("New notification: " + value);
        spacer(1);
    }

    @Override
    public void addMessageToChat(BMessage messageBean) {
        //stampa nuovo messaggio arrivato nella chat
        System.out.println(cFacade.getUsernameByID(messageBean.getSenderID()) + ": " + messageBean.getMessage());
    }
}
