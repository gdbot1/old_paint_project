package Paint.Boxes;

import Paint.Brush;
import World.Values.Bind;
import World.Values.Hitbox;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import GUI.Frame;

public class ToolBox extends JPanel implements ActionListener, MouseListener {
    Bind selectToolMouseBind = new Bind(new int[]{MouseEvent.BUTTON1});
    boolean mouseAction = false;

    Frame frame = new Frame(0,0,293,102,"",false,false,false,false);
    Timer timer;

    int paintingType;
    List<Button> buttons = new ArrayList<>();

    public ToolBox (int paintingType, int fps) {
        this.paintingType=paintingType;
        frame.add(this);
        frame.addMouseListener(this);

        Color selectedColor = Color.white, defaultColor = Color.gray, collisionColor = Color.lightGray;
        int width = 43, height = 43;

        buttons.add(new Button(new Hitbox(10,10,width,height),Brush.brush,selectedColor,defaultColor,collisionColor,"Images/Toolbox/Tools/brush.png"));
        buttons.add(new Button(new Hitbox(10+width+10,10,width,height),Brush.eraser,selectedColor,defaultColor,collisionColor,"Images/Toolbox/Tools/eraser.png"));
        buttons.add(new Button(new Hitbox(10+width*2+10*2,10,width,height),Brush.filling,selectedColor,defaultColor,collisionColor,"Images/Toolbox/Tools/filling.png"));
        buttons.add(new Button(new Hitbox(10+width*3+10*3,10,width,height),Brush.selectorDefault,selectedColor,defaultColor,collisionColor,"Images/Toolbox/Tools/selectorDefault.png"));
        buttons.add(new Button(new Hitbox(10+width*4+10*4,10,width,height),Brush.selectorLasso,selectedColor,defaultColor,collisionColor,"Images/Toolbox/Tools/selectorLasso.png"));

        timer = new Timer(1000/fps,this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < buttons.size(); i++) buttons.get(i).collisionAction = collision(buttons.get(i).hitbox);

        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        g.setColor(new Color(100,100,100));
        g.fillRect(0,0,frame.getWidth(),frame.getHeight());

        for (int i = 0; i < buttons.size(); i++) {
            int x = (int) buttons.get(i).hitbox.x, y = (int) buttons.get(i).hitbox.y, width = (int) buttons.get(i).hitbox.width, height = (int) buttons.get(i).hitbox.height;

            g.setColor(new Color(80,80,80));
            g.fillRect(x+3,y+3,width,height);

            if (buttons.get(i).paintingType==paintingType) g.setColor(buttons.get(i).selectedColor);
            else if (buttons.get(i).collisionAction) g.setColor(buttons.get(i).collisionColor);
            else g.setColor(buttons.get(i).defaultColor);
            g.fillRect(x,y,width,height);
            if (buttons.get(i).image!=null) g.drawImage(buttons.get(i).image,x,y,width,height,null);
            //buttons.get(i).hitbox.draw(g,null,null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int key = e.getButton();
        if (selectToolMouseBind.check(key)) {
            mouseAction = true;
            for (Button button : buttons) if (button.collisionAction) paintingType = button.paintingType;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int key = e.getButton();
        if (selectToolMouseBind.check(key)) mouseAction = false;
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

    public int getPaintingType () {
        return this.paintingType;
    }

    public void setToolboxVisible (boolean visible) {
        frame.setVisible(visible);
    }

    public void setToolboxAlwaysOnTop (boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }

    public void setLocation (int x, int y) {
        frame.setLocation(x, y);
    }
}

class Button {

    public Hitbox hitbox;
    public int paintingType; //type of tool in button
    public Color selectedColor, defaultColor, collisionColor;
    public BufferedImage image;
    public boolean collisionAction = false;

    public Button (Hitbox hitbox, int paintingType, Color selectedColor, Color defaultColor, Color collisionColor, BufferedImage image) {
        this.hitbox=new Hitbox(hitbox);
        this.paintingType=paintingType;
        this.selectedColor=selectedColor;
        this.defaultColor=defaultColor;
        this.collisionColor=collisionColor;
        this.image=image;
    }

    public Button (Hitbox hitbox, int paintingType, Color selectedColor, Color defaultColor, Color collisionColor, String path) {
        this.hitbox=new Hitbox(hitbox);
        this.paintingType=paintingType;
        this.selectedColor=selectedColor;
        this.defaultColor=defaultColor;
        this.collisionColor=collisionColor;
        File imageFile = new File(path);
        if (imageFile.exists()) {
            try {
                this.image=ImageIO.read(imageFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else this.image=null;
    }
}
