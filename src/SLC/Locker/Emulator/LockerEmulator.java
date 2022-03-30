package SLC.Locker.Emulator;

import AppKickstarter.misc.Msg;
import SLC.Locker.LockerDriver;
import SLC.SLCStarter;


public class LockerEmulator extends LockerDriver {
    private SLCStarter emulatorStarter;
    private String id;
    private LockerEmulatorPresenter presenter;

    public LockerEmulator(String id, SLCStarter emulatorStarter) {
        super(id, emulatorStarter);
        this.emulatorStarter = emulatorStarter;
        this.id = id;
    }

    public void start() {
        this.presenter = new LockerEmulatorPresenter();
        this.presenter.start();
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
