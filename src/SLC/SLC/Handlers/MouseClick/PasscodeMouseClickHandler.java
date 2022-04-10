package SLC.SLC.Handlers.MouseClick;

public class PasscodeMouseClickHandler extends MouseClickHandler {
    private String buffer = "";
    private PasscodeHandler onPasscodeChangeHandler;
    private PasscodeHandler onPasscodeEnterHandler;

    public interface PasscodeHandler {
        void handle(String passcode);
    }

    public static class Buttons {
        public static final int Button0 = 0;
        public static final int Button1 = 1;
        public static final int Button2 = 2;
        public static final int Button3 = 3;
        public static final int Button4 = 4;
        public static final int Button5 = 5;
        public static final int Button6 = 6;
        public static final int Button7 = 7;
        public static final int Button8 = 8;
        public static final int Button9 = 9;
        public static final int Backspace = 10;
        public static final int Clear = 11;
        public static final int Enter = 12;
    }

    public PasscodeMouseClickHandler() {
        super(
                new Button("0", 319, 401, 44, 44),
                new Button("1", 251, 220, 44, 44),
                new Button("2", 318, 220, 44, 44),
                new Button("3", 382, 220, 44, 44),
                new Button("4", 251, 280, 44, 44),
                new Button("5", 319, 280, 44, 44),
                new Button("6", 382, 280, 44, 44),
                new Button("7", 251, 341, 44, 44),
                new Button("8", 319, 341, 44, 44),
                new Button("9", 382, 341, 44, 44),
                new Button("Backspace", 382, 158, 44, 44),
                new Button("Clear", 251, 401, 44, 44),
                new Button("Enter", 382, 401, 44, 44)
        );

        this.onClick(Buttons.Button0, () -> {
            buffer += "0";
            changePasscode();
        });

        this.onClick(Buttons.Button1, () -> {
            buffer += "1";
            changePasscode();
        });

        this.onClick(Buttons.Button2, () -> {
            buffer += "2";
            changePasscode();
        });

        this.onClick(Buttons.Button3, () -> {
            buffer += "3";
            changePasscode();
        });

        this.onClick(Buttons.Button4, () -> {
            buffer += "4";
            changePasscode();
        });

        this.onClick(Buttons.Button5, () -> {
            buffer += "5";
            changePasscode();
        });

        this.onClick(Buttons.Button6, () -> {
            buffer += "6";
            changePasscode();
        });

        this.onClick(Buttons.Button7, () -> {
            buffer += "7";
            changePasscode();
        });

        this.onClick(Buttons.Button8, () -> {
            buffer += "8";
            changePasscode();
        });

        this.onClick(Buttons.Button9, () -> {
            buffer += "9";
            changePasscode();
        });

        this.onClick(Buttons.Backspace, () -> {
            int len = buffer.length();
            if (len == 0) return;

            buffer = buffer.substring(0, len - 1);
            changePasscode();
        });

        this.onClick(Buttons.Clear, () -> {
            buffer = "";
            changePasscode();
        });

        this.onClick(Buttons.Enter, () -> {
            if (this.onPasscodeEnterHandler != null) {
                this.onPasscodeEnterHandler.handle(buffer);
            }
        });
    }

    private void changePasscode() {
        if (onPasscodeChangeHandler != null) {
            onPasscodeChangeHandler.handle(this.buffer);
        }
    }

    public void onPasscodeChange(PasscodeHandler handler) {
        this.onPasscodeChangeHandler = handler;
    }

    public void onPasscodeEnter(PasscodeHandler handler) {
        this.onPasscodeEnterHandler = handler;
    }
}
