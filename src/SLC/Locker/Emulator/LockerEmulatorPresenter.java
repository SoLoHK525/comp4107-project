package SLC.Locker.Emulator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;


public class LockerEmulatorPresenter implements IPresenter{

    @FXML
    private ChoiceBox PollOptions;

    private String pollMode = "ACK";


    public void start()  {
        try {
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            String fxmlName = "LockerEmulatorView.fxml";
            loader.setLocation(LockerEmulatorPresenter.class.getResource(fxmlName));
            Parent root = loader.load();
            System.out.println(root);
            primaryStage.setTitle("Locker Preview");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException io) {
            System.out.println("Fail to load Locker emulator io");
        }
    }

    public void initialize() {
        PollOptions.getSelectionModel().selectFirst();
    }

    public void onLockerClicked(ActionEvent actionEvent) {
        System.out.println(actionEvent);
    }

    public void onPollModeSelected(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getTarget();
        System.out.println(pollMode + " Original");
        System.out.println(item.getLabel());
    }
}
