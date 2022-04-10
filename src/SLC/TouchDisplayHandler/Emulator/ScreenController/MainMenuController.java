package SLC.TouchDisplayHandler.Emulator.ScreenController;

import AppKickstarter.AppKickstarter;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulator;
import SLC.TouchDisplayHandler.Emulator.TouchDisplayEmulatorController;
import javafx.scene.text.Text;

import java.util.logging.Logger;

public class MainMenuController extends TouchDisplayEmulatorController {
    public Text button0;
    public Text button1;
    public Text button2;
    public Text button3;
    public Text button4;
    public Text button5;

    @Override
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator, String pollRespParam) {
        super.initialize(id, appKickstarter, log, touchDisplayEmulator, pollRespParam);
    }

    public void changeTextLabel(String id, String label) {
        Text target;

        switch (id) {
            case "button0":
                target = button0;
                break;
            case "button1":
                target = button1;
                break;
            case "button2":
                target = button2;
                break;
            case "button3":
                target = button3;
                break;
            case "button4":
                target = button4;
                break;
            case "button5":
                target = button5;
                break;
            default:
                return;
        }

        target.setText(label);
    }
}
