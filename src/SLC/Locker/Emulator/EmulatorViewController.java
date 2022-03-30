package SLC.Locker.Emulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;

public class EmulatorViewController {
    @FXML
    private ChoiceBox PollOptions;

    public void initialize() {
        System.out.println(PollOptions);
        PollOptions.getSelectionModel().selectFirst();
    }

    public void onLockerClicked(ActionEvent actionEvent) {
        System.out.println(actionEvent);
        System.out.println(PollOptions.getValue());
    }

    public String getPoll() {
        return (String) PollOptions.getValue();
    }
}
