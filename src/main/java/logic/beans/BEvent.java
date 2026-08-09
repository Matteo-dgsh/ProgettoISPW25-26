package logic.beans;

import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class BEvent {
    protected String eventName;
    protected String eventProvince;
    protected String eventCity;
    protected String eventAddress;
    protected String eventMusicGenre;
    protected String eventDate;
    protected String eventTime;
    private int eventID;
    private byte[] eventPicData;
    private String eventOrganizer;
    private int eventOrganizerID;
    private String eventPicPath;

    public BEvent() {
        //empty
    }

    //SETTERS
    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public void setEventOrganizer(String eventOrganizer) {
        this.eventOrganizer = eventOrganizer;
    }

    public void setEventOrganizerID(int eventOrgID) {
        this.eventOrganizerID = eventOrgID;
    }

    public void setEventName(String eventName) throws InvalidValueException, TextTooLongException {
        if(eventName == null || eventName.equalsIgnoreCase("")) {
            throw new InvalidValueException("Please insert a valid event name");
        }
        else if(eventName.length() > 20) {
            throw new TextTooLongException("Too many characters for event name field");
        }
        this.eventName = eventName;
    }

    public void setEventProvince(String province) throws InvalidValueException {
        if(province == null){
            throw new InvalidValueException("Please insert a valid province");
        }
        this.eventProvince = province;
    }

    public void setEventCity(String city) throws InvalidValueException {
        if(city == null){
            throw new InvalidValueException("Please insert a valid city");
        }
        this.eventCity = city;
    }

    public void setEventAddress(String eventAddress) throws InvalidValueException, TextTooLongException {
        if(eventAddress == null || eventAddress.equalsIgnoreCase("")) {
            throw new InvalidValueException("Please insert a valid address");
        }
        else if(eventAddress.length() > 50) {
            throw new TextTooLongException("Too many characters for address field");
        }
        this.eventAddress = eventAddress;
    }

    public void setEventMusicGenre(String eventMusicGenre) throws InvalidValueException {
        if(eventMusicGenre == null) {
            throw new InvalidValueException("Please insert a valid music genre");
        }
        this.eventMusicGenre = eventMusicGenre;
    }

    public void setEventDate(String eventDate) throws InvalidValueException {
        if(eventDate == null) {
            throw new InvalidValueException("Please insert a valid date");
        }
        this.eventDate = eventDate;
    }

    public void setEventTime(String eventHour, String eventMinutes) throws InvalidValueException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try{
            LocalTime localTime = LocalTime.parse(eventHour + ":" + eventMinutes, formatter);
            this.eventTime = localTime.toString();
        } catch (Exception e){
            throw new InvalidValueException("Please insert a valid time (hours 00-23, minutes 00-59)");
        }
    }

    public void setEventTime(String eventTime) throws InvalidValueException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try{
            LocalTime localTime = LocalTime.parse(eventTime, formatter);
            this.eventTime = localTime.toString();
        } catch (Exception e){
            throw new InvalidValueException("Please insert a valid time (hours 00-23, minutes 00-59)");
        }
    }

    public void setEventPicData(byte[] picData) { //ALL = Data + File, rende disponibile sia la versione file che la versione binaria dell'immagine
        this.eventPicData = picData;
    }

    public void setEventPicPath(String path) throws InvalidValueException {
        if(path.isBlank()) {
            throw new InvalidValueException("Please insert an image to complete event adding");
        }
        this.eventPicPath = path;
    }

    //GETTERS
    public int getEventID() {
        return this.eventID;
    }

    public String getEventOrganizer() {
        return this.eventOrganizer;
    }

    public int getEventOrganizerID() {
        return this.eventOrganizerID;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getEventProvince() {
        return this.eventProvince;
    }

    public String getEventCity() {
        return this.eventCity;
    }

    public String getEventAddress() {
        return this.eventAddress;
    }

    public String getEventMusicGenre() {
        return this.eventMusicGenre;
    }

    public String getEventDate() {
        return this.eventDate;
    }

    public String getEventTime() {
        return this.eventTime;
    }

    public byte[] getEventPicData() {
        return this.eventPicData;
    }

    public String getEventPicPath() {
        return this.eventPicPath;
    }
}
