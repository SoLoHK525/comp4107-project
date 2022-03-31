package SLC.SLC.Handlers.MouseClick;

public class TouchScreenConfirmationMouseClickHandler extends MouseClickHandler {
    public TouchScreenConfirmationMouseClickHandler() {
        super(
                new Button("LeftButton", 101, 391, 160, 40),
                new Button("RightButton", 385, 391, 160, 40)
        );
    }
}
