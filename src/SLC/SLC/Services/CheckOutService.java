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
            System.out.println("Entered access code: " + passcode);
            String verifyReply = slc.verifyAccessCode(passcode);

            if(verifyReply.equals("false")) {
                slc.setScreenText("title", "Invalid access code, please enter again.");
            }else if(verifyReply.equals("0.0")) {
                slc.setScreenText("title", "You can pick up you package now.");
            }else {
                slc.setScreenText("title", "You are fined $" + verifyReply + " for late pickup.");
            }
        });

        /**
         * setScreenText must run in Platform.runLater because
         * it has to queue after the scene to make sure the
         * scene is drawn before editing the text
         */
        Platform.runLater(() -> {
            slc.setScreenText("title", "Please enter the access code.");
        });
    }

    @Override
    public void onMessage(Msg message) {
        //serverMBox.send(new Msg(id, mbox, Msg.Type.SVR_CheckOut, message.getDetails()));
    }
}
