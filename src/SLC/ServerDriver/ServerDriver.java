package SLC.ServerDriver;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.Msg;
import SLC.HWHandler.HWHandler;

import java.io.IOException;

public class ServerDriver extends HWHandler {

    public ServerDriver(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    }

    @Override
    protected void processMsg(Msg msg) {
        String payload = msg.getDetails();

        switch (msg.getType()) {
            case SVR_ReservedResponse:
                this.handleReservedResponse(payload);
                break;
            case SVR_VerifyBarcode:
                this.handleVerifyBarcode(payload);
                break;
            case SVR_CheckIn:
                this.handleCheckIn(payload);
                break;
            case SVR_CheckOut:
                this.handleCheckOut(payload);
                break;
            case SVR_HealthPollResponse:
                this.handleHealthPollResponse(payload);
                break;
            default:
                log.warning(id + ": unknown message type: [" + msg + "]");
        }
    }

    protected void handleReservedResponse(String payload) {
        log.info(id + ": handleReservedResponse");
    }

    protected void handleVerifyBarcode(String payload) {
        log.info(id + ": handleVerifyBarcode");
    }

    protected void handleCheckIn(String payload) {
        log.info(id + ": handleCheckIn");
    }

    protected void handleCheckOut(String payload) {
        log.info(id + ": handleCheckOut");
    }

    protected void handleHealthPollResponse(String payload) {
        log.info(id + ": handleHealthPollResponse");
    }

    @Override
    protected void handlePoll() {
        log.info(id + ": Handle Poll");
    }
}
