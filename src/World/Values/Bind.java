package World.Values;

public class Bind {
    public int[] key;

    public Bind(int[] key) {
        this.key=key;
    }

    public boolean check (int inputKey) {
        for (int i : key) if (i == inputKey) return true;
        return false;
    }
}
