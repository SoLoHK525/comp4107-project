package SLC.TouchDisplayHandler.Emulator.ScreenController;

import AppKickstarter.AppKickstarter;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulatorController;
import javafx.scene.text.Text;

import java.util.logging.Logger;

public class TextController extends TouchDisplayEmulatorController {
    public Text title;
    public Text subtitle;
    public Text body;

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
            case "subtitle":
                target = subtitle;
                break;
            case "body":
                target = body;
                break;
            default:
                return;
        }

        target.setText(label);
    }
}
