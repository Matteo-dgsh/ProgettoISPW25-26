package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URISyntaxException;

public interface InfoPages {
    //implementata da GCHelpFAQs e GCContactUs
    void openLink(ActionEvent event) throws URISyntaxException, IOException;
}
