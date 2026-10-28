import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import GUI.Frame;
import Paint.Boxes.ToolBox;
import Paint.Color.Palette;
import Paint.Layers.Layer;
import Paint.Layers.LayersPanel;
import World.Camera;
import Paint.Brush;
import Paint.Layers.Canvas;
import World.Values.Hitbox;

public class Main extends JPanel implements ActionListener, KeyListener, MouseListener, MouseWheelListener {

    boolean CTRLAction = false;
    int fps = 60;

    public static void main(String[] args) {
        new Main();
    }

    Frame frame = new Frame(0,0,1000,600,"Paint",false,false,true, true);
    Timer timer = new Timer(1000/fps,this);

    Hitbox panel = new Hitbox(0,0,frame.getWidth(),75);

    Camera camera = new Camera(0,0,1,0,5,20);
    Brush brush = new Brush(new Color(0,0,0,255),25,Brush.brush,Brush.mixing);

    Palette palette = new Palette(0,0,1.0f,1.0f,1.0f,1.0f,144);
    LayersPanel layersPanel = new LayersPanel(new Canvas(1000,1000,1),0,0,144);
    ToolBox toolBox = new ToolBox(0,144);

    public Main () {
        palette.setPaletteVisible(true);
        palette.setPaletteAlwaysOnTop(true);
        palette.setLocation(1000 + 25, 0);

        toolBox.setToolboxVisible(true);
        toolBox.setToolboxAlwaysOnTop(true);
        toolBox.setLocation(1000 + 25, 260 + 25);

        layersPanel.getCurrentCanvas().centralizeTheCoordinateOfCamera(camera,frame);
        layersPanel.getCurrentCanvas().centralizeTheZoomOfCamera(camera,frame,false,100);
        layersPanel.setLayersAlwaysOnTop(true);
        layersPanel.setLocation(1000 + 25, 260 + 25 + 102 + 25);

        frame.add(this);
        frame.addKeyListener(this);
        frame.addMouseListener(this);
        frame.addMouseWheelListener(this);
        frame.setVisible(true);

        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        panel.width = frame.getWidth();

        if (frame.isUndecorated()) brush.mousePosition = MouseInfo.getPointerInfo().getLocation();
        else brush.mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-frame.getY()-31);

        camera.x-=(camera.x-camera.actionX)/camera.smoothEffect;
        camera.y-=(camera.y-camera.actionY)/camera.smoothEffect;
        camera.zoom-=(camera.zoom-camera.actionZoom)/camera.smoothEffect;
        camera.angle-=(camera.angle-camera.actionAngle)/camera.smoothEffect;

        if (!brush.paintAction) {
            if (camera.moveUpAction) camera.actionY -= camera.moveSpeed;
            if (camera.moveRightAction) camera.actionX += camera.moveSpeed;
            if (camera.moveDownAction) camera.actionY += camera.moveSpeed;
            if (camera.moveLeftAction) camera.actionX -= camera.moveSpeed;
        }

        brush.color = palette.getColor();
        brush.paintingType = toolBox.getPaintingType();

        if (brush.paintAction) layersPanel.getCurrentCanvas().paintPress(brush,camera,frame);

        repaint();

        brush.mouseLastPosition = MouseInfo.getPointerInfo().getLocation();

        if (frame.isActive()) {
            Cursor customCursor = null;
            //if (brush.paintingType==Brush.brush) customCursor = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon("Images/Cursors/brush.png").getImage(), new Point(25, 25), "CustomCursor");
            if (customCursor!=null) {
                frame.setCursor(customCursor);

            }
        } else {
            frame.setCursor(Cursor.getDefaultCursor());
        }
    }

    public void paint (Graphics g) {
        super.paint(g);
        Graphics2D g2D = (Graphics2D) g;

        g.drawImage(new ImageIcon("background.jfif").getImage(),0,0,frame.getWidth(),frame.getHeight(),null);

        layersPanel.getCurrentCanvas().draw(g,camera,frame);

        if (brush.paintAction) layersPanel.getCurrentCanvas().drawTimeline(g,camera,frame,brush);

        //action brush
        Point mousePosition = getMousePosition();
        int width = (int)(brush.width * camera.actionZoom);
        if (brush.paintingType == 1) {
            g.setColor(Color.WHITE);
        }
        else {
            g.setColor(brush.color);
        }
        g.fillOval(mousePosition.x - width / 2, mousePosition.y - width / 2, width, width);
        layersPanel.getCurrentCanvas().drawSelectorLine(g,camera,frame);

        g.setColor(new Color(120,120,120));
        g.fillRect((int)(panel.x),(int)(panel.y),(int)(panel.width),(int)(panel.height));
        g.drawImage(new ImageIcon("Images/LayersPanel/Box/background.png").getImage(),(int)(panel.x),(int)(panel.y),(int)(panel.width),(int)(panel.height),null);
    }

    public Point getMousePosition() {
        return new Point(MouseInfo.getPointerInfo().getLocation().x-this.frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-this.frame.getY()-31);
    }

    public boolean collision (Hitbox hitbox) {
        Point mousePosition = getMousePosition();
        return mousePosition.x>hitbox.x&&mousePosition.x<hitbox.x+hitbox.width&&
                mousePosition.y>hitbox.y&&mousePosition.y<hitbox.y+hitbox.height;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (camera.moveUpKeyBing.check(key)) camera.moveUpAction = true;
        else if (camera.moveRightKeyBing.check(key)) camera.moveRightAction = true;
        else if (camera.moveDownKeyBing.check(key)) camera.moveDownAction = true;
        else if (camera.moveLeftKeyBing.check(key)) camera.moveLeftAction = true;
        else if (key==KeyEvent.VK_CONTROL) CTRLAction = true;
        if (key==KeyEvent.VK_Z&&CTRLAction&&layersPanel.canvas.size()>1) {
            //layersPanel.canvas.removeFirst();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key==KeyEvent.VK_CONTROL) CTRLAction = false;
        if (camera.moveUpKeyBing.check(key)) camera.moveUpAction = false;
        else if (camera.moveRightKeyBing.check(key)) camera.moveRightAction = false;
        else if (camera.moveDownKeyBing.check(key)) camera.moveDownAction = false;
        else if (camera.moveLeftKeyBing.check(key)) camera.moveLeftAction = false;
        else if (key==KeyEvent.VK_ENTER) {
            layersPanel.getCurrentCanvas().selected = false;
            layersPanel.getCurrentCanvas().selectLine = new ArrayList<>();
            layersPanel.getCurrentCanvas().startPoint = new ArrayList<>();
            layersPanel.getCurrentCanvas().endPoint = new ArrayList<>();
            layersPanel.getCurrentCanvas().select = new Layer(layersPanel.getCurrentCanvas().width,layersPanel.getCurrentCanvas().height,1.0f,true,true);
        } else if (key==KeyEvent.VK_DELETE&&layersPanel.getCurrentCanvas().selected) {
            for (int i = 0; i < Math.min(layersPanel.getCurrentCanvas().startPoint.size(),layersPanel.getCurrentCanvas().endPoint.size()); i++) {
                for (int j = layersPanel.getCurrentCanvas().startPoint.get(i).x; j <= layersPanel.getCurrentCanvas().endPoint.get(i).x; j++) {
                    for (int k = layersPanel.getCurrentCanvas().startPoint.get(i).y; k <= layersPanel.getCurrentCanvas().endPoint.get(i).y; k++) {
                        if (j>=0&&k>=0&&j<layersPanel.getCurrentCanvas().layers.get(layersPanel.getCurrentCanvas().selectedLayer).image.getWidth()&&k<layersPanel.getCurrentCanvas().layers.get(layersPanel.getCurrentCanvas().selectedLayer).image.getHeight()&&!new Color(layersPanel.getCurrentCanvas().select.image.getRGB(j, k)).equals(Color.WHITE)) {
                            layersPanel.getCurrentCanvas().layers.get(layersPanel.getCurrentCanvas().selectedLayer).image.setRGB(j,k,new Color(0,0,0,0).getRGB());
                        }
                    }
                }
            }
        } else if (key==KeyEvent.VK_NUMPAD1) brush.mixType = Brush.mixing;
        else if (key==KeyEvent.VK_NUMPAD2) brush.mixType = Brush.blending;
        else if (key==KeyEvent.VK_NUMPAD3) brush.mixType = Brush.replace;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int key = e.getButton();
        if (brush.paintMouseBind.check(key)&&!collision(panel)) {
            if (!brush.paintAction) {
                layersPanel.getCurrentCanvas().startMousePosition = MouseInfo.getPointerInfo().getLocation();
                layersPanel.getCurrentCanvas().startMousePosition.x-=frame.getX();
                layersPanel.getCurrentCanvas().startMousePosition.y-=frame.getY();
                if (!frame.isUndecorated()) {
                    layersPanel.getCurrentCanvas().startMousePosition.x-=8;
                    layersPanel.getCurrentCanvas().startMousePosition.y-=31;
                }
            }
            brush.paintAction = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int key = e.getButton();
        if (brush.paintMouseBind.check(key)) {
            //Canvas canvas = new Canvas(layersPanel.getCurrentCanvas());
            brush.paintAction = false;
            layersPanel.getCurrentCanvas().paintRelease(camera,frame,brush);
            //layersPanel.canvas.addFirst(canvas);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!brush.paintAction) {
            if (!CTRLAction) {
                camera.actionZoom -= (double) e.getWheelRotation() * camera.actionZoom / 10;
            }
            else {
                brush.width -= e.getWheelRotation();

                if (brush.width < 1) {
                    brush.width = 1;
                }
            }
        }
    }
}