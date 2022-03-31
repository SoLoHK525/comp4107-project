package SLC.Locker.Emulator;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class EmulatorViewController extends Observable {
    @FXML
    private ChoiceBox PollOptions;
    @FXML
    private GridPane LockerGrids;


    private int NUM_ROW = 8;

    private String UNLOCK_BG = "-fx-background-color:red";
    private String LOCK_BG = "-fx-background-color:#dddddd";

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
        locker.setOnAction(this::onLockerClicked);
        locker.setMinSize(100, 30);
        locker.setAlignment(Pos.CENTER);
        locker.setStyle(LOCK_BG);
        LockerGrids.add(locker, (int) Math.floor(order / NUM_ROW), order % NUM_ROW);
    }

    public void SetLocker(String id, boolean isLock) {
        ObservableList<Node> lockers = LockerGrids.getChildren();
        for (Node locker : lockers) {
            if (!(locker instanceof Button)) continue;
            Button lockerBtn = (Button) locker;
            if (lockerBtn.getText().equals(id)) {
                if (!isLock) {
                    lockerBtn.setStyle(UNLOCK_BG);
                } else {
                    lockerBtn.setStyle(LOCK_BG);
                }
                return;
            }
        }
    }

    public void onLockerClicked(ActionEvent actionEvent) {
        Button locker = (Button) actionEvent.getTarget();
        super.setChanged();
        notifyObservers(locker.getText());
    }

    public String getPoll() {
        return (String) PollOptions.getValue();
    }

}
