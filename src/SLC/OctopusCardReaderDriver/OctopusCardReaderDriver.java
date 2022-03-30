package SLC.OctopusCardReaderDriver;

import SLC.HWHandler.HWHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

public class OctopusCardReaderDriver extends HWHandler {

    public OctopusCardReaderDriver(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    @Override
    protected void processMsg(Msg msg) {

    }

    @Override
    protected void handlePoll() {

    }
}
