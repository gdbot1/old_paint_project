package Paint;

import World.Values.Bind;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class Brush {
    public Bind paintMouseBind = new Bind(new int[]{MouseEvent.BUTTON1});
    public java.awt.Point mousePosition = null;
    public Point mouseLastPosition = null;
    public boolean paintAction = false;
    public Color color;
    public int width;
    public int mixType, paintingType;//
    public static final int mixing = 0, replace = 1, blending = 2;//mix types
    public static final int brush = 0, eraser = 1, filling = 2, selectorLasso = 3, selectorDefault = 4;//painting types
    public Image brushCursor = new ImageIcon().getImage();

    public Brush (Color color, int width, int paintingType ,int mixType) {
        this.color=color;
        this.width=width;
        this.paintingType=paintingType;
        this.mixType=mixType;
    }
}
