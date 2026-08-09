package logic.dao;

import logic.model.MGroupMessage;
import logic.model.Message;
import logic.utils.InMemoryDataStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione di IChatDAO usata in modalita' Demo: nessuna query SQL,
 * i dati vivono solo in InMemoryDataStore e vengono persi alla chiusura dell'app.
 */
public class ChatDAOInMemory implements IChatDAO {

    @Override
    public List<MGroupMessage> retrieveGroupChat(Integer groupID) {
        return new ArrayList<>(InMemoryDataStore.chatMessages.getOrDefault(groupID, new ArrayList<>()));
    }

    @Override
    public boolean writeMessage(Message message) {
        MGroupMessage stored = new MGroupMessage();
        stored.setSenderID(message.getSenderID());
        stored.setReceiverID(message.getReceiverID());
        stored.setMessage(message.getMessage());
        InMemoryDataStore.chatMessages.computeIfAbsent(message.getReceiverID(), id -> new ArrayList<>()).add(stored);
        return true;
    }
}
