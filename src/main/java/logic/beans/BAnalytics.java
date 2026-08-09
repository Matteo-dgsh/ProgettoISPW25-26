package logic.beans;

import logic.exceptions.InvalidValueException;
import logic.exceptions.TextTooLongException;

public class BAnalytics extends BEvent{
    private Integer timesClicked;
    private Integer participants;
    private Integer plannedParticipations;

    public BAnalytics(BEvent eventBean) throws InvalidValueException, TextTooLongException {
        setEventName(eventBean.getEventName());
        setEventProvince(eventBean.getEventProvince());
        setEventCity(eventBean.getEventCity());
        setEventAddress(eventBean.getEventAddress());
        setEventMusicGenre(eventBean.getEventMusicGenre());
        setEventDate(eventBean.getEventDate());
        setEventTime(eventBean.getEventTime());
    }

    //SETTERS
    public void setTimesClicked(Integer timesClicked) {
        this.timesClicked = timesClicked;
    }

    public void setParticipants(Integer participants) {
        this.participants = participants;
    }

    public void setPlannedParticipations(Integer plannedParticipations) {
        this.plannedParticipations = plannedParticipations;
    }

    //GETTERS
    public Integer getTimesClicked(){
        return this.timesClicked;
    }

    public Integer getParticipants(){
        return this.participants;
    }

    public Integer getPlannedParticipations(){
        return this.plannedParticipations;
    }

}
