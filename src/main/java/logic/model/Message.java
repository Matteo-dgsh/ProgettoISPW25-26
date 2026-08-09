package logic.model;

import java.io.Serializable;

public interface Message extends Serializable {
    long serialVersionUID = 1L;
    void setSenderID(Integer senderID);
    void setReceiverID(Integer receiverID);
    void setMessage(String text);
    Integer getSenderID();
    Integer getReceiverID();
    String getMessage();

}
