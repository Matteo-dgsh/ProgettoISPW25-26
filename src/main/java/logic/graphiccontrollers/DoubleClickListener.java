package logic.graphiccontrollers;

import javafx.scene.input.MouseEvent;
import logic.beans.BEvent;

public interface DoubleClickListener {
    void setupEventClickListener();
    void onItemDoubleClick(MouseEvent event, BEvent selectedEventBean, String fxmlpage);
}
