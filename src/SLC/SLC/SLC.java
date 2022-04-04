package SLC.SLC;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.util.concurrent.ThreadLocalRandom;


//======================================================================
// SLC
public class SLC extends AppThread {
    private int pollingTime;
    private MBox barcodeReaderMBox;
    private MBox touchDisplayMBox;
    private MBox octopusCardReaderMBox;
    private MBox lockerMBox;

    //------------------------------------------------------------
    // SLC
    public SLC(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        pollingTime = Integer.parseInt(appKickstarter.getProperty("SLC.PollingTime"));
    } // SLC


    //------------------------------------------------------------
    // run
    public void run() {
        Timer.setTimer(id, mbox, pollingTime);
        log.info(id + ": starting...");

        barcodeReaderMBox = appKickstarter.getThread("BarcodeReaderDriver").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        octopusCardReaderMBox = appKickstarter.getThread("OctopusCardReaderDriver").getMBox();
        lockerMBox = appKickstarter.getThread("Locker").getMBox();

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case TD_MouseClicked:
                    log.info("MouseCLicked: " + msg.getDetails());
                    processMouseClicked(msg);
                    break;

                case LK_ReturnStatus:
                    log.info("LK_Status: " + msg.getDetails());
                    break;

                case TimesUp:
                    Timer.setTimer(id, mbox, pollingTime);
                    log.info("Poll: " + msg.getDetails());
                    barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    octopusCardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    lockerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));

                    //For testing purpose
                    //lockerMBox.send(new Msg(id, mbox, Msg.Type.LK_Unlock, String.format("%04d", ThreadLocalRandom.current().nextInt(0, 16))));
                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case OCR_CardRead:
                    log.info("Octopus Card " + msg.getDetails() + " is charged");
                    octopusCardReaderMBox.send(new Msg(id, mbox, Msg.Type.OCR_Charged, ""));
                    break;

                case OCR_GoActive:
                    log.info("[Octopus Card Reader] GoActive Response: " + msg.getDetails());
                    break;

                case OCR_GoStandby:
                    log.info("[Octopus Card Reader] GoStandby Response: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                
                case Terminate:
                    quit = true;
                    break;
                
                case BR_GoActive:
                    if (msg.getSender().equals("BarcodeReaderDriver")){
                        log.info("Activation Response: " + msg.getDetails());
                        break;
                    }
                    log.info("Activate: " + msg.getDetails());
                    barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.BR_GoActive, ""));
                    break;

                case BR_GoStandby:
                    if (msg.getSender().equals("BarcodeReaderDriver")){
                        log.info("Standby Response: " + msg.getDetails());
                        break;
                    }
                    log.info("Standby: " + msg.getDetails());
                    barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.BR_GoStandby, ""));
                    break;

                case BR_BarcodeRead:
                    log.info("[" + msg.getSender() + "(Received Barcode): " + msg.getDetails() + "]");
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        // *** process mouse click here!!! ***
    } // processMouseClicked
} // SLC
