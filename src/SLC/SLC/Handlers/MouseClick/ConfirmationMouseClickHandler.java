package SLC.SLC.Handlers.MouseClick;

public class ConfirmationMouseClickHandler extends MouseClickHandler {
    public static class Buttons {
        public static final int LeftButton = 0;
        public static final int RightButton = 1;
    }

    public ConfirmationMouseClickHandler() {
        super(
                new Button("LeftButton", 101, 391, 160, 40),
                new Button("RightButton", 385, 391, 160, 40)
        );
    }
}
