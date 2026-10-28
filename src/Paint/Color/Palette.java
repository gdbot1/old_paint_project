package Paint.Color;

import World.Values.Bind;
import World.Values.Hitbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import GUI.Frame;

public class Palette extends JPanel implements ActionListener, MouseListener {
    public boolean mouseAction = false, huiAction = false, alphaAction = false, SBAction = false;
    public Bind selectColorMouseBind = new Bind(new int[]{MouseEvent.BUTTON1});

    public float hui, saturation, brightness, alpha;

    Hitbox huiField, alphaField, SBField, colorDisplay;

    public Frame frame;
    Timer timer;

    public Palette (int x, int y, float hui, float saturation, float brightness, float alpha, int fps) {
        this.hui=hui;
        this.saturation=saturation;
        this.brightness=brightness;
        this.alpha=alpha;//370x220

        frame = new Frame(x,y,388,260,"Palette",false,false,false,false);

        frame.add(this);
        frame.addMouseListener(this);

        huiField = new Hitbox(10,10,40,200);
        alphaField = new Hitbox(60,10,40,200);
        SBField = new Hitbox(110,10,200,200);
        colorDisplay = new Hitbox(320,10,40,40);

        timer = new Timer(1000/fps,this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Point mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-this.frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-this.frame.getY()-31);
        if (frame.isVisible()) {
            if (huiAction) {
                float currentHui = (float) ((mousePosition.y - huiField.y) / huiField.height);
                if (currentHui > 1.0f) currentHui = 1.0f;
                else if (currentHui < 0.0f) currentHui = 0.0f;
                this.hui = currentHui;
            }
            if (alphaAction) {
                float currentAlpha = (float) ((mousePosition.y - alphaField.y) / alphaField.height);
                if (currentAlpha > 1.0f) currentAlpha = 1.0f;
                else if (currentAlpha < 0.0f) currentAlpha = 0.0f;
                this.alpha = currentAlpha;
            }
            if (SBAction) {
                float currentSaturation = (float) ((mousePosition.x - SBField.x) / SBField.width);
                float currentBrightness = (float) (1.0f - (mousePosition.y - SBField.y) / SBField.height);
                if (currentSaturation > 1.0f) currentSaturation = 1.0f;
                else if (currentSaturation < 0.0f) currentSaturation = 0.0f;
                if (currentBrightness > 1.0f) currentBrightness = 1.0f;
                else if (currentBrightness < 0.0f) currentBrightness = 0.0f;
                this.saturation = currentSaturation;
                this.brightness = currentBrightness;
            }
            repaint();
        }
    }

    public void paint (Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        g.setColor(new Color(100,100,100));
        g.fillRect(0,0,frame.getWidth(),frame.getHeight());

        g.setColor(Color.white);
        g.fillRect((int)huiField.x,(int)huiField.y,(int)huiField.width,(int)huiField.height);
        for (int i = 0; i < huiField.height; i++) {
            float currentHui = (float)((float)i/huiField.height);
            g.setColor(new Color(Color.HSBtoRGB(currentHui,1.0f,1.0f)));
            g.drawLine((int)huiField.x,(int)(i+huiField.y),(int)(huiField.x+huiField.width-1),(int)(huiField.y+i));
        }

        g.setColor(Color.white);
        g.fillRect((int)alphaField.x,(int)alphaField.y,(int)alphaField.width,(int)alphaField.height);

        int cells = 4;

        double alphaWidth = (int)(alphaField.width/cells);
        int countOfCellsInAlphaFieldInHeight = (int)(alphaField.height/alphaWidth);
        for (int i = 0; i < cells; i++) for (int j = 0; j < countOfCellsInAlphaFieldInHeight; j++) {
            g.setColor(new Color(191,191,191));
            if (i%2!=0&&j%2==0||i%2==0&&j%2!=0) g.fillRect((int)(alphaField.x+i*alphaWidth),(int)(alphaField.y+j*alphaWidth),(int)alphaWidth,(int)alphaWidth);
        }
        for (int i = 0; i < alphaField.height; i++) {
            float currentAlpha = (float)((float)i/alphaField.height);
            Color color = new Color(Color.HSBtoRGB(this.hui,this.saturation,this.brightness));
            g.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(currentAlpha*255)));
            g.drawLine((int)alphaField.x,(int)(i+alphaField.y),(int)(alphaField.x+alphaField.width-1),(int)(alphaField.y+i));
        }

        g.setColor(Color.white);
        g.fillRect((int)SBField.x,(int)SBField.y,(int)SBField.width,(int)SBField.height);
        for (int i = 0; i < SBField.width; i++) {
            float currentSaturation = (float)((float)i/SBField.width);
            g.setColor(new Color(Color.HSBtoRGB(this.hui,currentSaturation,1)));
            g.drawLine((int)(i+SBField.x),(int)(SBField.y),(int)(SBField.x+i),(int)(SBField.y+SBField.height-1));
        }

        for (int i = 0; i < SBField.height; i++) {
            float currentBrightness = (float)((float)i/SBField.height);
            g.setColor(new Color(0,0,0,currentBrightness));
            g.drawLine((int)SBField.x,(int)(i+SBField.y),(int)(SBField.x+SBField.width-1),(int)(SBField.y+i));
        }

        g.setColor(Color.white);
        g.fillRect((int)colorDisplay.x,(int)colorDisplay.y,(int)colorDisplay.width,(int)colorDisplay.height);

        double colorDisplayWidth = (int)(colorDisplay.width/cells), colorDisplayHeight = (int)(colorDisplay.height/cells);
        for (int i = 0; i < cells; i++) for (int j = 0; j < cells; j++) {
            g.setColor(new Color(191,191,191));
            if (i%2!=0&&j%2==0||i%2==0&&j%2!=0) g.fillRect((int)(colorDisplay.x+i*colorDisplayWidth),(int)(colorDisplay.y+j*colorDisplayHeight),(int)colorDisplayWidth,(int)colorDisplayHeight);
        }

        Color color = new Color(Color.HSBtoRGB(this.hui,this.saturation,this.brightness));

        g.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),(int)(this.alpha*255)));
        g.fillRect((int)colorDisplay.x,(int)colorDisplay.y,(int)colorDisplay.width,(int)colorDisplay.height);

        int huiY = (int)(huiField.y+(this.hui*huiField.height));
        int alphaY = (int)(alphaField.y+(this.alpha*alphaField.height));
        int saturationX = (int)(SBField.x+(this.saturation*SBField.width));
        int brightnessY = (int)(SBField.y+((1f-this.brightness)*SBField.height));

        g.setColor(Color.BLACK);
        g.fillPolygon(new int[]{saturationX,saturationX+7,saturationX+5},new int[]{brightnessY-1,brightnessY+7,brightnessY+15},3);
        g.fillPolygon(new int[]{(int)huiField.x,(int)(huiField.x+huiField.width/2),(int)(huiField.x+huiField.width/2)},new int[]{huiY,huiY,huiY+5},3);
        g.fillPolygon(new int[]{(int)alphaField.x,(int)(alphaField.x+alphaField.width/2),(int)(alphaField.x+alphaField.width/2)},new int[]{alphaY,alphaY,alphaY+5},3);

        g.setColor(Color.white);
        g.fillPolygon(new int[]{saturationX,saturationX+15,saturationX+7},new int[]{brightnessY-1,brightnessY+5,brightnessY+7},3);
        g.fillPolygon(new int[]{(int)(huiField.x+huiField.width/2),(int)(huiField.x+huiField.width),(int)(huiField.x+huiField.width/2)},new int[]{huiY,huiY,huiY+5},3);
        g.fillPolygon(new int[]{(int)(alphaField.x+alphaField.width/2),(int)(alphaField.x+alphaField.width),(int)(alphaField.x+alphaField.width/2)},new int[]{alphaY,alphaY,alphaY+5},3);

        //g.fillPolygon(new int[]{(int)huiField.x,(int)(huiField.x+huiField.width),(int)(huiField.x+huiField.width/2)},new int[]{huiY,huiY,huiY+5},3);
        //g.fillPolygon(new int[]{(int)alphaField.x,(int)(alphaField.x+alphaField.width),(int)(alphaField.x+alphaField.width/2)},new int[]{alphaY,alphaY,alphaY+5},3);
        //g.fillPolygon(new int[]{saturationX,saturationX+15,saturationX+7,saturationX+5},new int[]{brightnessY,brightnessY+5,brightnessY+7,brightnessY+15},4);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int key = e.getButton();
        if (selectColorMouseBind.check(key)) {
            mouseAction = true;
            if (collision(huiField)) huiAction = true;
            if (collision(alphaField)) alphaAction = true;
            if (collision(SBField)) SBAction = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int key = e.getButton();
        if (selectColorMouseBind.check(key)) {
            mouseAction = false;
            huiAction = false;
            alphaAction = false;
            SBAction = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public boolean collision (Hitbox hitbox) {
        Point mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-this.frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-this.frame.getY()-31);
        return mousePosition.x>hitbox.x&&mousePosition.x<hitbox.x+hitbox.width&&
                mousePosition.y>hitbox.y&&mousePosition.y<hitbox.y+hitbox.height;
    }

    public Color getColor () {
        Color currentColor = new Color(Color.HSBtoRGB(this.hui,this.saturation,this.brightness));

        return new Color(currentColor.getRed(),currentColor.getGreen(),currentColor.getBlue(),(int)(this.alpha*255));
    }

    public void setPaletteVisible (boolean visible) {
        frame.setVisible(visible);
    }

    public void setPaletteAlwaysOnTop (boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }
}
