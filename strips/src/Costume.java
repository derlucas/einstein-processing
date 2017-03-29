import hypermedia.net.UDP;
import processing.core.PApplet;

public class Costume {

    private int SEGMENTS = 12;
    int ledsCount = 170;
    final PApplet base;
    final String ipAddress;
    final int x;
    final int y;
    int outputColors[] = new int[ledsCount];
    boolean enableOutput = false;

    public Costume(PApplet base, int x, int y, String ipAddress) {
        this.base = base;
        this.x = x;
        this.y = y;
        this.ipAddress = ipAddress;
    }

    public void display() {
        base.fill(0);
        base.stroke(20);
        base.rect(x, y, 60, 80);
    }

    public void setSegmentColor(int segment, int color) {

        int from = 0, to = 0;

        switch (segment) {
            case 0:
                from = 35;
                to = 64;
                break;
            case 1:
                from = 100;
                to = 129;
                break;
            case 2:
                from = 130;
                to = 149;
                break;
            case 3:
                from = 156;
                to = 163;
                break;
            case 4:
                from = 76;
                to = 81;
                break;
            case 5:
                from = 11;
                to = 16;
                break;
            case 6:
                from = 18;
                to = 34;
                break;
            case 7:
                from = 83;
                to = 99;
                break;
            case 8:
                from = 150;
                to = 155;
                break;
            case 9:
                from = 164;
                to = 169;
                break;
            case 10:
                from = 65;
                to = 74;
                break;
            case 11:
                from = 0;
                to = 9;
                break;
            case 12:
                from = 82;
                to = 82;
                break;
        }
        for (int i = from; i <= to; i++) {
            outputColors[i] = color;
        }
    }

    public void setEnabled(boolean enabled) {
        this.enableOutput = enabled;
    }

    public boolean isEnabled() {
        return this.enableOutput;
    }

    public void send(UDP udp, float brightness) {
        if (!enableOutput) {
            return;
        }

        byte[] buffer = new byte[180 * 3 + 3];  // 3 control bytes at the beginning
        buffer[0] = 0;  // 1 for gamma = on
        buffer[1] = 0;
        buffer[2] = 0;

        for (int j = 0; j < ledsCount; j++) {
            if (brightness < 0.01) {
                buffer[3 + (j * 3)] = (byte) (0);
                buffer[3 + (j * 3) + 1] = (byte) (0);
                buffer[3 + (j * 3) + 2] = (byte) (0);
            } else {
                buffer[3 + (j * 3)] = (byte) (base.red(outputColors[j]) * brightness);
                buffer[3 + (j * 3) + 1] = (byte) (base.green(outputColors[j]) * brightness);
                buffer[3 + (j * 3) + 2] = (byte) (base.blue(outputColors[j]) * brightness);
            }
        }

        udp.send(buffer, ipAddress, 4210);
    }


    public void effectMI() {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, 0);
        }

        // mi   unten linie
        setSegmentColor(2, base.color(255, 240, 0));
//        setSegmentColor(12, base.color(255, 240, 0));
//        setSegmentColor(13, base.color(255, 240, 0));
    }

    public void effectLA() {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, 0);
        }

        // hosenträger
        setSegmentColor(0, base.color(0, 0, 255));
        setSegmentColor(1, base.color(0, 0, 255));
    }

    public void effectDO() {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, 0);
        }

        // V neck
        setSegmentColor(6, base.color(255, 0, 0));
        setSegmentColor(7, base.color(255, 0, 0));

    }

}
