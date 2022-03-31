package SLC.SLC.Handlers.MouseClick;

public class MainMenuMouseClickHandler extends MouseClickHandler {
    public MainMenuMouseClickHandler() {
        super(
                new Button("Button1", 0, 270, 300, 70),
                new Button("Button2", 0, 340, 300, 70),
                new Button("Button3", 0, 410, 300, 70),
                new Button("Button4", 640 - 300, 270, 300, 70),
                new Button("Button5", 640 - 300, 340, 300, 70),
                new Button("Button6", 640 - 300, 410, 300, 70)
        );
    }
}
