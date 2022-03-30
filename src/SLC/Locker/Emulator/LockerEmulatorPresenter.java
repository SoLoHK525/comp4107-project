package SLC.Locker.Emulator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class LockerEmulatorPresenter {

    private EmulatorViewController viewController;

    public void start() {
        try {
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
        } catch (IOException io) {
            System.out.println("Fail to load Locker emulator io");
        }
    }

    public String getPoll() {
        return viewController.getPoll();
    }
}
