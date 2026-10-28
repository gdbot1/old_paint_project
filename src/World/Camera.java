package World;

import World.Values.Bind;
import World.Values.Point;

import java.awt.event.KeyEvent;

public class Camera {
    public Bind moveUpKeyBing = new Bind(new int[]{KeyEvent.VK_W,KeyEvent.VK_UP}), moveRightKeyBing = new Bind(new int[]{KeyEvent.VK_D,KeyEvent.VK_RIGHT}), moveDownKeyBing = new Bind(new int[]{KeyEvent.VK_S,KeyEvent.VK_DOWN}), moveLeftKeyBing = new Bind(new int[]{KeyEvent.VK_A,KeyEvent.VK_LEFT});
    public boolean moveUpAction = false, moveRightAction = false, moveDownAction = false, moveLeftAction = false;
    public double x, y, zoom, angle, actionX, actionY, actionZoom, actionAngle, smoothEffect, moveSpeed;

    public Camera (double x, double y, double zoom, double angle, double smoothEffect, double moveSpeed) {
        this.x=x;
        this.y=y;
        this.zoom=zoom;
        this.angle=angle;
        this.smoothEffect=smoothEffect;

        this.actionX=x;
        this.actionY=y;
        this.actionZoom=zoom;
        this.actionAngle=angle;

        this.moveSpeed=moveSpeed;
    }

    public Camera (Point point, double zoom, double angle, double smoothEffect) {
        this.x=point.x;
        this.y=point.y;
        this.zoom=zoom;
        this.angle=angle;
        this.smoothEffect=smoothEffect;

        this.actionX=point.x;
        this.actionY=point.y;
        this.actionZoom=zoom;
        this.actionAngle=angle;
    }

    public void setLocation (double x, double y) {
        this.x=x;
        this.y=y;

        this.actionX=x;
        this.actionY=y;
    }

    public void setLocation (Point point) {
        this.x=point.x;
        this.y=point.y;

        this.actionX=point.x;
        this.actionY=point.y;
    }

    public Point getLocation () {
        return new Point(this.x, this.y);
    }

    public double getZoom (double number, double center) {
        return (number-center)*zoom+center;
    }
    public double getReverseZoom (double number, double center) {
        return (number-center)/zoom+center;
    }
}
