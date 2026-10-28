package World.Values.Binds;

import World.Values.Bind;

import java.awt.*;

public class GrabBind extends Bind {
    protected int x, y;
    protected int mouseX, mouseY;
    protected boolean grabbed;

    public GrabBind(int[] key) {
        super(key); //add special key bind which will store info about camera grap

        this.x = 0;
        this.y = 0;
        this.mouseX = 0;
        this.mouseY = 0;
        this.grabbed = false;
    }

    public void grab(int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void grab(Point point, Point mouse) {
        this.x = point.x;
        this.y = point.y;
        this.mouseX = mouse.x;
        this.mouseY = mouse.y;
    }

    public int getDeltaX(int mouseX) {
        return mouseX - this.mouseX;
    }

    public int getDeltaY(int mouseY) {
        return mouseY - this.mouseY;
    }

    public int getAbsX(int x, double scale) {
        return this.x - (int)(getDeltaX(x) / scale);
    }

    public int getAbsY(int y, double scale) {
        return this.y - (int)(getDeltaY(y) / scale);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isGrabbed() {
        return grabbed;
    }

    public void setGrabbed(boolean grabbed) {
        this.grabbed = grabbed;
    }
}
