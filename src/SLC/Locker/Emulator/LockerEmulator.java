package SLC.Locker.Emulator;

import AppKickstarter.misc.Msg;
import SLC.HWHandler.HWHandler;
import SLC.SLCStarter;


public class LockerEmulator extends HWHandler {
    private String id;
    private LockerEmulatorPresenter presenter;
    private LockerEmulatorModel lockerModel;

    public LockerEmulator(String id, SLCStarter emulatorStarter) {
        super(id, emulatorStarter);
        this.id = id;
    }

    public void start() {
        this.lockerModel = new LockerEmulatorModel();
        this.presenter = new LockerEmulatorPresenter(lockerModel);
        this.presenter.start();
    }

    @Override
    protected void processMsg(Msg msg) {
        switch (msg.getType()) {
            case LK_Unlock:
                this.presenter.lockingOperation(msg.getDetails(), false);
            case LK_CheckStatus:
                String status = this.presenter.getLockStatus(msg.getDetails());
                slc.send(new Msg(id, mbox, Msg.Type.LK_ReturnStatus, "Locker ("+ msg.getDetails() + ") is now " + status + "!"));
                break;
        }
    }

    @Override
    protected void handlePoll() {
        switch (presenter.getPoll()) {
            case "ACK":
                slc.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                break;

            case "NAK":
                slc.send(new Msg(id, mbox, Msg.Type.PollNak, id + " is down!"));
                break;

            case "Ignore":
                // Just ignore.  do nothing!!
                break;
        }
    }
}
