package SLC.SLC;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;
import SLC.SLC.DataStore.Dto.CheckIn.ReservationRequestDto;
import SLC.SLC.DataStore.Dto.CheckIn.ReservedResponseDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.Interface.LockerSize;
import SLC.SLC.DataStore.SerializableDto;
import SLC.SLC.DataStore.Dto.CheckOut.CheckOutDto;
import SLC.SLC.Handlers.MouseClick.MainMenuMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.Services.CheckInService;
import SLC.SLC.Services.DiagnosticService;
import SLC.SLC.Services.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;
import SLC.SLC.Handlers.MouseClick.ConfirmationMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.PasscodeMouseClickHandler;
import SLC.SLC.Services.*;
import javafx.application.Platform;

import java.io.IOException;


//======================================================================
// SLC
public class SLC extends AppThread {
    private int pollingTime;
    private MBox barcodeReaderMBox;
    private MBox touchDisplayMBox;
    private MBox octopusCardReaderMBox;
    private MBox lockerMBox;
    private MBox serverMBox;
    private ArrayList<Locker> lockers;
    private Service currentService;
    private HashMap<String, Locker> checkInPackage;

    private String accessCode;
    private int pickUpTime;
    private double amount = 0;
    private String octopusCardNo;

    private Screen screen;
    MouseClickHandler mouseClickHandler;


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

    public String verifyAccessCode(String code) {
        //verify the received access code from the checkin package's hash map
        //if access code valid
        accessCode = code;
                    /*
                    pickUpTime = (int) (System.currentTimeMillis() / 1000L);
                    int storedDuration = pickUpTime - (checkinTime);
                    int late = 86400;
                    while(storedDuration > late) {
                        amount += 20;
                        late += 86400;
                    }

                    if(amount == 0) {
                        //lockerMBox.send(new Msg(id, mbox, Msg.Type.LK_Unlock, <corresponding slot id>));
                    }else {
                        octopusCardReaderMBox.send(new Msg(id, mbox, Msg.Type.OCR_GoActive, Double.toString(amount)));
                    }

                    return Double.toString(amount);
                     */
        //else
        return "false";
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
        serverMBox = appKickstarter.getThread("ServerDriver").getMBox();

        lockers = initLockers();
        checkInPackage = new HashMap<>();

        /**
         * Services
         */
        currentService = null; // checkin / checkout;
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
                    serverMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case OCR_CardRead:
                    octopusCardNo = msg.getDetails();
                    log.info("Octopus Card " + octopusCardNo + " is charged");
                    octopusCardReaderMBox.send(new Msg(id, mbox, Msg.Type.OCR_Charged, ""));
                    //lockerMBox.send(new Msg(id, mbox, Msg.Type.LK_Unlock, <corresponding slot id>));
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
                    if (msg.getSender().equals("BarcodeReaderDriver")) {
                        log.info("Activation Response: " + msg.getDetails());
                        break;
                    }
                    log.info("Activate: " + msg.getDetails());
                    barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.BR_GoActive, ""));
                    break;

                case BR_GoStandby:
                    if (msg.getSender().equals("BarcodeReaderDriver")) {
                        log.info("Standby Response: " + msg.getDetails());
                        break;
                    }
                    log.info("Standby: " + msg.getDetails());
                    barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.BR_GoStandby, ""));
                    break;

                case SVR_ReserveRequest:
                    HandleReserve(msg.getDetails());
                    break;

                case BR_BarcodeRead:
                    log.info("[" + msg.getSender() + "(Received Barcode): " + msg.getDetails() + "]");
                    break;

                case LK_Locked:
                    CheckOutDto checkOut = new CheckOutDto(accessCode, octopusCardNo, amount, pickUpTime);
                    try {
                        currentService.onMessage(new Msg(id, mbox, Msg.Type.SVR_CheckOut, checkOut.toBase64()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //**delete this access code's key value pair from the hash map

                    break;

                case SVR_BarcodeVerified:
                case SVR_HealthPollRequest:
                    // add service
                    if(this.currentService != null) {
                        this.currentService.onMessage(msg);
                    }
                    diagnosticService.onMessage(msg);
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
    // initLockers
    private ArrayList<Locker> initLockers() {
        ArrayList<Locker> lockers = new ArrayList<>();
        int numLockers = Integer.parseInt(this.appKickstarter.getProperty("Locker.NumLocker"));
        String[] largeLockerIDs = this.appKickstarter.getProperty("Locker.largeID").split(",");
        String[] mediumLockerIDs = this.appKickstarter.getProperty("Locker.mediumID").split(",");

        for (int i = 0; i < numLockers; i++) {
            //  slot ID for temp use
            String slotID = String.format("%04d", i);
            LockerSize size = LockerSize.SMALL;

            if (Arrays.asList(largeLockerIDs).contains(slotID)) {
                size = LockerSize.LARGE;
            } else if (Arrays.asList(mediumLockerIDs).contains(slotID)) {
                size = LockerSize.MEDIUM;
            }

            lockers.add(new Locker(slotID, size));
        }
        return lockers;
    } // initLockers


    //------------------------------------------------------------
    // HandleReserve
    private void HandleReserve(String detail) {
        try {
            ReservationRequestDto msgDetail = SerializableDto.from(detail);
            for (Locker locker : lockers) {
                if (locker.getReserved() || locker.getContainPackage()) continue;
                if (locker.getSize() == msgDetail.lockerSize) {
                    locker.setReserved(true);
                    ReservedResponseDto res = new ReservedResponseDto();
                    res.barcode = msgDetail.barcode;
                    res.hasLocker = true;
                    res.reservedLocker = locker.toDto();
                    String dtoStr = res.toBase64();
                    getServerMBox().send(GenerateMsg(Msg.Type.SVR_ReservedResponse, dtoStr));
                    return;
                }
            }

            getServerMBox().send(GenerateMsg(Msg.Type.SVR_ReservedResponse, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //

    //------------------------------------------------------------
    // GenerateMsg
    public Msg GenerateMsg(Msg.Type type, String detail) {
        return new Msg(id, mbox, type, detail);
    } // GenerateMsg

    //------------------------------------------------------------
    // EndService
    public void EndService() {
        this.currentService = null;
        this.setService(UserService.SelectScreen);
    } // EndService

    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        String[] pos = msg.getDetails().trim().split("\\s+");
        int x = Integer.parseInt(pos[0]);
        int y = Integer.parseInt(pos[1]);

        // Use different handler according which screen is being displayed
        MouseClickHandler handler = new MainMenuMouseClickHandler();


        System.out.println("Button: " + handler.getClickedButtonIndex(x, y));
        handler.handleButtonClick(x, y);
		// Use different handler according which screen is being displayed
        if(mouseClickHandler != null) {
            mouseClickHandler.handleButtonClick(x, y);
        }
    } // processMouseClicked


    //------------------------------------------------------------
    // Getters
    public Logger getLogger() {
        return log;
    }

    public ArrayList<Locker> getLockers() {
        return lockers;
    }

    public MBox getServerMBox() {
        //  Temp
        return serverMBox;
    }

    public MBox getBarcodeReaderMBox() {
        return barcodeReaderMBox;
    }

    public MBox getLockerMBox() {
        return lockerMBox;
    }

    public void setCheckInPackage(String accessCode, Locker locker) {
       this.checkInPackage.put(accessCode, locker);
    }
    //Getters

} // SLC
