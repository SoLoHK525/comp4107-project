package SLC.SLC.Handlers.MouseClick;

import java.util.*;

public abstract class MouseClickHandler {
    protected List<Button> buttons;
    protected Map<Integer, OnButtonClick> handlers = new HashMap<>();

    protected MouseClickHandler(Button ...args) {
        buttons = new ArrayList<>(Arrays.asList(args));
    }

    static class Button {
        String label;
        int x, y, width, height;

        Button(String label, int x, int y, int width, int height) {
            this.label = label;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    public void onClick(int index, OnButtonClick callback) {
        this.handlers.put(index, callback);
    }

    public void handleButtonClick(int x, int y) {
        int index = getClickedButtonIndex(x, y);

        if (index == -1) return; // button not found

        OnButtonClick delegate = this.handlers.get(index);

        if(delegate != null) {
            this.handlers.get(index).emit();
        }
    }

    public int getClickedButtonIndex(int x, int y) {
        int counter = 0;

        for(Button btn : buttons) {
            if(btn.x <= x && x <= btn.width + btn.x) {
                if(btn.y <= y && y <= btn.height + btn.y) {
                    return counter;
                }
            }

            counter++;
        }

        return -1;
    }
}
