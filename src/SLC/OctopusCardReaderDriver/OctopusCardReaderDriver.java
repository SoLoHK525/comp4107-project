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
                handleGoActive(msg.getDetails());
                break;

            case OCR_GoStandby:
                handleGoStandby();
                break;

            case OCR_Charged:
                handleCharged();
                handleGoStandby();
                break;

            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void handleGoActive(String amount) {
        log.info(id + ": Go Active");
    }

    protected void handleGoStandby() {
        log.info(id + ": Go Standby");
    }

    protected void handleCharged() {
        log.info(id + ": charged an Octopus Card");
    }

    @Override
    protected void handlePoll() {
        log.info(id + ": Handle Poll");
    }
}
