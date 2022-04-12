package SLC.SLC.Services;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import SLC.SLC.DataStore.Dto.CheckIn.BarcodeVerificationDto;
import SLC.SLC.DataStore.Dto.CheckIn.CheckInDto;
import SLC.SLC.DataStore.Dto.CheckIn.VerifiedResponseDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;
import SLC.SLC.Handlers.MouseClick.ConfirmationMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.SLC;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

import SLC.SLC.Screen;
import SLC.SLC.UserService;
import javafx.application.Platform;

public class CheckInService extends Service {
    //CONFIG PARAMS
    int maxRecallAllowed;
    int serviceTimeout;

    //STATE
    String curBarcode;
    String slotID;
    int HWRecallTimerID;
    RecallingHW curRecallingHW;
    int recalledCount;

    int EndServiceTimerID;

    public CheckInService(SLC instance) {
        super(instance);
        serviceTimeout = Integer.parseInt(slc.GetProperty("Service.TimeOut"));
        maxRecallAllowed = Integer.parseInt(slc.GetProperty("Service.MaxHWRecall"));

        curBarcode = "";
        HWRecallTimerID = -1;
        recalledCount = 0;

        slc.getLogger().info("Starting Check in service");

        slc.setScreen(Screen.Text);

        System.out.println("Running check in service");
       UpdateTimeoutTimer(serviceTimeout);

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Welcome!");
            slc.setScreenText("subtitle", "XXX Smart Locker");
            slc.setScreenText("body", "Scan a barcode to checkin your delivery ticket.");
        });

        MBox barcodeReaderMBox = slc.getBarcodeReaderMBox();
        barcodeReaderMBox.send(slc.GenerateMsg(Msg.Type.BR_GoActive, ""));
    }

    @Override
    public void onMessage(Msg message) {
        try {
            switch (message.getType()) {
                case TimesUp:
                    if (Timer.getTimesUpMsgTimerId(message) == HWRecallTimerID) {
                        Recall();
                    }else if(Timer.getTimesUpMsgTimerId(message) == EndServiceTimerID) {
                        slc.EndService();
                    }
                    break;
                case Error:
                case BR_ReturnStandby:
                    if(!curBarcode.equals("")) {
                        this.displaySuccessMessage();
                    } else {
                        this.displayInactiveMessage("Inactive Barcode reader", "Come back later");
                    }
                    break;
                case BR_ReturnActive:
                    if (!curBarcode.equals("")) {
                        if (recalledCount == 0) {
                            WaitForRecall(slc.getBarcodeReaderMBox(), slc.GenerateMsg(Msg.Type.BR_GoStandby, ""));
                        } else if (recalledCount >= maxRecallAllowed) {
                            slc.EndService();
                        }
                    }
                    break;
                case BR_BarcodeRead:
                    UpdateTimeoutTimer(serviceTimeout);
                    SendBarcodeToServer(message);
                    break;
                case SVR_BarcodeVerified:
                    VerifiedResponseDto res = SerializableDto.from(message.getDetails());
                    if (!res.verified) {
                        this.displayInvalidBarcodeError();
                    } else {
                        slotID = res.slotID;
                        slc.getLockerMBox().send(slc.GenerateMsg(Msg.Type.LK_Unlock, slotID));
                        this.displayTakePackageMessage(slotID);
                    }
                    break;
                case LK_Locked:
                    UpdateTimeoutTimer(serviceTimeout);
                    PerformCheckIn();
                    slc.getBarcodeReaderMBox().send(slc.GenerateMsg(Msg.Type.BR_GoStandby, ""));
                    break;
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void UpdateTimeoutTimer(int sleepTime) {
        Timer.cancelTimer("end-service", slc.getMBox(), EndServiceTimerID);
        EndServiceTimerID = Timer.setTimer("end-service", slc.getMBox(), sleepTime);
    }

    private void displayInvalidBarcodeError() {
        slc.setScreen(Screen.Confirmation);

        MouseClickHandler handler = slc.getMouseClickHandler();

        handler.onClick(ConfirmationMouseClickHandler.Buttons.LeftButton, () -> {
            slc.setService(UserService.CheckIn);
        });

        handler.onClick(ConfirmationMouseClickHandler.Buttons.RightButton, () -> {
            slc.setService(UserService.SelectScreen);
        });

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Invalid barcode");
            slc.setScreenText("subtitle", "Barcode invalid or it has been used already.");
            slc.setScreenText("leftButton", "Try Again");
            slc.setScreenText("rightButton", "Back to Main Menu");
        });
    }

    private void displaySuccessMessage() {
        slc.setScreen(Screen.Confirmation);

        MouseClickHandler handler = slc.getMouseClickHandler();

        handler.onClick(ConfirmationMouseClickHandler.Buttons.LeftButton, () -> {
            slc.setService(UserService.CheckIn);
        });

        handler.onClick(ConfirmationMouseClickHandler.Buttons.RightButton, () -> {
            slc.setService(UserService.SelectScreen);
        });

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Success");
            slc.setScreenText("subtitle", "The package has been stored successfully.");
            slc.setScreenText("leftButton", "Continue Checkin");
            slc.setScreenText("rightButton", "Main Menu");
        });
    }

    private void displayInactiveMessage(String subtitle, String msgContent) {
       UpdateTimeoutTimer(Integer.parseInt(slc.GetProperty("Service.MsgScreenDuration")));
        slc.setScreen(Screen.Text);
        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Yikes!");
            slc.setScreenText("subtitle", subtitle);
            slc.setScreenText("body", msgContent);
        });
    }

    private void displayTakePackageMessage(String slotID) {
        slc.setScreen(Screen.Text);

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Place the package");
            slc.setScreenText("subtitle", "You may put the package inside the [" + slotID + "] locker now.");
            slc.setScreenText("body", "Remember to close the locker after putting the package!");
        });
    }

    private void PerformCheckIn() throws IOException {
        CheckInDto dto = new CheckInDto();
        dto.access_code = GenAccessCode();
        dto.barcode = curBarcode;
        dto.timestamp = (int) new Date().getTime();

        for (Locker locker : slc.getLockers()) {
            if (Objects.equals(locker.getSlotId(), slotID)) {
                slc.setCheckInPackage(dto.access_code, locker);
                break;
            }
        }

        slc.getServerMBox().send(slc.GenerateMsg(Msg.Type.SVR_CheckIn, dto.toBase64()));
    }

    private String GenAccessCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }

    private void SendBarcodeToServer(Msg msg) throws IOException {
        BarcodeVerificationDto dto = new BarcodeVerificationDto();
        curBarcode = msg.getDetails();
        dto.barcode = curBarcode;
        slc.getServerMBox().send(slc.GenerateMsg(Msg.Type.SVR_VerifyBarcode, dto.toBase64()));
    }

    private void WaitForRecall(MBox mBox, Msg msg) {
        curRecallingHW = new RecallingHW(mBox, msg);
        HWRecallTimerID = Timer.setTimer("hw-recaller", slc.getMBox(), Integer.parseInt(slc.GetProperty("Service.HWRecallInterval")));
    }

    private void Recall() {
        curRecallingHW.mBox.send(curRecallingHW.msg);
        recalledCount++;
        WaitForRecall(curRecallingHW.mBox, curRecallingHW.msg);
    }

    private class RecallingHW {
        public MBox mBox;
        public Msg msg;

        public RecallingHW(MBox mBox, Msg msg) {
            this.mBox = mBox;
            this.msg = msg;
        }
    }
}
