package GUI;

import javax.swing.*;

public class Frame extends JFrame {
    public Frame (int x, int y, int width, int height, String title, boolean fullScreen, boolean undecorated, boolean resizable, boolean exit_on_close) {
        setLocation(x,y);
        setSize(width,height);
        setTitle(title);

        if (fullScreen) setExtendedState(MAXIMIZED_BOTH);
        setResizable(resizable);
        setUndecorated(undecorated);

        if (exit_on_close) setDefaultCloseOperation(EXIT_ON_CLOSE);
    }
}
