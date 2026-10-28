package World.Values;

public class Point {
    public double x, y;

    public Point (double x, double y) {
        this.x=x;
        this.y=y;
    }

    public Point (Point point) {
        this.x=point.x;
        this.y=point.y;
    }

    public void setLocation (double x, double y) {
        this.x=x;
        this.y=y;
    }

    public void setLocation (Point point) {
        this.x=point.x;
        this.y=point.y;
    }
}
