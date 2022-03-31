package SLC.OctopusCardReaderDriver.Emulator;

import AppKickstarter.misc.Msg;
import SLC.SLCStarter;
import SLC.OctopusCardReaderDriver.OctopusCardReaderDriver;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


public class OctopusCardReaderEmulator extends OctopusCardReaderDriver {
    private SLCStarter slcStarter;
    private String id;
    private Stage myStage;
    private OctopusCardReaderEmulatorController octopusCardReaderEmulatorController;

    public OctopusCardReaderEmulator(String id, SLCStarter slcStarter) {
        super(id, slcStarter);
        this.slcStarter = slcStarter;
        this.id = id;
    }

    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "OctopusCardReaderEmulator.fxml";
        loader.setLocation(OctopusCardReaderEmulator.class.getResource(fxmlName));
        root = loader.load();
        octopusCardReaderEmulatorController = (OctopusCardReaderEmulatorController) loader.getController();
        octopusCardReaderEmulatorController.initialize(id, slcStarter, log, this);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 504, 655));
        myStage.setTitle("Octopus Card Reader");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            slcStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    }


    protected void handleGoActive() {
        switch (octopusCardReaderEmulatorController.getActivationResp()) {
            case "Activated":
                slc.send(new Msg(id, mbox, Msg.Type.OCR_GoActive, "Octopus Card Reader Activated!"));
                octopusCardReaderEmulatorController.goActive();
                break;

            case "Standby":
                slc.send(new Msg(id, mbox, Msg.Type.OCR_GoStandby, "Octopus Card Reader Standby!"));
                octopusCardReaderEmulatorController.goStandby();
                break;

            case "Ignore":
                break;
        }
        octopusCardReaderEmulatorController.appendTextArea("Octopus Card Reader Activated");
    }


    protected void handleGoStandby() {
        switch (octopusCardReaderEmulatorController.getStandbyResp()) {
            case "Activated":
                slc.send(new Msg(id, mbox, Msg.Type.OCR_GoActive, "Octopus Card Reader Activated!"));
                octopusCardReaderEmulatorController.goActive();
                break;

            case "Standby":
                slc.send(new Msg(id, mbox, Msg.Type.OCR_GoStandby, "Octopus Card Reader Standby!"));
                octopusCardReaderEmulatorController.goStandby();
                break;

            case "Ignore":
                break;
        }
        octopusCardReaderEmulatorController.appendTextArea("Octopus Card Reader Standby");
    }


    protected void handlePoll() {
        switch (octopusCardReaderEmulatorController.getPollResp()) {
            case "ACK":
                slc.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                break;

            case "NAK":
                slc.send(new Msg(id, mbox, Msg.Type.PollNak, id + " is down!"));
                break;

            case "Ignore":
                break;
        }
    }



}
