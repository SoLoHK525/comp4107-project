package SLC.SLC;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;
import SLC.SLC.Handlers.MouseClick.MainMenuMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.Handlers.MouseClick.ConfirmationMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.PasscodeMouseClickHandler;
import SLC.SLC.Services.*;
import javafx.application.Platform;


//======================================================================
// SLC
public class SLC extends AppThread {
    private int pollingTime;
    private MBox barcodeReaderMBox;
    private MBox touchDisplayMBox;
    private MBox octopusCardReaderMBox;
    private MBox lockerMBox;

    private Screen screen;
    MouseClickHandler mouseClickHandler;

    private Service currentService;

    //------------------------------------------------------------
    // SLC
    public SLC(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        pollingTime = Integer.parseInt(appKickstarter.getProperty("SLC.PollingTime"));
    } // SLC

    public void setScreen(Screen screen) {
        this.screen = screen;

        switch (screen) {
            case MainMenu:
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
                mouseClickHandler = new MainMenuMouseClickHandler();
                break;
            case Confirmation:
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
                mouseClickHandler = new ConfirmationMouseClickHandler();
            case Passcode:
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "Passcode"));
                mouseClickHandler = new PasscodeMouseClickHandler();
                break;
            case Text:
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "Text"));
                mouseClickHandler = null;
                break;
            case Empty:
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_UpdateDisplay, "Empty"));
                mouseClickHandler = null;
                break;
            default:
                mouseClickHandler = null;
                break;
        }
    }

    public Service setService(UserService service) {
        Service newService;

        switch (service) {
            case CheckIn:
                newService = new CheckInService(this);
                break;
            case CheckOut:
                newService = new CheckOutService(this);
                break;
            case SelectScreen:
                newService = new SelectScreenService(this);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + service);
        }

        this.currentService = newService;

        return newService;
    }

    public MouseClickHandler getMouseClickHandler() {
        return mouseClickHandler;
    }

    public void setScreenText(String elementId, String content) {
        // TODO: enforcing Platform.runLater here is to temporary resolve the concurrency bug while updating the text label.
        Platform.runLater(() -> {
            touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_ChangeTextLabel, elementId + " " + content));
        });
    }

    //------------------------------------------------------------
    // run
    public void run() {
        Timer.setTimer(id, mbox, pollingTime);
        log.info(id + ": starting...");

        barcodeReaderMBox = appKickstarter.getThread("BarcodeReaderDriver").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        octopusCardReaderMBox = appKickstarter.getThread("OctopusCardReaderDriver").getMBox();
        lockerMBox = appKickstarter.getThread("Locker").getMBox();

        /**
         * Services
         */
        this.setService(UserService.SelectScreen); // select / checkin / checkout;
        DiagnosticService diagnosticService = new DiagnosticService(this);

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
                case SVR_ReserveRequest:
                case SVR_BarcodeVerified:
                case SVR_HealthPollRequest:
                    break;
                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }


            currentService.onMessage(msg);
            diagnosticService.onMessage(msg);
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
		String[] pos = msg.getDetails().trim().split("\\s+");
		int x = Integer.parseInt(pos[0]);
		int y = Integer.parseInt(pos[1]);

		// Use different handler according which screen is being displayed
        if(mouseClickHandler != null) {
            mouseClickHandler.handleButtonClick(x, y);
        }
    } // processMouseClicked
} // SLC
