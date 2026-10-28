package World.Values;

import World.Camera;

import java.awt.*;

public class Hitbox {
    public double x, y, width, height;

    public Hitbox (double x, double y, double width, double height) {
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
    }

    public Hitbox (Hitbox hitbox) {
        this.x=hitbox.x;
        this.y=hitbox.y;
        this.width=hitbox.width;
        this.height=hitbox.height;
    }

    public void setSize (double width, double height) {
        this.width=width;
        this.height=height;
    }

    public void setSize (Dimension dimension) {
        this.width=dimension.width;
        this.height=dimension.height;
    }

    public void setLocation (double x, double y) {
        this.x=x;
        this.y=y;
    }

    public void setLocation (Point point) {
        this.x=point.x;
        this.y=point.y;
    }

    public Point getLocation () {
        return new Point(x,y);
    }

    public Dimension getSize () {
        return new Dimension(width,height);
    }

    public boolean contains (Hitbox h) {
        return x+width>h.x&&x<h.x+h.width&&y+height>h.y&&y<h.y+h.height;
    }

    public boolean contains (Point p) {
        return p.x>x&&p.x<x+width&&p.y>y&&p.y<y+height;
    }

    public void draw (Graphics g, Camera camera, Frame frame) {
        Graphics2D g2D = (Graphics2D) g;
        double x = this.x,y = this.y,width = this.width,height = this.height;
        if (camera!=null&&frame!=null) {
            x = camera.getZoom(this.x, (double) frame.getWidth() / 2 + camera.x) - camera.x;
            y = camera.getZoom(this.y, (double) frame.getHeight() / 2 + camera.y) - camera.y;
            width = this.width * camera.zoom + 1;
            height = this.height * camera.zoom + 1;
        }

        g.setColor(Color.red);
        g.drawRect((int)x,(int)y,(int)width,(int)height);
    }

    public void fill (Graphics g, Camera camera, Frame frame) {
        Graphics2D g2D = (Graphics2D) g;
        double x = this.x,y = this.y,width = this.width,height = this.height;
        if (camera!=null&&frame!=null) {
            x = camera.getZoom(this.x, (double) frame.getWidth() / 2 + camera.x) - camera.x;
            y = camera.getZoom(this.y, (double) frame.getHeight() / 2 + camera.y) - camera.y;
            width = this.width * camera.zoom + 1;
            height = this.height * camera.zoom + 1;
        }

        g.setColor(Color.red);
        g.fillRect((int)x,(int)y,(int)width,(int)height);
    }
}
