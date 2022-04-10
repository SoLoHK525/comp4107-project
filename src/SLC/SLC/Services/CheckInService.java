package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.SLC;
import SLC.SLC.Screen;
import javafx.application.Platform;

public class CheckInService extends Service {
    public CheckInService(SLC instance) {
        super(instance);

        slc.setScreen(Screen.Text);

        System.out.println("Running check in service");

        Platform.runLater(() -> {
            slc.setScreenText("title", "Welcome!");
            slc.setScreenText("subtitle", "XXX Smart Locker");
            slc.setScreenText("body", "Scan a barcode to checkin your delivery ticket.");
        });
    }

    @Override
    public void onMessage(Msg message) {

    }
}
