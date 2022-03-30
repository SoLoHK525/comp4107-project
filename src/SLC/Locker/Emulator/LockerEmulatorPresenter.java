package SLC.Locker.Emulator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;


public class LockerEmulatorPresenter {

    private EmulatorViewController viewController;
    private LockerEmulatorModel lockerData;


    public LockerEmulatorPresenter(LockerEmulatorModel lockerData) {
        this.lockerData = lockerData;
    }

    public void start() {
        try {
            //  Only called once
            //  Load fxml template and set up stage
            Stage primaryStage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            String fxmlName = "LockerEmulatorView.fxml";
            loader.setLocation(LockerEmulatorPresenter.class.getResource(fxmlName));
            Parent root = loader.load();
            viewController = loader.getController();
            viewController.initialize();
            primaryStage.setTitle("Locker Preview");
            primaryStage.setScene(new Scene(root, 600, 400));
            primaryStage.setResizable(false);
            primaryStage.show();

            //  Insert and render locker data
            ArrayList<String> ids = this.lockerData.GetAllLockerID();
            for(int i = 0; i < ids.size(); i++) {
                viewController.addToGrid(ids.get(i), i);
            }
        } catch (IOException io) {
            System.out.println("Fail to load Locker emulator io");
        }
    }

    public String getPoll() {
        return viewController.getPoll();
    }
}
