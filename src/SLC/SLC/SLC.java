package SLC.SLC;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;
import SLC.SLC.DataStore.Dto.CheckIn.ReservationRequestDto;
import SLC.SLC.DataStore.Dto.CheckIn.ReservedResponseDto;
import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.Dto.SLC.SLCStateDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.Interface.LockerSize;
import SLC.SLC.DataStore.SerializableDto;
import SLC.SLC.DataStore.Dto.CheckOut.CheckOutDto;
import SLC.SLC.Handlers.MouseClick.MainMenuMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.Services.CheckInService;
import SLC.SLC.Services.DiagnosticService;
import SLC.SLC.Services.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private int responseTime;
    private MBox barcodeReaderMBox;
    private MBox touchDisplayMBox;
    private MBox octopusCardReaderMBox;
    private MBox lockerMBox;
    private MBox serverMBox;
    private ArrayList<Locker> lockers;
    private Service currentService;
    private DiagnosticService diagnosticService;

    // Map access code to locker's slot id
    private HashMap<String, String> checkInPackage;

    private Screen screen;
    MouseClickHandler mouseClickHandler;

    private Runnable onScreenLoaded;

    //------------------------------------------------------------
    // SLC
    public SLC(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
        pollingTime = Integer.parseInt(appKickstarter.getProperty("SLC.PollingTime"));
    } // SLC

    public void setOnScreenLoaded(Runnable e) {
        this.onScreenLoaded = e;
    }

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
                break;
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

        this.log.info(id + ": switching to service: " + service.name());

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
        int pollingTimerID = Timer.setTimer(id, mbox, pollingTime);
        log.info(id + ": starting...");

        barcodeReaderMBox = appKickstarter.getThread("BarcodeReaderDriver").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        octopusCardReaderMBox = appKickstarter.getThread("OctopusCardReaderDriver").getMBox();
        lockerMBox = appKickstarter.getThread("Locker").getMBox();
        serverMBox = appKickstarter.getThread("ServerDriver").getMBox();

        lockers = initLockers();
        checkInPackage = new HashMap<>();
        this.loadState();

        /**
         * Services
         */
        currentService = null; // checkin / checkout;
        this.setService(UserService.SelectScreen); // select / checkin / checkout;
        diagnosticService = new DiagnosticService(this);

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case TD_MouseClicked:
                    log.info("MouseCLicked: " + msg.getDetails());
                    processMouseClicked(msg);
                    break;

                case TD_ScreenLoaded:
                    handleScreenLoaded();
                    break;

                case TimesUp:
                    if (Timer.getTimesUpMsgTimerId(msg) == pollingTimerID) {
                        pollingTimerID = Timer.setTimer(id, mbox, pollingTime);
                        log.info("Poll: " + msg.getDetails());
                        if (!diagnosticService.isBRShutDown())
                            barcodeReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        if (!diagnosticService.isTSShutDown())
                            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        if (!diagnosticService.isORRShutDown())
                            octopusCardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        if (!diagnosticService.isLKShutDown())
                            lockerMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
                        diagnosticService.setResponseTimer();
                        diagnosticService.lastUpdate = (int) (System.currentTimeMillis() / 1000L);
                    }
                    break;


                case OCR_CardRead:
                    String octopusCardNo = msg.getDetails();
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
                    this.saveState();
                    break;

                case SVR_ReserveRequest:
                    HandleReserve(msg.getDetails());
                    break;

                case BR_BarcodeRead:
                    log.info("[" + msg.getSender() + "(Received Barcode): " + msg.getDetails() + "]");
                    break;
            }

            // add service
            if(this.currentService != null) {
                this.currentService.onMessage(msg);
            }
            diagnosticService.onMessage(msg);
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    private void handleScreenLoaded() {
        if(this.onScreenLoaded != null) {
            this.onScreenLoaded.run();
            this.onScreenLoaded = null;
        }
    }

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
            ReservedResponseDto res = new ReservedResponseDto();
            for (Locker locker : lockers) {
                if (locker.getReserved() || locker.getContainPackage()) continue;
                if (locker.getSize() == msgDetail.lockerSize) {
                    locker.setReserved(true);
                    res.barcode = msgDetail.barcode;
                    res.hasLocker = true;
                    res.reservedLocker = locker.toDto();
                    getServerMBox().send(GenerateMsg(Msg.Type.SVR_ReservedResponse, res.toBase64()));
                    this.saveState();
                    return;
                }
            }

            getServerMBox().send(GenerateMsg(Msg.Type.SVR_ReservedResponse, res.toBase64()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //

    private void saveState() {
        try {
            File stateFile = new File("SLCState.bin");
            SLCStateDto dto = new SLCStateDto();

            ArrayList<LockerDto> serializableLockers = new ArrayList<>();

            for(Locker locker : lockers) {
                serializableLockers.add(locker.toDto());
            }

            dto.lockers = serializableLockers;
            dto.checkInPackage = this.checkInPackage;

            dto.save(stateFile);
            this.log.info(this.id + ": saved SLC state");
        } catch (IOException exception) {
            this.log.warning(this.id + ": error occurred while saving SLC state: " + exception.getMessage());
        }
    }

    private void loadState() {
        try {
            File stateFile = new File("SLCState.bin");

            if(stateFile.exists()) {
                SLCStateDto dto = SLCStateDto.from(stateFile);

                if(dto.lockers.size() > 0) {
                    this.lockers = new ArrayList<>();
                }

                for(LockerDto locker : dto.lockers) {
                    lockers.add(Locker.fromDto(locker));
                }

                this.checkInPackage = dto.checkInPackage;
                this.log.info(this.id + ": loaded SLC state");
            }
        } catch (IOException | ClassNotFoundException e) {
            this.log.warning(this.id + ": Failed to load state from SLCState.bin, error: " + e.getMessage());
        }
    }

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

        // System.out.println("Button: " + handler.getClickedButtonIndex(x, y));
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

    public MBox getOctopusCardReaderMBox() {
        return octopusCardReaderMBox;
    }

    public Locker getLockerBySlotId(String slotId) {
        for(Locker locker : lockers) {
            if(locker.getSlotId().equals(slotId)) {
                return locker;
            }
        }

        return null;
    }

    public HashMap<String, String> getCheckInPackage() {
        return checkInPackage;
    }

    public DiagnosticService getDiagnosticService(){
        return diagnosticService;
    }

    public void setCheckInPackage(String accessCode, String lockerSlotId) {
       this.checkInPackage.put(accessCode, lockerSlotId);
       this.saveState();
    }

    public String GetProperty(String propertyName) {
       return this.appKickstarter.getProperty(propertyName);
    }
    
    public int getResponseTime(){
        return Integer.parseInt(this.appKickstarter.getProperty("SLC.ResponseTime"));
    }

    public void setCheckInPackage(String accessCode, Locker locker) {
       this.checkInPackage.put(accessCode, locker.getSlotId());
    }
    //Getters

} // SLC
