package Paint.Layers;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Layer {
    public BufferedImage image;
    public float transparency;
    public boolean visible;

    public Layer (BufferedImage image, float transparency, boolean visible) {
        this.image=image;
        this.transparency=transparency;
        this.visible=visible;
    }

    public Layer (Layer layer) {
        this.image=layer.image;
        this.transparency=layer.transparency;
        this.visible=layer.visible;
    }

    public Layer (int width, int height,float transparency, boolean visible, boolean background) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        if (!background) g2d.setColor(new Color(0,0,0,0));
        else g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
        this.image=image;
        this.transparency=transparency;
        this.visible=visible;
    }

    public void drawLine (Color color, int width, Point from, Point to) {
        Graphics2D graphics2D = this.image.createGraphics();

        graphics2D.setColor(color);
        graphics2D.setStroke(new BasicStroke(width,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));

        graphics2D.drawLine(from.x,from.y,to.x,to.y);

        graphics2D.dispose();
    }

    public void drawPolygon (Color color, int[] px, int[] py, int polygons) {
        Graphics2D graphics2D = this.image.createGraphics();

        graphics2D.setColor(color);

        graphics2D.fillPolygon(px,py,polygons);

        graphics2D.dispose();
    }

    public void drawRectangle (Color color, int x, int y, int width, int height) {
        Graphics2D graphics2D = this.image.createGraphics();

        graphics2D.setColor(color);

        graphics2D.fillRect(x,y,width,height);

        graphics2D.dispose();
    }
}
