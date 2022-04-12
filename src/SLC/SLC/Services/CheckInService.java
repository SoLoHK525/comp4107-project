package SLC.SLC.Services;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import SLC.SLC.DataStore.Dto.CheckIn.BarcodeVerificationDto;
import SLC.SLC.DataStore.Dto.CheckIn.CheckInDto;
import SLC.SLC.DataStore.Dto.CheckIn.VerifiedResponseDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;
import SLC.SLC.SLC;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import SLC.SLC.Screen;
import javafx.application.Platform;

public class CheckInService extends Service {
    //CONFIG PARAMS
    int maxRecallAllowed = 3;

    //STATE
    String curBarcode;
    String slotID;
    int HWRecallTimerID;
    RecallingHW curRecallingHW;
    int recalledCount;

    //REF
    MBox serverMBox;

    public CheckInService(SLC instance) {
        super(instance);
        curBarcode = "";
        HWRecallTimerID = -1;
        recalledCount = 0;

        slc.getLogger().info("Starting Check in service");

        slc.setScreen(Screen.Text);

        System.out.println("Running check in service");
        Timer.setTimer("check-in", slc.getMBox(), 1000);

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Welcome!");
            slc.setScreenText("subtitle", "XXX Smart Locker");
            slc.setScreenText("body", "Scan a barcode to checkin your delivery ticket.");
        });

        serverMBox = slc.getServerMBox();
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
                    }
                    break;
                case Error:
                case BR_ReturnStandby:
                    slc.EndService();
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
                    SendBarcodeToServer(message);
                    break;
                case SVR_BarcodeVerified:
                    VerifiedResponseDto res = SerializableDto.from(message.getDetails());
                    if (!res.verified) {
                        slc.EndService();
                    } else {
                        slotID = res.slotID;
                        slc.getLockerMBox().send(slc.GenerateMsg(Msg.Type.LK_Unlock, slotID));
                    }
                    break;
                case LK_Locked:
                    PerformCheckIn();
                    slc.getBarcodeReaderMBox().send(slc.GenerateMsg(Msg.Type.BR_GoStandby, ""));
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    private void PerformCheckIn() throws IOException {
        CheckInDto dto = new CheckInDto();
        dto.access_code = GenAccessCode();
        dto.barcode = curBarcode;
        dto.timestamp = (int) new Date().getTime();
        for (Locker locker : slc.getLockers()) {
            if (locker.getSlotId() == slotID) {
                slc.setCheckInPackage(dto.access_code, locker);
                break;
            }
        }

        serverMBox.send(slc.GenerateMsg(Msg.Type.SVR_CheckIn, dto.toBase64()));
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
        HWRecallTimerID = Timer.setTimer("hw-recaller", slc.getMBox(), 3000);
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
