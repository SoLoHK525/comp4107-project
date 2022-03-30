package SLC.Locker.Emulator;

import AppKickstarter.misc.Msg;
import SLC.HWHandler.HWHandler;
import SLC.SLCStarter;
import javafx.stage.Stage;

import java.io.IOException;

public class LockerEmulator extends HWHandler {
    private SLCStarter emulatorStarter;
    private String id;
    private IPresenter presenter;

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
    protected void processMsg(Msg msg) {

    }

    @Override
    protected void handlePoll() {

    }
}
