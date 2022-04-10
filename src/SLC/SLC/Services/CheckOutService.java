package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.Handlers.MouseClick.PasscodeMouseClickHandler;
import SLC.SLC.SLC;
import SLC.SLC.Screen;
import javafx.application.Platform;

public class CheckOutService extends Service {
    public CheckOutService(SLC instance) {
        super(instance);
        initialScreen();
    }

    void initialScreen() {
        slc.setScreen(Screen.Passcode);

        PasscodeMouseClickHandler handler = (PasscodeMouseClickHandler) slc.getMouseClickHandler();

        // need to update the passcode to the screen like this
        // kinda like how react update textfields
        handler.onPasscodeChange((String passcode) -> {
            slc.setScreenText("passcode", passcode);
        });

        // when user click on the "Enter" pad
        handler.onPasscodeEnter((String passcode) -> {
            System.out.println("Entered passcode: " + passcode);
        });

        /**
         * setScreenText must run in Platform.runLater because
         * it has to queue after the scene to make sure the
         * scene is drawn before editing the text
         */
        Platform.runLater(() -> {
            slc.setScreenText("title", "Please enter the passcode.");
        });
    }

    @Override
    public void onMessage(Msg message) {

    }
}
