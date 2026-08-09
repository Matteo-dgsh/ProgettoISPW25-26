package logic.model;

import logic.beans.BEvent;
import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;

import static logic.view.EssentialGUI.logger;

public class MEvent {
    private int eventID;
    private String eventOrganizer;
    private int eventOrganizerID;
    private String eventName;
    private String eventProvince;
    private String eventCity;
    private String eventAddress;
    private String eventMusicGenre;
    private String eventDate;
    private String eventTime;
    private byte[] eventPicData;
    private String eventPicPath;



    public MEvent(BEvent eventBean) {
        eventID = eventBean.getEventID();
        eventOrganizer = eventBean.getEventOrganizer();
        eventOrganizerID = eventBean.getEventOrganizerID();
        eventName = eventBean.getEventName();
        eventProvince = eventBean.getEventProvince();
        eventCity = eventBean.getEventCity();
        eventAddress = eventBean.getEventAddress();
        eventMusicGenre = eventBean.getEventMusicGenre();
        eventDate = eventBean.getEventDate();
        eventTime = eventBean.getEventTime();
        eventPicData = eventBean.getEventPicData();
        eventPicPath = eventBean.getEventPicPath();
    }

    public MEvent() {
        //empty
    }

    public BEvent getEventInfo() {
        try {
            BEvent eventInfo = new BEvent();
            eventInfo.setEventID(eventID);
            eventInfo.setEventOrganizer(eventOrganizer);
            eventInfo.setEventOrganizerID(eventOrganizerID);
            eventInfo.setEventName(eventName);
            eventInfo.setEventProvince(eventProvince);
            eventInfo.setEventCity(eventCity);
            eventInfo.setEventAddress(eventAddress);
            eventInfo.setEventMusicGenre(eventMusicGenre);
            eventInfo.setEventDate(eventDate);
            eventInfo.setEventTime(eventTime);
            eventInfo.setEventPicData(eventPicData); //data = array di bytes
            eventInfo.setEventPicPath(eventPicPath);
            return eventInfo;
        } catch (InvalidValueException | TextTooLongException e) {
            //grave se entra qui dentro perché prendendo le informazioni dal DB dovrebbero essere già state controllate all'inserimento
            logger.severe(e.getMessage());
            return null;
        }
    }

    //SETTERS
    public void setEventID(int eventID) {
        this.eventID = eventID;
    }
    public void setEventOrganizer(String eventOrg){
        this.eventOrganizer = eventOrg;
    }
    public void setEventOrganizerID(int eventOrgID){
        this.eventOrganizerID = eventOrgID;
    }
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
    public void setEventProvince(String eventProvince){this.eventProvince = eventProvince;}
    public void setEventCity(String eventCity) {
        this.eventCity = eventCity;
    }
    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }
    public void setEventMusicGenre(String eventMusicGenre) {
        this.eventMusicGenre = eventMusicGenre;
    }
    public void setEventDate(String eventDate){
        this.eventDate = eventDate;
    }
    public void setEventTime(String eventTime){
        this.eventTime = eventTime;
    }
    public void setEventPicDataFromDB(byte[] picData){
        this.eventPicData = picData;
    }
    public void setEventPicPath(String picPath){this.eventPicPath = picPath;}


    //GETTERS
    public int getEventID(){return this.eventID;}
    public String getEventOrganizer(){return this.eventOrganizer;}
    public int getEventOrganizerID(){return this.eventOrganizerID;}
    public String getEventName(){return this.eventName;}
    public String getEventProvince(){return this.eventProvince;}
    public String getEventCity() {
        return this.eventCity;
    }
    public String getEventAddress(){return this.eventAddress;}
    public String getEventMusicGenre(){return this.eventMusicGenre;}
    public String getEventDate(){return this.eventDate;}
    public String getEventTime(){return this.eventTime;}
    public byte[] getEventPicData(){return this.eventPicData;}
    public String getEventPicPath(){return this.eventPicPath;}
}


