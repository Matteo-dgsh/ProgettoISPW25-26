package logic.dao;

import logic.model.MGroupMessage;
import logic.model.Message;

import java.util.List;

public interface IChatDAO {
    List<MGroupMessage> retrieveGroupChat(Integer groupID);

    boolean writeMessage(Message message);
}
