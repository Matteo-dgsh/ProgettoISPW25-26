package logic.controllers;

import logic.beans.BMessage;
import logic.controllers.factory.MessageFactory;
import logic.dao.ChatDAO;
import logic.dao.ChatDAOInMemory;
import logic.dao.IChatDAO;
import logic.model.MGroupMessage;
import logic.model.Message;
import logic.utils.PersistenceClass;
import logic.utils.enums.MessageTypes;

import java.util.ArrayList;
import java.util.List;

public class CGroupChat {
    private IChatDAO chatDAO;
    private MessageFactory msgFactory;

    public CGroupChat(){
        this.chatDAO = switch (PersistenceClass.getPersistenceType()) {
            case IN_MEMORY -> new ChatDAOInMemory();
            case JDBC, FILE_SYSTEM -> new ChatDAO();
        };
        this.msgFactory = new MessageFactory();
    }

    public List<BMessage> retrieveGroupChat(Integer groupID) {
        return makeBeansFromModels(chatDAO.retrieveGroupChat(groupID));
    }

    private List<BMessage> makeBeansFromModels (List<MGroupMessage> models){
        ArrayList<BMessage> beans = new ArrayList<>();
        for (MGroupMessage model : models){
            BMessage bean = new BMessage(model);
            beans.add(bean);
        }
        return beans;
    }

    public boolean writeMessage(BMessage message) {
        Message msgModel = msgFactory.createMessage(MessageTypes.GROUP, message.getSenderID(), message.getReceiverID(), message.getMessage());
        return chatDAO.writeMessage(msgModel);
    }
}
