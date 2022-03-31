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
        switch (msg.getType()) {
            case OCR_CardRead:
                slc.send(new Msg(id, mbox, Msg.Type.OCR_CardRead, msg.getDetails()));
                break;

            case OCR_GoActive:
                handleGoActive();
                break;

            case OCR_GoStandby:
                handleGoStandby();
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void handleGoActive() {
        log.info(id + ": Go Active");
    }

    protected void handleGoStandby() {
        log.info(id + ": Go Standby");
    }

    @Override
    protected void handlePoll() {
        log.info(id + ": Handle Poll");
    }
}
