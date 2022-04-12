package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.DataStore.Dto.CheckOut.CheckOutDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.Handlers.MouseClick.ConfirmationMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.Handlers.MouseClick.PasscodeMouseClickHandler;
import SLC.SLC.SLC;
import SLC.SLC.Screen;
import SLC.SLC.UserService;

import java.io.IOException;
import java.util.HashMap;

public class CheckOutService extends Service {
    private State state;

    /**
        Session State
     */
    private String accessCode;
    private int pickUpTime;
    private double amount;
    private String octopusCardNo;
    private Locker locker;

    private enum State {
        PASSCODE,
        OPENED_LOCKER,
        WAITING_FOR_PAYMENT
    }

    public CheckOutService(SLC instance) {
        super(instance);
        initialScreen();
    }

    void initialScreen() {
        slc.setScreen(Screen.Passcode);
        this.state = State.PASSCODE;

        PasscodeMouseClickHandler handler = (PasscodeMouseClickHandler) slc.getMouseClickHandler();

        // need to update the passcode to the screen like this
        // kinda like how react update textfields
        handler.onPasscodeChange((String passcode) -> {
            slc.setScreenText("passcode", passcode);
        });

        // when user click on the "Enter" pad
        handler.onPasscodeEnter((String passcode) -> {
            System.out.println("Entered access code: " + passcode);
            Locker locker = getLockerByAccessCode(passcode);

            if (locker == null) {
                this.displayInvalidAccessCodeError();
            } else {
                amount = calculateFine(passcode);

                if (amount == 0.0) {
                    openLocker(locker);
                } else {
                    slc.getOctopusCardReaderMBox().send(slc.GenerateMsg(Msg.Type.OCR_GoActive, Double.toString(amount)));
                    this.displayFinedMessage(amount);
                    this.state = State.WAITING_FOR_PAYMENT;
                    this.locker = locker;
                }
            }
        });

        /**
         * setScreenText must run in Platform.runLater because
         * it has to queue after the scene to make sure the
         * scene is drawn before editing the text
         */
        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Please enter the access code.");
        });
    }

    private void openLocker(Locker locker) {
        slc.getLockerMBox().send(slc.GenerateMsg(Msg.Type.LK_Unlock, locker.getSlotId()));
        this.state = State.OPENED_LOCKER;
        this.displayLockerOpenedMessage();
    }

    private Locker getLockerByAccessCode(String code) {
        //verify the received access code from the hash map "checkInPackage"
        HashMap<String, Locker> checkInPackage = slc.getCheckInPackage();

        return checkInPackage.get(code);
    }

    private double calculateFine(String code) {
        HashMap<String, Locker> checkInPackage = slc.getCheckInPackage();

        Locker locker = checkInPackage.get(code);

        accessCode = code;
        pickUpTime = (int) (System.currentTimeMillis() / 1000L);
        int storedDuration = pickUpTime - locker.getLastUpdate();
        int late = 86400; //24hr

        while (storedDuration > late) {
            amount += 20;   //charge $20 for each 24hr
            late += 86400;
        }

        amount += 0.5; // TODO: Remove debug variables

        return amount;
    }

    private void displayLockerOpenedMessage() {
        slc.setScreen(Screen.Text);

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Success");
            slc.setScreenText("subtitle", "You can pick up you package now.");
            slc.setScreenText("body", "Remember to close the locker after taking the package!");
        });
    }

    private void displayFinedMessage(double amount) {
        slc.setScreen(Screen.Text);

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Yikes!");
            slc.setScreenText("subtitle", "Pay fine using an octopus card.");
            slc.setScreenText("body", "Since you were late to pickup the package, you have been fined for $" + amount + "!");
        });
    }

    private void displayInvalidAccessCodeError() {
        slc.setScreen(Screen.Confirmation);

        MouseClickHandler handler = slc.getMouseClickHandler();

        handler.onClick(ConfirmationMouseClickHandler.Buttons.LeftButton, () -> {
            slc.setService(UserService.CheckOut);
        });

        handler.onClick(ConfirmationMouseClickHandler.Buttons.RightButton, () -> {
            slc.setService(UserService.SelectScreen);
        });

        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("title", "Oops");
            slc.setScreenText("subtitle", "Invalid Access Code.");
            slc.setScreenText("leftButton", "Try Again");
            slc.setScreenText("rightButton", "Back to Main Menu");
        });
    }

    private void onPackageTaken() {
        HashMap<String, Locker> checkInPackage = slc.getCheckInPackage();

        CheckOutDto checkOut = new CheckOutDto(accessCode, octopusCardNo, amount, pickUpTime);
        try {
            slc.getServerMBox().send(slc.GenerateMsg(Msg.Type.SVR_CheckOut, checkOut.toBase64()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //**delete this access code's key value pair from the hash map
        checkInPackage.remove(accessCode);

        slc.setService(UserService.SelectScreen);
    }

    private void onOctopusCardRead(String details) {
        if(this.state == State.WAITING_FOR_PAYMENT) {
            octopusCardNo = details;
            slc.getLogger().info("Octopus Card " + octopusCardNo + " is charged");
            this.openLocker(this.locker);
            slc.getOctopusCardReaderMBox().send(slc.GenerateMsg(Msg.Type.OCR_Charged, ""));
        } else {
            throw new RuntimeException("Received OCR_CardRead during state: " + state.name());
        }
    }

    @Override
    public void onMessage(Msg message) {
        switch (message.getType()) {
            case LK_Locked:
                onPackageTaken();
                break;
            case OCR_CardRead:
                onOctopusCardRead(message.getDetails());
                break;
        }
        //serverMBox.send(new Msg(id, mbox, Msg.Type.SVR_CheckOut, message.getDetails()));
    }
}
