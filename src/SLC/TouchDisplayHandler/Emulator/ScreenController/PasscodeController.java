package SLC.TouchDisplayHandler.Emulator.ScreenController;

import AppKickstarter.AppKickstarter;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulatorController;
import javafx.scene.text.Text;

import java.util.logging.Logger;

public class PasscodeController extends TouchDisplayEmulatorController {
    public Text title;
    public Text passcode;

    @Override
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator, String pollRespParam) {
        super.initialize(id, appKickstarter, log, touchDisplayEmulator, pollRespParam);
    }

    public void changeTextLabel(String id, String label) {
        Text target;

        switch (id) {
            case "title":
                target = title;
                break;
            case "passcode":
                target = passcode;
                break;
            default:
                return;
        }

        target.setText(label);
    }
}
