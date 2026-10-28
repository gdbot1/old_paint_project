package World.Values;

public class Dimension {
    public double width, height;

    public Dimension(double width, double height) {
        this.width=width;
        this.height=height;
    }

    public Dimension(Dimension dimension) {
        this.width=dimension.width;
        this.height=dimension.height;
    }

    public void setSize (double width, double height) {
        this.width=width;
        this.height=height;
    }

    public void setSize (Dimension dimension) {
        this.width=dimension.width;
        this.height=dimension.height;
    }
}
