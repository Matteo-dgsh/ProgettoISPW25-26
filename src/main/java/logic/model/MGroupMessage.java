package logic.model;

public class MGroupMessage implements Message {
    private Integer senderID;
    private Integer receiverID;
    private String message;

    //SETTER
    @Override
    public void setSenderID(Integer senderID) {
        this.senderID = senderID;
    }

    @Override
    public void setReceiverID(Integer receiverID) {
        this.receiverID = receiverID;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    //GETTER
    @Override
    public Integer getSenderID() {
        return senderID;
    }

    @Override
    public Integer getReceiverID() {
        return receiverID;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
