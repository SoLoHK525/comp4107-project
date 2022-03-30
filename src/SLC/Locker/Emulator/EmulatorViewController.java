package SLC.Locker.Emulator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.concurrent.locks.Lock;

public class EmulatorViewController {
    @FXML
    private ChoiceBox PollOptions;
    @FXML
    private GridPane LockerGrids;

    private int NUM_ROW = 8;

    public void initialize() {
        PollOptions.getSelectionModel().selectFirst();
    }

    public void addToGrid(String id, int order) {
        if (order % NUM_ROW == 0) {
            ColumnConstraints cons = new ColumnConstraints();
            cons.setHgrow(Priority.SOMETIMES);
            cons.setMinWidth(100);
            cons.setMaxWidth(100);
            LockerGrids.getColumnConstraints().add(cons);

        }
        Button locker = new Button(id);
        locker.setMinSize(100, 30);
        locker.setAlignment(Pos.CENTER);
        LockerGrids.add(locker, (int) Math.floor(order / NUM_ROW), order % NUM_ROW);
    }

    public void onLockerClicked(ActionEvent actionEvent) {
        System.out.println(actionEvent);
        System.out.println(PollOptions.getValue());
    }

    public String getPoll() {
        return (String) PollOptions.getValue();
    }
}
