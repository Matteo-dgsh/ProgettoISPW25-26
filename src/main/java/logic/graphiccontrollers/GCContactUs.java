package logic.graphiccontrollers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;

import logic.view.EssentialGUI;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class GCContactUs extends EssentialGUI implements InfoPages {

    @FXML
    private Hyperlink mattHyperlink;

    @FXML
    private Hyperlink nicoHyperlink;

    @Override
    public void openLink(ActionEvent event) throws URISyntaxException, IOException {
        if (nicoHyperlink.isVisited()) {
            Desktop.getDesktop().browse(new URI(nicoHyperlink.getText()));
            nicoHyperlink.setVisited(false);
        } else {
            Desktop.getDesktop().browse(new URI(mattHyperlink.getText()));
            mattHyperlink.setVisited(false);
        }
    }
}
