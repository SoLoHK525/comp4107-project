package SLC.SLC.Services;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import SLC.SLC.DataStore.Dto.CheckIn.BarcodeVerificationDto;
import SLC.SLC.DataStore.Dto.CheckIn.CheckInDto;
import SLC.SLC.DataStore.Dto.CheckIn.VerifiedResponseDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.DataStore.SerializableDto;
import SLC.SLC.SLC;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

public class CheckInService extends Service {
    //STATE
    String curBarcode;
    String slotID;

    //REF
    MBox serverMBox;
    public CheckInService(SLC instance) {
        super(instance);
        slc.getLogger().info("Starting Check in service");

        serverMBox = slc.getServerMBox();
        MBox barcodeReaderMBox = slc.getBarcodeReaderMBox();
        barcodeReaderMBox.send(slc.GenerateMsg(Msg.Type.BR_GoActive, ""));
    }

    @Override
    public void onServerMessage(Msg msg) {
        try {
            switch (msg.getType()) {
                case Error:
                    slc.EndService();
                    break;
                case BR_BarcodeRead:
                    SendBarcodeToServer(msg);
                    break;
                case SVR_BarcodeVerified:
                    VerifiedResponseDto res = SerializableDto.from(msg.getDetails());
                    if(!res.verified) {
                        slc.EndService();
                    } else{
                        slotID = res.slotID;
                        slc.getLockerMBox().send(slc.GenerateMsg(Msg.Type.LK_Unlock, slotID));
                    }
                    break;
                case LK_Locked:
                    PerformCheckIn();

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void PerformCheckIn() throws IOException {
        CheckInDto dto = new CheckInDto();
        dto.access_code = GenAccessCode();
        dto.barcode = curBarcode;
        dto.timestamp = (int) new Date().getTime();
        for (Locker locker : slc.getLockers()) {
            if(locker.getSlotId() == slotID) {
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
}
