package SLC.ServerDriver.Emulator;

import AppKickstarter.misc.Msg;
import SLC.SLC.DataStore.Dto.CheckIn.*;
import SLC.SLC.DataStore.Dto.CheckOut.CheckOutDto;
import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.Interface.LockerSize;
import SLC.SLCStarter;
import SLC.ServerDriver.ServerDriver;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

public class ServerEmulator extends ServerDriver {
    private SLCStarter slcStarter;
    private String id;
    private Stage stage;
    private ServerEmulatorController serverEmulatorController;

    // Key: Barcode, Value: Locker
    private final HashMap<String, LockerDto> barcodeToLockerMap = new HashMap<>();

    // Key: access code Value: barcode
    private final HashMap<String, String> accessCodeToBarcodeMap = new HashMap<>();

    public ServerEmulator(String id, SLCStarter slcStarter) {
        super(id, slcStarter);
        this.slcStarter = slcStarter;
        this.id = id;
    }

    public void start() throws Exception {
        Parent root;
        stage = new Stage();
        FXMLLoader loader = new FXMLLoader();

        String fxmlName = "ServerEmulator.fxml";
        loader.setLocation(ServerEmulatorController.class.getResource(fxmlName));
        root = loader.load();

        serverEmulatorController = loader.getController();
        serverEmulatorController.initialize(id, slcStarter, log, this);

        stage.initStyle(StageStyle.DECORATED);
        stage.setScene(new Scene(root, 504, 655));
        stage.setTitle("SLC Server");
        stage.setResizable(false);
        stage.setOnCloseRequest((WindowEvent event) -> {
            slcStarter.stopApp();
            Platform.exit();
        });
        stage.show();
    }

    public void addOrder() {
        try {
            Random random = new Random();
            StringBuilder barcode = new StringBuilder();

            for (int i = 0; i < 8; i++) {
                barcode.append((char) ('0' + random.nextInt(10)));
                if (i == 3) barcode.append('-');
            }

            ReservationRequestDto dto = new ReservationRequestDto();
            dto.barcode = barcode.toString();
            dto.lockerSize = random.nextBoolean() ? LockerSize.SMALL : random.nextBoolean() ? LockerSize.MEDIUM : LockerSize.LARGE;

            slc.send(new Msg(id, mbox, Msg.Type.SVR_ReserveRequest, dto.toBase64()));
        }catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleReservedResponse(String payload) {
        try {
            ReservedResponseDto response = ReservedResponseDto.from(payload);

            if(response.hasLocker) {
                // when locker is reserved
                this.barcodeToLockerMap.put(response.barcode, response.reservedLocker);
                this.updateOrderTextArea();
            }else{
                this.log.warning(id + ": Failed to reserve locker");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void handleVerifyBarcode(String payload) {
        try {
            BarcodeVerificationDto dto = BarcodeVerificationDto.from(payload);
            VerifiedResponseDto verifiedResponseDto = new VerifiedResponseDto();

            if(this.accessCodeToBarcodeMap.containsValue(dto.barcode)) {
                verifiedResponseDto.verified = false;
                verifiedResponseDto.slotID = "";
            }else{
                LockerDto locker = this.barcodeToLockerMap.get(dto.barcode);

                if(locker != null) {
                    verifiedResponseDto.verified = true;
                    verifiedResponseDto.slotID = locker.slotId;
                }else{
                    verifiedResponseDto.verified = false;
                    verifiedResponseDto.slotID = "";
                    this.log.warning("Barcode: " + dto.barcode + "does not have a reserved locker");
                }
            }

            this.slc.send(new Msg(id, mbox, Msg.Type.SVR_BarcodeVerified, verifiedResponseDto.toBase64()));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        log.info(id + ": handleVerifyBarcode");
    }

    @Override
    protected void handleCheckIn(String payload) {
        try {
            CheckInDto dto = CheckInDto.from(payload);
            this.accessCodeToBarcodeMap.put(dto.access_code, dto.barcode);
            this.updateAccessCodeToLockerTextArea();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        log.info(id + ": handleCheckIn");
    }

    @Override
    protected void handleCheckOut(String payload) {
        try {
            CheckOutDto dto = CheckOutDto.from(payload);
            
            log.info("Checkout: " + dto.access_code);

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        log.info(id + ": handleCheckOut");
    }

    @Override
    protected void handleHealthPollResponse(String payload) {
        log.info(id + ": handleHealthPollResponse");
    }

    @Override
    protected void handlePoll() {
        // TODO: fucking make a controller for this shit
        switch (serverEmulatorController.pollRespCBox.toString()) {
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

    private void updateOrderTextArea() {
        StringBuilder buffer = new StringBuilder();

        for (HashMap.Entry<String, LockerDto> entry : barcodeToLockerMap.entrySet()) {
            String barcode = entry.getKey();
            LockerDto locker = entry.getValue();

            buffer.append(String.format("%s - %s\n", barcode, locker.slotId));
        }

        this.serverEmulatorController.orderTextArea.setText(buffer.toString());
    }

    private void updateAccessCodeToLockerTextArea() {
        StringBuilder buffer = new StringBuilder();

        for (HashMap.Entry<String, String> entry : accessCodeToBarcodeMap.entrySet()) {
            String access_code = entry.getKey();
            String barcode = entry.getValue();

            buffer.append(String.format("%s - %s\n", access_code, barcode));
        }

        this.serverEmulatorController.accessCodeToLockerTextArea.setText(buffer.toString());
    }
}
