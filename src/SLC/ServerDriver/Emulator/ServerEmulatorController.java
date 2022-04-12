package SLC.ServerDriver.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import SLC.OctopusCardReaderDriver.Emulator.OctopusCardReaderEmulator;
import com.sun.security.ntlm.Server;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

import java.util.logging.Logger;

public class ServerEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private ServerEmulator serverEmulator;
    private MBox serverMBox;

    public TextArea orderTextArea;
    public TextArea accessCodeToLockerTextArea;
    public ChoiceBox pollRespCBox;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, ServerEmulator serverEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.serverEmulator = serverEmulator;
        this.serverMBox = appKickstarter.getThread("ServerDriver").getMBox();
    }

    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getId()) {
            case "btn-new-order":
                this.serverEmulator.addOrder();
                break;
            default:
                this.log.info(this.id + ": unknown button");
                break;
        }
    }
}
