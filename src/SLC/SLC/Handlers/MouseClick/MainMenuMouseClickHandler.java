package SLC.SLC.Handlers.MouseClick;

public class MainMenuMouseClickHandler extends MouseClickHandler {
    public static class Buttons {
        public static final int Button0 = 0;
        public static final int Button1 = 1;
        public static final int Button2 = 2;
        public static final int Button3 = 3;
        public static final int Button4 = 4;
        public static final int Button5 = 5;
    }

    public MainMenuMouseClickHandler() {
        super(
                new Button("Button0", 0, 270, 300, 70),
                new Button("Button1", 0, 340, 300, 70),
                new Button("Button2", 0, 410, 300, 70),
                new Button("Button3", 640 - 300, 270, 300, 70),
                new Button("Button4", 640 - 300, 340, 300, 70),
                new Button("Button5", 640 - 300, 410, 300, 70)
        );
    }
}
