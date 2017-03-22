import hypermedia.net.UDP;
import processing.core.PApplet;

public class Costume {

    private final PApplet base;
    private final String ipAddress;
    private final int x;
    private final int y;
    private int outputColors[] = new int[170];
    private boolean enableOutput = false;

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
        if(!enableOutput) {
            return;
        }

        byte[] buffer = new byte[170 * 3];
        for (int j = 0; j < 170; j++) {
            if (brightness < 0.01) {
                buffer[(j * 3)] = (byte) (0);
                buffer[(j * 3) + 1] = (byte) (0);
                buffer[(j * 3) + 2] = (byte) (0);
            }
            else {
                buffer[(j * 3)] = (byte) (base.red(outputColors[j]) * brightness);
                buffer[(j * 3) + 1] = (byte) (base.green(outputColors[j]) * brightness);
                buffer[(j * 3) + 2] = (byte) (base.blue(outputColors[j]) * brightness);
            }
        }

        udp.send(buffer, ipAddress, 4210);
    }

}
