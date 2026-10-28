package Paint.Layers;


import World.Camera;
import Paint.Brush;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class Canvas {
    public List<Layer> layers;
    public Layer display, select;
    public boolean selected = false, switcher = false;
    public int width, height, selectedLayer, cellSizeWithoutBackground = 25;
    public List<Point> line = new ArrayList<>(), timeLine = new ArrayList<>();
    public List<List<Point>> selectLine = new ArrayList<>();
    public List<Point> startPoint = new ArrayList<>(), endPoint = new ArrayList<>();
    public Point startMousePosition = null;
    public BufferedImage background;
    public int currentCountOfLoadingPixels = 0, countOfLoadingPixels = 0;

    public Canvas (List<Layer> layers) {
        this.layers=layers;
        display=new Layer(width,height,1.0f,true,false);
        select=new Layer(width,height,1.0f,true,false);
        if (layers!=null&&!layers.isEmpty()) {
            this.width = layers.getFirst().image.getWidth();
            this.height = layers.getFirst().image.getHeight();
            selectedLayer = 0;

            loadBackground();
        }
    }

    public Canvas (int width, int height, int count) {
        List<Layer> layers = new ArrayList<>();
        for (int i = 0; i < count; i++) layers.add(new Layer(width,height,1.0f,true,false));
        display=new Layer(width,height,1.0f,true,true);
        select=new Layer(width,height,1.0f,true,true);
        this.layers=layers;
        this.width=width;
        this.height=height;
        selectedLayer = 0;

        loadBackground();
    }

    public Canvas (Canvas canvas) {
        this.layers = new ArrayList<>();
        for (int i = 0; i < canvas.layers.size(); i++) layers.add(new Layer(canvas.layers.get(i)));
        this.display=new Layer(canvas.display);
        this.select=new Layer(canvas.select);
        this.height=canvas.height;
        this.width=canvas.width;
        this.selectedLayer=canvas.selectedLayer;
        this.cellSizeWithoutBackground=canvas.cellSizeWithoutBackground;
        this.line=new ArrayList<>();
        this.timeLine=new ArrayList<>();
        this.currentCountOfLoadingPixels=canvas.currentCountOfLoadingPixels;
        this.countOfLoadingPixels=canvas.countOfLoadingPixels;

        loadBackground();
    }

    public void paintPress (Brush brush, Camera camera, Frame frame) {
        Point mousePosition;
        if (frame.isUndecorated()) mousePosition = MouseInfo.getPointerInfo().getLocation();
        else mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-frame.getY()-31);
        double x = camera.getReverseZoom(mousePosition.x+camera.x,(double)frame.getWidth()/2+camera.x), y = camera.getReverseZoom(mousePosition.y+camera.y,(double)frame.getHeight()/2+camera.y);
        if (line.isEmpty() || mousePosition.x != line.getLast().x || mousePosition.y != line.getLast().y) line.add(new Point((int) x, (int) y));
        if (timeLine.isEmpty() || mousePosition.x != timeLine.getLast().x || mousePosition.y != timeLine.getLast().y) timeLine.add(mousePosition);
    }

    public void paintRelease (Camera camera,Frame frame,Brush brush) {
        //int[] px = new int[line.size()], py = new int[line.size()];
        //for (int i = 0; i < line.size(); i++) {
        //    px[i] = line.get(i).x;
        //    py[i] = line.get(i).y;
        //}
        //display.drawPolygon(Color.BLACK,px,py,px.length);

        if (brush.paintingType!=Brush.selectorLasso&&brush.paintingType!=Brush.selectorDefault) for (int i = 0; i < line.size()-1; i++) display.drawLine(Color.BLACK,brush.width,line.get(i),line.get(i+1));
        else if (brush.paintingType==Brush.selectorLasso) {
            int[] px = new int[line.size()], py = new int[line.size()];
            for (int i = 0; i < line.size(); i++) {
                px[i] = line.get(i).x;
                py[i] = line.get(i).y;
            }
            selectLine.add(new ArrayList<>());
            selectLine.getLast().addAll(line);
            select.drawPolygon(Color.BLACK,px,py,px.length);

            selected = true;
        } else {
            Point mousePosition;
            if (frame.isUndecorated()) mousePosition = MouseInfo.getPointerInfo().getLocation();
            else mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-frame.getY()-31);

            double x1 = camera.getReverseZoom(mousePosition.x+camera.x,(double)frame.getWidth()/2+camera.x), y1 = camera.getReverseZoom(mousePosition.y+camera.y,(double)frame.getHeight()/2+camera.y);
            double x2 = camera.getReverseZoom(startMousePosition.x+camera.x,(double)frame.getWidth()/2+camera.x), y2 = camera.getReverseZoom(startMousePosition.y+camera.y,(double)frame.getHeight()/2+camera.y);

            int x = (int)x2, y = (int)y2;
            int width = (int)(x1-x2), height = (int)(y1-y2);

            selectLine.add(new ArrayList<>());
            selectLine.getLast().add(new Point(x,y));
            selectLine.getLast().add(new Point(x+width,y));
            selectLine.getLast().add(new Point(x+width,y+height));
            selectLine.getLast().add(new Point(x,y+height));

            //System.out.println(x+","+y+";"+(x+width)+","+y+";"+(x+width)+","+(y+height)+";"+x+","+(y+height));
            System.out.println(selectLine.getLast().get(0).x+","+selectLine.getLast().get(0).y+";"+selectLine.getLast().get(1).x+","+selectLine.getLast().get(1).y+";"+selectLine.getLast().get(2).x+","+selectLine.getLast().get(2).y+";"+selectLine.getLast().get(3).x+","+selectLine.getLast().get(3).y);

            select.drawRectangle(Color.BLACK,x,y,width,height);

            selected = true;
        }

        int minX = width+1, minY = height+1, maxX = -1, maxY = -1;
        for (int i = 0; i < line.size(); i++) {
            if (line.get(i).x>maxX) maxX = line.get(i).x;
            if (line.get(i).x<minX) minX = line.get(i).x;
            if (line.get(i).y>maxY) maxY = line.get(i).y;
            if (line.get(i).y<minY) minY = line.get(i).y;
        }
        if (brush.paintingType==Brush.selectorLasso||brush.paintingType==Brush.selectorDefault) {
            startPoint.add(new Point(minX,minY));
            endPoint.add(new Point(maxX,maxY));
        }

        countOfLoadingPixels = ((maxX+brush.width)-(minX-brush.width))*((maxY+brush.width)-(minY-brush.width));

        if (brush.paintingType== Brush.eraser||brush.paintingType== Brush.brush) for (int i = minX-brush.width; i < maxX+brush.width; i++) for (int j = minY-brush.width; j < maxY+brush.width; j++) {
            currentCountOfLoadingPixels = i+j*(maxX+brush.width)-(minX-brush.width);
        //for (int i = 0; i < width; i++) for (int j = 0; j < height; j++) {
            if (i>=0&&i<width&&j>=0&&j<height) {
                if (!new Color(display.image.getRGB(i, j)).equals(Color.WHITE)) {
                    if (!new Color(select.image.getRGB(i, j)).equals(Color.WHITE)||!selected) {
                        if (brush.paintingType == Brush.brush) {
                            if (brush.mixType == Brush.mixing) {
                                Graphics2D g2D = layers.get(selectedLayer).image.createGraphics();
                                g2D.setColor(brush.color);
                                g2D.drawLine(i, j, i, j);
                                g2D.dispose();
                            } else if (brush.mixType == Brush.replace)
                                layers.get(selectedLayer).image.setRGB(i, j, brush.color.getRGB());
                            else if (brush.mixType == Brush.blending) {
                                int layerColorValue = layers.get(selectedLayer).image.getRGB(i, j);

                                int layerColorAlpha = (layerColorValue >> 24) & 0xFF;
                                int layerColorRed = (layerColorValue >> 16) & 0xFF;
                                int layerColorGreen = (layerColorValue >> 8) & 0xFF;
                                int layerColorBlue = layerColorValue & 0xFF;

                                int red = layerColorRed + brush.color.getRed(), green = layerColorGreen + brush.color.getGreen(), blue = layerColorBlue + brush.color.getBlue(), alpha = layerColorAlpha + brush.color.getAlpha();

                                if (red > 255) red = 255;
                                else if (red < 0) red = 0;
                                if (green > 255) green = 255;
                                else if (green < 0) green = 0;
                                if (blue > 255) blue = 255;
                                else if (blue < 0) blue = 0;
                                if (alpha > 255) alpha = 255;
                                else if (alpha < 0) alpha = 0;

                                layers.get(selectedLayer).image.setRGB(i, j, new Color(red, green, blue, alpha).getRGB());
                            }
                        } else if (brush.paintingType == Brush.eraser) layers.get(selectedLayer).image.setRGB(i, j, 0);
                    }
                }
            }
        }
        else if (brush.paintingType== Brush.filling) {
            if (line.getLast().x>=0&&line.getLast().y>=0&&line.getLast().x<width&&line.getLast().y<height) floodFill(line.getLast().x,line.getLast().y,layers.get(selectedLayer).image.getRGB(line.getLast().x,line.getLast().y),brush.color.getRGB(),layers.get(selectedLayer).image,brush);
        }

        currentCountOfLoadingPixels = 0;
        countOfLoadingPixels = 0;

        line = new ArrayList<>();
        timeLine = new ArrayList<>();
        display=new Layer(width,height,1.0f,true,true);
    }

    public void addLayer (float transparency, boolean visible) {
        layers.add(new Layer(this.width,this.height,transparency,visible,false));
    }

    public void addLayer (int id,float transparency, boolean visible) {
        layers.add(id,new Layer(this.width,this.height,transparency,visible,false));
    }

    public void replaceLayer (int fromIndex, int toIndex) {
        layers.add(toIndex,layers.remove(fromIndex));
    }

    public void removeLayer (int index) {
        layers.remove(index);
    }

    public void draw (Graphics g, Camera camera, Frame frame) {
        Graphics2D g2D = (Graphics2D) g;

        double x = camera.getZoom(0,(double)frame.getWidth()/2+camera.x)-camera.x, y = camera.getZoom(0,(double)frame.getHeight()/2+camera.y)-camera.y;
        double width = this.width*camera.zoom, height = this.height*camera.zoom;

        g.drawImage(background,(int)x,(int)y,(int)width,(int)height,null);

        for (int i = layers.size()-1; i >= 0; i--) {
            if (layers.get(i).visible) {
                float transparency = layers.get(i).transparency;

                if (transparency > 1.0f) transparency = 1.0f;
                else if (transparency < 0.0f) transparency = 0.0f;

                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));

                g.drawImage(layers.get(i).image,(int) x,(int)y,(int)width,(int)height,null);
                //g.drawImage(layers.get(i).image, 0,0,this.width,this.height, null);

                g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
        switcher=!switcher;
    }

    public void drawTimeline (Graphics g, Camera camera, Frame frame, Brush brush) {
        Graphics2D g2D = (Graphics2D) g;
        Color brushColor = new Color(brush.color.getRed(),brush.color.getGreen(),brush.color.getBlue());
        if (brush.paintingType!=Brush.filling) {
            if (brush.paintingType != Brush.selectorLasso&&brush.paintingType != Brush.selectorDefault) {
                for (int i = 0; i < timeLine.size() - 1; i++) {
                    g2D.setStroke(new BasicStroke((float) (brush.width * camera.zoom), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (brush.paintingType != Brush.eraser) g.setColor(brushColor);
                    else g.setColor(Color.white);
                    g.drawLine(timeLine.get(i).x, timeLine.get(i).y, timeLine.get(i + 1).x, timeLine.get(i + 1).y);
                }
            } else if (brush.paintingType==Brush.selectorLasso) {
                for (int i = 0; i < timeLine.size(); i++) {
                    int n = i + 1;
                    if (n >= timeLine.size()) n = 0;
                    g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (switcher) g.setColor(Color.white);
                    else g.setColor(Color.black);
                    g.drawLine(timeLine.get(i).x, timeLine.get(i).y, timeLine.get(n).x, timeLine.get(n).y);
                    g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (switcher) g.setColor(Color.black);
                    else g.setColor(Color.white);
                    g.drawLine(timeLine.get(i).x, timeLine.get(i).y, timeLine.get(n).x, timeLine.get(n).y);
                }
            } else {
                Point mousePosition;
                if (frame.isUndecorated()) mousePosition = MouseInfo.getPointerInfo().getLocation();
                else mousePosition = new Point(MouseInfo.getPointerInfo().getLocation().x-frame.getX()-8,MouseInfo.getPointerInfo().getLocation().y-frame.getY()-31);

                int x = startMousePosition.x, y = startMousePosition.y;
                int width = mousePosition.x-startMousePosition.x, height = mousePosition.y-startMousePosition.y;

                if (switcher) g.setColor(Color.white);
                else g.setColor(Color.black);
                g.drawLine(x,y,x+width,y);
                g.drawLine(x,y,x,y+height);
                g.drawLine(x+width,y,x+width,y+height);
                g.drawLine(x,y+height,x+width,y+height);
                g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (switcher) g.setColor(Color.black);
                else g.setColor(Color.white);
                g.drawLine(x,y,x+width,y);
                g.drawLine(x,y,x,y+height);
                g.drawLine(x+width,y,x+width,y+height);
                g.drawLine(x,y+height,x+width,y+height);
            }
        }
    }

    public void drawSelectorLine (Graphics g, Camera camera, Frame frame) {
        Graphics2D g2D = (Graphics2D) g;
        if (!this.selected) return;
        for (int i = 0; i < selectLine.size(); i++) {
            for (int j = 0; j < selectLine.get(i).size(); j++) {
                int n = j + 1;
                if (n >= selectLine.get(i).size()) n = 0;
                double x1 = camera.getZoom(selectLine.get(i).get(j).x, (double) frame.getWidth() / 2 + camera.x) - camera.x, y1 = camera.getZoom(selectLine.get(i).get(j).y, (double) frame.getHeight() / 2 + camera.y) - camera.y;
                double x2 = camera.getZoom(selectLine.get(i).get(n).x, (double) frame.getWidth() / 2 + camera.x) - camera.x, y2 = camera.getZoom(selectLine.get(i).get(n).y, (double) frame.getHeight() / 2 + camera.y) - camera.y;

                g2D.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (switcher) g.setColor(Color.white);
                else g.setColor(Color.black);
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
                g2D.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                if (switcher) g.setColor(Color.black);
                else g.setColor(Color.white);
                g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
            }
        }
    }

    public void loadBackground () {
        background = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        Graphics2D backgroundG2D = background.createGraphics();
        backgroundG2D.setColor(Color.white);
        backgroundG2D.fillRect(0,0,width,height);
        double currentCellSizeWithoutBackground = cellSizeWithoutBackground;
        for (int i = 0; i < width/currentCellSizeWithoutBackground; i++) for (int j = 0; j < height/currentCellSizeWithoutBackground; j++) {
            if (i%2!=0&&j%2==0||i%2==0&&j%2!=0) {
                backgroundG2D.setColor(new Color(191, 191, 191));
                backgroundG2D.fillRect((int) (i * currentCellSizeWithoutBackground), (int) (j * currentCellSizeWithoutBackground), (int) currentCellSizeWithoutBackground, (int) currentCellSizeWithoutBackground);
            }
        }
        backgroundG2D.dispose();
    }

    public void centralizeTheCoordinateOfCamera (Camera camera, Frame frame) {
        camera.actionX=(double)width/2-(double)frame.getWidth()/2;
        camera.actionY=(double)height/2-(double)frame.getHeight()/2;
    }

    public void centralizeTheZoomOfCamera (Camera camera, Frame frame, boolean horizontal, int distance) {
        if (horizontal) camera.actionZoom=((double)(frame.getWidth()/2-distance)/((double)frame.getWidth()/2-((double)frame.getWidth()/2-(double)width/2)));
        else camera.actionZoom=((double)(frame.getHeight()/2-distance)/((double)frame.getHeight()/2-((double)frame.getHeight()/2-(double)height/2)));
    }

    public void floodFill(int x, int y, int startColor, int newColor, BufferedImage image, Brush brush) {
        int originalColor = image.getRGB(x, y);
        if (originalColor == newColor) {
            return;
        }

        ArrayDeque<Point> queue = new ArrayDeque<>();
        queue.add(new Point(x, y));

        while (!queue.isEmpty()) {
            Point point = queue.poll();
            int currentX = point.x;
            int currentY = point.y;

            if (currentX >= 0 && currentX < image.getWidth() && currentY >= 0 && currentY < image.getHeight()) {
                if (image.getRGB(currentX, currentY) == startColor) if (!new Color(select.image.getRGB(currentX, currentY)).equals(Color.WHITE)||!selected) {
                    if (brush.mixType== Brush.mixing) {
                        Graphics2D g2D = image.createGraphics();
                        g2D.setColor(brush.color);
                        g2D.drawLine(currentX,currentY,currentX,currentY);
                        g2D.dispose();
                    }
                    else if (brush.mixType== Brush.replace) image.setRGB(currentX, currentY, newColor);
                    else if (brush.mixType== Brush.blending) {
                        int layerColorValue = layers.get(selectedLayer).image.getRGB(currentX,currentY);

                        int layerColorAlpha = (layerColorValue >> 24) & 0xFF;
                        int layerColorRed = (layerColorValue >> 16) & 0xFF;
                        int layerColorGreen = (layerColorValue >> 8) & 0xFF;
                        int layerColorBlue = layerColorValue & 0xFF;

                        int red = layerColorRed + brush.color.getRed(), green = layerColorGreen + brush.color.getGreen(), blue = layerColorBlue + brush.color.getBlue(), alpha = layerColorAlpha + brush.color.getAlpha();

                        if (red > 255) red = 255;
                        else if (red < 0) red = 0;
                        if (green > 255) green = 255;
                        else if (green < 0) green = 0;
                        if (blue > 255) blue = 255;
                        else if (blue < 0) blue = 0;
                        if (alpha > 255) alpha = 255;
                        else if (alpha < 0) alpha = 0;

                        layers.get(selectedLayer).image.setRGB(currentX,currentY, new Color(red, green, blue, alpha).getRGB());
                    }
                    queue.add(new Point(currentX - 1, currentY));
                    queue.add(new Point(currentX + 1, currentY));
                    queue.add(new Point(currentX, currentY - 1));
                    queue.add(new Point(currentX, currentY + 1));
                }
            }
        }
    }
}