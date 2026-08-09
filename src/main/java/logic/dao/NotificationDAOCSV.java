package logic.dao;

import com.opencsv.CSVWriter;
import logic.controllers.factory.NotificationFactory;
import logic.model.Notification;
import logic.model.NotificationProperties;
import logic.utils.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import logic.utils.enums.NotificationTypes;

import static logic.view.EssentialGUI.logger;


public class NotificationDAOCSV implements NotificationDAO {

    private static final String CSV_DB_NAME = "DBNotifications.csv";

    private int entriesNumber = 0;

    //file descriptor
    private File fd;

    private NotificationFactory notiFactory;

    private static class NotificationAttributesOrder {
        private static final int FIRST = 0;
        private static final int SECOND = 1;
        private static final int THIRD = 2;
        private static final int FOURTH = 3;
        private static final int FIFTH = 4;
        public static int getIndexNotificationID() {
            return FIRST;
        }

        public static int getIndexNotifiedID() {
            return SECOND;
        }

        public static int getIndexNotiType() {
            return THIRD;
        }

        public static int getIndexEventID() {
            return FOURTH;
        }

        public static int getIndexNotifierID() {
            return FIFTH;
        }
    }

    public NotificationDAOCSV() {
        //creo la cartella che conterrà il CSV DB
        String folderName = "csvData";
        File folder = new File(folderName);

        if (!folder.exists()) {
            createFolder(folder);
        } else {
            logger.finest(() -> "Folder already exists: " + folderName);
        }

        String filePath = folderName + File.separator + CSV_DB_NAME;

        //inizializzo il file descriptor
        this.fd = new File(filePath);

        //elimino il file se già esiste
        if (fd.exists()) {
            // Elimina il file esistente
            try{
                Files.delete(Paths.get(filePath));
                logger.finest("Deleted existing CSV file.");
            } catch (IOException e) {
                logger.warning("Failed to delete existing CSV file.");
            }
        }

        //creo il file
        if (!this.fd.exists()) {
            createFileFromDesc(this.fd);
        } else {
            logger.finest(() -> "File CSV already exists in path: " + folderName + "/" + CSV_DB_NAME);
        }

        //aggiorno l'indice del file all'ultima entry aggiunta
        updateLastIndex();

        //inizializzo una notificationFactory
        this.notiFactory = new NotificationFactory();

    }

    private void createFileFromDesc(File fd) {
        try {
            boolean res = fd.createNewFile();
            if (res) {
                logger.finest("Created new file.");
            } else {
                logger.finest("Creation of CSV file failed.");
            }
        } catch (IOException e) {
            logger.severe("Error while creating new file: " + e.getMessage());
        }
    }

    private void createFolder(File folderFile) {
        boolean success = folderFile.mkdirs();
        if (success) {
            logger.finest("New folder created successfully.");
        } else {
            logger.finest("Folder creation failed.");
        }
    }

    private void updateLastIndex() {
        int count = 0;
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            //cerco le credenziali username e password nel file CSV
            while (csvReader.readNext() != null) {
                count++;
            }
        } catch (CsvValidationException | IOException e) {
            logger.severe("Cannot update last index");
        }
        entriesNumber = count;
    }

    @Override
    public void addNotificationToUsers(List<Integer> notifiedIDs, NotificationTypes notificationTypes, int eventID) {
        try (CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter(fd, true)))){
            String[] csvRecord = new String[5];

            for (Integer notifiedID : notifiedIDs) {
                csvRecord[NotificationAttributesOrder.getIndexNotificationID()] = String.valueOf(++entriesNumber);
                csvRecord[NotificationAttributesOrder.getIndexNotifiedID()] = String.valueOf(notifiedID);
                csvRecord[NotificationAttributesOrder.getIndexNotiType()] = notificationTypes.toString();
                csvRecord[NotificationAttributesOrder.getIndexEventID()] = String.valueOf(eventID);
                csvRecord[NotificationAttributesOrder.getIndexNotifierID()] = String.valueOf(LoggedUser.getUserID());

                //scrivo e aggiorno il file csv
                csvWriter.writeNext(csvRecord);
                csvWriter.flush();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while adding notification to user (csv)");
        }
    }

    @Override
    public ArrayList<Notification> getNotificationsByUserID(int usrID) {
        ArrayList<Notification> notifications = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd)))) {
            String[] retrievedRecord;
            Notification msg;

            while ((retrievedRecord = csvReader.readNext()) != null) {
                if (Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotifiedID()]) == usrID) {
                    //id, type, event_id, notifier_id
                    if ((retrievedRecord[NotificationAttributesOrder.getIndexNotiType()]).equals(NotificationTypes.EVENT_ADDED.toString())) {
                        msg = notiFactory.createNotification(NotificationTypes.EVENT_ADDED, usrID, new NotificationProperties(Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotifierID()]), Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotificationID()])), Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexEventID()]), null, null);
                    } else {
                        msg = notiFactory.createNotification(NotificationTypes.USER_EVENT_PARTICIPATION, usrID, new NotificationProperties(Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotifierID()]), Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotificationID()])), Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexEventID()]), null, null);
                    }
                    notifications.add(msg);
                }
            }
        } catch (CsvValidationException | IOException e) {
            logger.log(Level.SEVERE, "SQLException occurred while getting notifications list by userID (csv)");
        }
        return notifications;
    }

    @Override
    public boolean deleteNotification(int notificationID) {
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(fd))); CSVWriter csvWriter = new CSVWriter(new BufferedWriter(new FileWriter("csvData/temp.csv", false)))){
            String[] retrievedRecord;
            while ((retrievedRecord = csvReader.readNext()) != null) {
                if (Integer.parseInt(retrievedRecord[NotificationAttributesOrder.getIndexNotificationID()]) != notificationID) {
                    // Scrivi solo le voci che non corrispondono all'ID della notifica da eliminare
                    csvWriter.writeNext(retrievedRecord);
                }
            }
        } catch (CsvValidationException | IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while removing notification from db (csv)");
            return false;
        }

        return moveTempIntoCSV();
    }

    private boolean moveTempIntoCSV(){
        try {
            Files.move(Paths.get("csvData/temp.csv"), Paths.get(fd.toURI()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException occurred while moving temp file into CSV database");
            return false;
        }
        return true;
    }
}
