package Paint.Layers;

import World.Values.Bind;
import World.Values.Hitbox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import GUI.Frame;

public class LayersPanel extends JPanel implements ActionListener, KeyListener, MouseListener, MouseWheelListener {

    Hitbox addLayer = new Hitbox(10,10,20,20), removeLayer = new Hitbox(40,10,20,20),
            syncLayers = new Hitbox(70,10,20,20), moveLayer = new Hitbox(100,10,20,20), rollButton = new Hitbox(130,0,15,15);

    BufferedImage bufferedImage;
    public BufferedImage background;
    int xImage = 10, yImage = 40, layerHeight = 40;

    double startY;

    java.util.List<Hitbox> visibleButtons = new ArrayList<>();

    public Bind selectColorMouseBind = new Bind(new int[]{MouseEvent.BUTTON1});
    boolean mouseAction = false, collied = false, addButtonAction = false, removeButtonAction = false, syncButtonAction = false, moveButtonAction = false;

    double scale = 0;

    String buttonBackgroundPath = "Images/LayersPanel/Box/background.png";

    public Frame frame;
    Timer timer;

    public java.util.List<Canvas> canvas = new ArrayList<>();

    public LayersPanel (Canvas canvas, int x, int y, int fps) {
        frame = new Frame(x,y,162,260,"Panel",false,false,false,false);

        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseWheelListener(this);

        frame.setVisible(true);

        loadBackground();

        bufferedImage = new BufferedImage(110,170,BufferedImage.TYPE_INT_ARGB);

        this.canvas.add(canvas);

        timer = new Timer(1000/fps,this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Point mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-this.frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-this.frame.getY()-31);

        addButtonAction = collision(addLayer);
        removeButtonAction = collision(removeLayer);
        syncButtonAction = collision(syncLayers);
        moveButtonAction = collision(moveLayer);

        rollButton.height = bufferedImage.getHeight()/((double) getCurrentCanvas().layers.size()*layerHeight)*(220);

        if (frame.isVisible()) {
            repaint();

            if (this.scale>layerHeight* getCurrentCanvas().layers.size()-bufferedImage.getHeight()) this.scale = Math.max(layerHeight * getCurrentCanvas().layers.size() - bufferedImage.getHeight(), 0);
            else if (this.scale<0) this.scale=0;
        }
        scale=rollButton.y*(getCurrentCanvas().layers.size()*layerHeight-(double)bufferedImage.getHeight())/(220-rollButton.height);
        if (mouseAction) if (collied) rollButton.y=mousePosition.y-startY+31;

        if (getCurrentCanvas().layers.size()*layerHeight>bufferedImage.getHeight()) {
            if (rollButton.y < 0) rollButton.y = 0;
            else if (rollButton.y > 220 - rollButton.height) rollButton.y = 220 - rollButton.height;
        } else rollButton.y = 0;
    }

    public Canvas getCurrentCanvas() {
        return canvas.getFirst();
    }

    public void paint (Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        /*
        addLayer.draw(g,null,null);
        removeLayer.draw(g,null,null);
        syncLayers.draw(g,null,null);
        moveLayer.draw(g,null,null);

         */
        g.setColor(new Color(100,100,100));
        g.fillRect(0,0,frame.getWidth(),frame.getHeight());

        Image buttonBackground = new ImageIcon(buttonBackgroundPath).getImage();

        if (addButtonAction) g.setColor(new Color(200,200,200));
        else g.setColor(new Color(180,180,180));
        g.fillRect((int)(addLayer.x),(int)(addLayer.y),(int)(addLayer.width),(int)(addLayer.height));
        g.drawImage(buttonBackground,(int)(addLayer.x),(int)(addLayer.y),(int)(addLayer.width),(int)(addLayer.height),null);
        g.drawImage(new ImageIcon("Images/LayersPanel/Buttons/add.png").getImage(),(int)(addLayer.x),(int)(addLayer.y),(int)(addLayer.width),(int)(addLayer.height),null);

        if (removeButtonAction) g.setColor(new Color(200,200,200));
        else g.setColor(new Color(180,180,180));
        g.fillRect((int)(removeLayer.x),(int)(removeLayer.y),(int)(removeLayer.width),(int)(removeLayer.height));
        g.drawImage(buttonBackground,(int)(removeLayer.x),(int)(removeLayer.y),(int)(removeLayer.width),(int)(removeLayer.height),null);
        g.drawImage(new ImageIcon("Images/LayersPanel/Buttons/remove.png").getImage(),(int)(removeLayer.x),(int)(removeLayer.y),(int)(removeLayer.width),(int)(removeLayer.height),null);

        if (syncButtonAction) g.setColor(new Color(200,200,200));
        else g.setColor(new Color(180,180,180));
        g.fillRect((int)(syncLayers.x),(int)(syncLayers.y),(int)(syncLayers.width),(int)(syncLayers.height));
        g.drawImage(buttonBackground,(int)(syncLayers.x),(int)(syncLayers.y),(int)(syncLayers.width),(int)(syncLayers.height),null);
        g.drawImage(new ImageIcon("Images/LayersPanel/Buttons/sync.png").getImage(),(int)(syncLayers.x),(int)(syncLayers.y),(int)(syncLayers.width),(int)(syncLayers.height),null);

        if (moveButtonAction) g.setColor(new Color(200,200,200));
        else g.setColor(new Color(180,180,180));
        g.fillRect((int)(moveLayer.x),(int)(moveLayer.y),(int)(moveLayer.width),(int)(moveLayer.height));
        g.drawImage(buttonBackground,(int)(moveLayer.x),(int)(moveLayer.y),(int)(moveLayer.width),(int)(moveLayer.height),null);
        g.drawImage(new ImageIcon("Images/LayersPanel/Buttons/move.png").getImage(),(int)(moveLayer.x),(int)(moveLayer.y),(int)(moveLayer.width),(int)(moveLayer.height),null);

        Graphics2D graphics2D = bufferedImage.createGraphics();
        graphics2D.setColor(Color.lightGray);
        graphics2D.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());


        graphics2D.drawImage(buttonBackground,0,0,(bufferedImage.getWidth()),(bufferedImage.getHeight()),null);

        //
        int width = bufferedImage.getWidth();

        visibleButtons = new ArrayList<>();

        for (int i = 0; i < getCurrentCanvas().layers.size(); i++) if (getCurrentCanvas().layers.get(i)!=null) {
            if (i== getCurrentCanvas().selectedLayer) graphics2D.setColor(new Color(150,150,150));
            else graphics2D.setColor(new Color(120,120,120));
            graphics2D.fillRect(0,(int)(layerHeight*i-scale),width,layerHeight);
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.5f));
            graphics2D.drawImage(buttonBackground,0,(int)(layerHeight*i-scale),width,layerHeight,null);
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));

            if (!getCurrentCanvas().layers.get(i).visible) graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
            graphics2D.drawImage(background,5,(int)(layerHeight*i-scale)+5,layerHeight-10,layerHeight-10,null);
            graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,1.0f));

            visibleButtons.add(new Hitbox(xImage+(bufferedImage.getWidth()-10-(layerHeight-20)),yImage+(layerHeight*i-scale)+10,layerHeight-20,layerHeight-20));
            if (getCurrentCanvas().layers.get(i).visible) graphics2D.setColor(new Color(180,180,180));
            else graphics2D.setColor(new Color(160,160,160));
            graphics2D.fillRect((bufferedImage.getWidth()-10-(layerHeight-20)),(int)(layerHeight*i-scale)+10,layerHeight-20,layerHeight-20);
            graphics2D.drawImage(buttonBackground,(bufferedImage.getWidth()-10-(layerHeight-20)),(int)(layerHeight*i-scale)+10,layerHeight-20,layerHeight-20,null);

            if (getCurrentCanvas().layers.get(i).visible) graphics2D.drawImage(new ImageIcon("Images/LayersPanel/Buttons/visible.png").getImage(),(bufferedImage.getWidth()-10-(layerHeight-20)),(int)(layerHeight*i-scale)+10,layerHeight-20,layerHeight-20,null);
            else graphics2D.drawImage(new ImageIcon("Images/LayersPanel/Buttons/invisible.png").getImage(),(bufferedImage.getWidth()-10-(layerHeight-20)),(int)(layerHeight*i-scale)+10,layerHeight-20,layerHeight-20,null);

            if (getCurrentCanvas().layers.get(i).image!=null) {
                BufferedImage layerImage = getCurrentCanvas().layers.get(i).image;
                graphics2D.drawImage(layerImage,5,(int)(layerHeight*i-scale)+5,layerHeight-10,layerHeight-10,null);
            }
        }

        g.setColor(new Color(90,90,90));
        g.fillRect(130,0,15,220);
        g.drawImage(buttonBackground,130,0,15,220,null);

        //
        if (rollButton.height<220) {
            g.setColor(new Color(150, 150, 150));
            g.fillRect((int) (rollButton.x), (int) (rollButton.y), (int) (rollButton.width), (int) (rollButton.height+1));
            g.setColor(new Color(120, 120, 120));
            if (rollButton.height > 6) g.fillRect((int) (rollButton.x + 3), (int) (rollButton.y + 3), (int) (rollButton.width - 6), (int) (rollButton.height - 6 + 1));
            g.drawImage(buttonBackground, (int) (rollButton.x), (int) (rollButton.y), (int) (rollButton.width), (int) (rollButton.height+1), null);
        }

        g.drawImage(bufferedImage,xImage,yImage,null);
    }

    public void loadBackground () {
        background = new BufferedImage(layerHeight-10,layerHeight-10,BufferedImage.TYPE_INT_ARGB);
        Graphics2D backgroundG2D = background.createGraphics();
        backgroundG2D.setColor(Color.white);
        backgroundG2D.fillRect(0,0,layerHeight-10,layerHeight-10);
        double currentCellSizeWithoutBackground = 3;
        for (int i = 0; i < layerHeight-10/currentCellSizeWithoutBackground; i++) for (int j = 0; j < layerHeight-10/currentCellSizeWithoutBackground; j++) {
            if (i%2!=0&&j%2==0||i%2==0&&j%2!=0) {
                backgroundG2D.setColor(new Color(191, 191, 191));
                backgroundG2D.fillRect((int) (i * currentCellSizeWithoutBackground), (int) (j * currentCellSizeWithoutBackground), (int) currentCellSizeWithoutBackground, (int) currentCellSizeWithoutBackground);
            }
        }
        backgroundG2D.dispose();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int key = e.getButton();
        if (selectColorMouseBind.check(key)) {
            mouseAction = true;
            startY = MouseInfo.getPointerInfo().getLocation().y-rollButton.y;
            if (collision(rollButton)) collied = true;
            if (collision(addLayer)) getCurrentCanvas().addLayer(getCurrentCanvas().selectedLayer,1.0f,true);
            if (collision(removeLayer)) if (getCurrentCanvas().layers.size()>1&& getCurrentCanvas().selectedLayer< getCurrentCanvas().layers.size()) {
                getCurrentCanvas().removeLayer(getCurrentCanvas().selectedLayer);
                if (getCurrentCanvas().selectedLayer>= getCurrentCanvas().layers.size()) getCurrentCanvas().selectedLayer = getCurrentCanvas().layers.size()-1;
            }
            if (collision(syncLayers)) if (getCurrentCanvas().layers.size()>1&& getCurrentCanvas().selectedLayer<getCurrentCanvas().layers.size()-1) {
                Graphics2D graphics2D = getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer+1).image.createGraphics();
                graphics2D.drawImage(getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).image,0,0,null);
                graphics2D.dispose();
                getCurrentCanvas().removeLayer(getCurrentCanvas().selectedLayer);
            }
            if (collision(moveLayer)&&!getCurrentCanvas().layers.isEmpty()&& getCurrentCanvas().selectedLayer< getCurrentCanvas().layers.size()-1) {
                Layer layerToMove = getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer);
                getCurrentCanvas().layers.set(getCurrentCanvas().selectedLayer, getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer+1));
                getCurrentCanvas().layers.set(getCurrentCanvas().selectedLayer+1,layerToMove);
                getCurrentCanvas().selectedLayer = getCurrentCanvas().selectedLayer+1;
            }
            for (int i = 0; i < getCurrentCanvas().layers.size(); i++) {
                int width = bufferedImage.getWidth();
                Hitbox hitbox = new Hitbox(xImage,yImage+(int)(layerHeight*i-scale),width,layerHeight), hitbox2 = new Hitbox(xImage,yImage,bufferedImage.getWidth(),bufferedImage.getHeight());
                if (i>=visibleButtons.size()||!collision(visibleButtons.get(i))) {
                    if (collision(hitbox) && collision(hitbox2)) getCurrentCanvas().selectedLayer = i;
                } else if (i<visibleButtons.size()) getCurrentCanvas().layers.get(i).visible = !getCurrentCanvas().layers.get(i).visible;
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int key = e.getButton();
        if (selectColorMouseBind.check(key)) {
            mouseAction = false;
            collied = false;
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

    public void setPanelVisible (boolean visible) {
        frame.setVisible(visible);
    }

    public void setPanelAlwaysOnTop (boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        this.rollButton.y+=e.getWheelRotation()*10*(rollButton.height/220);

        if (getCurrentCanvas().layers.size()*layerHeight>bufferedImage.getHeight()) {
            if (rollButton.y < 0) rollButton.y = 0;
            else if (rollButton.y > 220 - rollButton.height) rollButton.y = 220 - rollButton.height;
        } else rollButton.y = 0;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_UP -> {
                if (getCurrentCanvas().layers.size()>1) getCurrentCanvas().selectedLayer--;
                if (getCurrentCanvas().selectedLayer<0) getCurrentCanvas().selectedLayer = 0;
            }
            case KeyEvent.VK_DOWN -> {
                if (getCurrentCanvas().layers.size()>1) getCurrentCanvas().selectedLayer++;
                if (getCurrentCanvas().selectedLayer>= getCurrentCanvas().layers.size()) getCurrentCanvas().selectedLayer = getCurrentCanvas().layers.size()-1;
            }
            case KeyEvent.VK_RIGHT -> {
                if (getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer)!=null) getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency+=0.1f;
                if (getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency>1.0f) getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency = 1.0f;
            }
            case KeyEvent.VK_LEFT -> {
                if (getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer)!=null) getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency-=0.1f;
                if (getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency<0.0f) getCurrentCanvas().layers.get(getCurrentCanvas().selectedLayer).transparency = 0.0f;
            }
        }
    }

    public void setLayersAlwaysOnTop (boolean alwaysOnTop) {
        frame.setAlwaysOnTop(alwaysOnTop);
    }
}