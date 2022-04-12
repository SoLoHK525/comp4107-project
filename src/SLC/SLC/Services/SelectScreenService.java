package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.Handlers.MouseClick.MainMenuMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.SLC;
import SLC.SLC.Screen;
import SLC.SLC.UserService;
import javafx.application.Platform;

public class SelectScreenService extends Service {
    public SelectScreenService(SLC instance) {
        super(instance);

        slc.setScreen(Screen.MainMenu);

        // Run after the controller is loaded by the JavaFX
        slc.setOnScreenLoaded(() -> {
            slc.setScreenText("button0", "Check In");
            slc.setScreenText("button1", "Check Out");
            slc.setScreenText("button2", "");
            slc.setScreenText("button3", "");
            slc.setScreenText("button4", "");
            slc.setScreenText("button5", "");

            MouseClickHandler handler = slc.getMouseClickHandler();

            handler.onClick(MainMenuMouseClickHandler.Buttons.Button0, () -> {
                slc.setService(UserService.CheckIn);
            });

            handler.onClick(MainMenuMouseClickHandler.Buttons.Button1, () -> {
                slc.setService(UserService.CheckOut);
            });
        });
    }

    @Override
    public void onMessage(Msg message) {

    }
}
