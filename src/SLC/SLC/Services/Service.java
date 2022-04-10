package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.SLC;

public abstract class Service {
    protected final SLC slc;

    public Service(SLC instance) {
        this.slc = instance;
    }

    public abstract void onMessage(Msg message);
}
