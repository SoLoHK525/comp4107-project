package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.SLC;

public class CheckOutService extends Service {
    public CheckOutService(SLC instance) {
        super(instance);
    }

    @Override
    public void onServerMessage(Msg message) {
        //serverMBox.send(new Msg(id, mbox, Msg.Type.SVR_CheckOut, message.getDetails()));

    }
}
