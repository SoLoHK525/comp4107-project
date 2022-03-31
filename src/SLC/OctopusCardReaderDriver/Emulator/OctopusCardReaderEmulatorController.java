package SLC.OctopusCardReaderDriver.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class OctopusCardReaderEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private OctopusCardReaderEmulator octopusCardReaderEmulator;
    private MBox octopusCardReaderMBox;
    private String activationResp;
    private String standbyResp;
    private String pollResp;
    public TextField amountField;
    public TextField cardNumField;
    public TextField cardReaderStatusField;
    public TextArea cardReaderTextArea;
    public ChoiceBox standbyRespCBox;
    public ChoiceBox activationRespCBox;
    public ChoiceBox pollRespCBox;

    public void initialize(String id, AppKickstarter appKickstarter, Logger log, OctopusCardReaderEmulator octopusCardReaderEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.octopusCardReaderEmulator = octopusCardReaderEmulator;
        this.octopusCardReaderMBox = appKickstarter.getThread("OctopusCardReaderDriver").getMBox();

        this.activationRespCBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                activationResp = activationRespCBox.getItems().get(newValue.intValue()).toString();
                appendTextArea("Activation Response set to " + activationRespCBox.getItems().get(newValue.intValue()).toString());
            }
        });
        this.standbyRespCBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                standbyResp = standbyRespCBox.getItems().get(newValue.intValue()).toString();
                appendTextArea("Standby Response set to " + standbyRespCBox.getItems().get(newValue.intValue()).toString());
            }
        });
        this.pollRespCBox.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                pollResp = pollRespCBox.getItems().get(newValue.intValue()).toString();
                appendTextArea("Poll Response set to " + pollRespCBox.getItems().get(newValue.intValue()).toString());
            }
        });

        this.activationResp = activationRespCBox.getValue().toString();
        this.standbyResp = standbyRespCBox.getValue().toString();
        this.pollResp = pollRespCBox.getValue().toString();
        this.goStandby();
    }


    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Octopus Card 1":
                cardNumField.setText(appKickstarter.getProperty("OctopusCardReader.card1"));
                break;

            case "Octopus Card 2":
                cardNumField.setText(appKickstarter.getProperty("OctopusCardReader.card2"));
                break;

            case "Octopus Card 3":
                cardNumField.setText(appKickstarter.getProperty("OctopusCardReader.card3"));
                break;

            case "Reset":
                cardNumField.setText("");
                break;

            case "Octopus here":
                octopusCardReaderMBox.send(new Msg(id, octopusCardReaderMBox, Msg.Type.OCR_CardRead, cardNumField.getText()));
                cardReaderTextArea.appendText("Charging card " + cardNumField.getText()+"\n");
                break;

            case "Activate/Standby":
                octopusCardReaderMBox.send(new Msg(id, octopusCardReaderMBox, Msg.Type.OCR_GoActive, cardNumField.getText()));
                cardReaderTextArea.appendText("Removing card\n");
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    }

    public String getActivationResp() { return activationResp; }

    public String getStandbyResp()    { return standbyResp; }

    public String getPollResp()       { return pollResp; }

    public void goActive() {
        updateOctopusCardReaderStatus("Active");
    }

    public void goStandby() {
        updateOctopusCardReaderStatus("Standby");
    }

    private void updateOctopusCardReaderStatus(String status) {
        cardReaderStatusField.setText(status);
    }

    public void appendTextArea(String status) {
        cardReaderTextArea.appendText(status+"\n");
    }


}
