package logic.beans;

import logic.model.Message;

public class BMessage {
    private Integer receiverID;
    private Integer senderID;
    private String message;

    public BMessage(Message model){
        this.senderID = model.getSenderID();
        this.message = model.getMessage();
    }

    public BMessage(Integer senderID, String message, Integer receiverID){
        this.senderID = senderID;
        this.message = message;
        this.receiverID = receiverID;
    }

    //SETTER
    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    //GETTER
    public Integer getSenderID() {
        return senderID;
    }

    public String getMessage() {
        return message;
    }

    public Integer getReceiverID() {
        return receiverID;
    }
}
