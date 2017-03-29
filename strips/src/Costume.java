import hypermedia.net.UDP;
import processing.core.PApplet;

public class Costume {

    private int SEGMENTS = 12;
    int ledsCount = 170;
    final PApplet base;
    final String ipAddress;
    final int x;
    final int y;
    final UDP udp;
    float outputRGB[][] = new float[ledsCount][3];
    float setRGB[][] = new float[ledsCount][3];
    boolean enableOutput = false;
    float brightness = 1.0f;
    float attack = 1.0f;
    float release = 1.0f;
    private long fadetimer;

    public Costume(PApplet base, UDP udp, int x, int y, String ipAddress) {
        this.base = base;
        this.x = x;
        this.y = y;
        this.ipAddress = ipAddress;
        this.udp = udp;
        for (int led = 0; led < ledsCount; led++) {
            for (int color = 0; color < 3; color++) {
                setRGB[led][color] = 0;
                outputRGB[led][color] = 0;
            }
        }
    }

    void setLedCount(int count) {
        this.ledsCount = count;
        outputRGB = new float[ledsCount][3];
        setRGB = new float[ledsCount][3];
        for (int led = 0; led < ledsCount; led++) {
            for (int color = 0; color < 3; color++) {
                setRGB[led][color] = 0;
                outputRGB[led][color] = 0;
            }
        }
    }

    public void setLedColor(int led, int color) {
        setRGB[led][0] = base.red(color) / 255.0f;
        setRGB[led][1] = base.green(color) / 255.0f;
        setRGB[led][2] = base.blue(color)  / 255.0f;
    }

    public void display() {
        base.fill(0);
        base.stroke(20);
        base.rect(x, y, 60, 80);
        base.fill(base.color(255*outputRGB[0][0], 255* outputRGB[0][1],255* outputRGB[0][2]));
        base.rect(x + 5, y + 5, 50, 70);
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
            setLedColor(i, color);
        }
    }

    public void setEnabled(boolean enabled) {
        this.enableOutput = enabled;
    }

    public void brightness(float brightness) {
        if (brightness > 1.0f || brightness < 0.0f) {
            return;
        }
        this.brightness = brightness;
    }

    public void attack(float attack) {
        this.attack = attack;
    }

    public void release(float release) {
        this.release = release;
    }

    public void send() {
        if (!enableOutput) {
            return;
        }

        byte[] buffer = new byte[180 * 3 + 3];  // 3 control bytes at the beginning
        buffer[0] = 0;  // 1 for gamma = on
        buffer[1] = 0;  // strip ID (0-3)
        buffer[2] = 0;  // reserved

        for (int j = 0; j < ledsCount; j++) {
            if (brightness < 0.01) {
                buffer[3 + (j * 3)] = (byte) (0);
                buffer[3 + (j * 3) + 1] = (byte) (0);
                buffer[3 + (j * 3) + 2] = (byte) (0);
            }
            else {
                buffer[3 + (j * 3)] = (byte) (255 * outputRGB[j][0] * brightness);
                buffer[3 + (j * 3) + 1] = (byte) (255 * outputRGB[j][1] * brightness);
                buffer[3 + (j * 3) + 2] = (byte) (255 * outputRGB[j][2] * brightness);
            }
        }

        udp.send(buffer, ipAddress, 4210);
    }

    public void render() {

        if (attack >= 0.99f && release >= 0.99f) {
            // direct switching
            for (int led = 0; led < ledsCount; led++) {
                for (int color = 0; color < 3; color++) {
                    outputRGB[led][color] = setRGB[led][color];
                }
            }
        }
        else if (base.millis() - fadetimer > 10) {

            //faderamp();
            fadedelta();

            fadetimer = base.millis();
        }
    }

    private void fadedelta() {
        for (int i = 0; i < ledsCount; i++) {
            for (int color = 0; color < 3; color++) {

                float diff = outputRGB[i][color] - setRGB[i][color];

                if (Math.abs(diff) > 0.01) {
                    if (diff > 0) {
                        outputRGB[i][color] -= diff * release;
                        if (outputRGB[i][color] > 1.0f) {
                            outputRGB[i][color] = 1.0f;
                        }
                    }
                    else {
                        outputRGB[i][color] -= diff * attack;
                        if (outputRGB[i][color] < 0.0f) {
                            outputRGB[i][color] = 0.0f;
                        }
                    }
                }
            }
        }
    }

    private void faderamp() {
        for (int i = 0; i < ledsCount; i++) {
            for (int color = 0; color < 3; color++) {

                if (Math.abs(setRGB[i][color] - outputRGB[i][color]) > 0.01) {

                    if (setRGB[i][color] > outputRGB[i][color]) {
                        outputRGB[i][color] += attack;
                        if (outputRGB[i][color] > 1.0f) {
                            outputRGB[i][color] = 1.0f;
                        }
                    }
                    else if (setRGB[i][color] < outputRGB[i][color]) {
                        outputRGB[i][color] -= release;
                        if (outputRGB[i][color] < 0.0f) {
                            outputRGB[i][color] = 0.0f;
                        }
                    }

                    float diff = outputRGB[i][color] - setRGB[i][color];
                    if (diff < 0) {
                        if (Math.abs(diff) < release) {
                            outputRGB[i][color] -= diff;
                        }
                    }
                    else {
                        if (Math.abs(diff) < attack) {
                            outputRGB[i][color] -= diff;
                        }
                    }

                    if (outputRGB[i][color] > 1.0f) {
                        outputRGB[i][color] = 1.0f;
                    }
                    else if (outputRGB[i][color] < 0) {
                        outputRGB[i][color] = 0;
                    }
                }
            }
        }
    }

    public void effectMI() {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, 0);
        }
        // mi   unten linie
        setSegmentColor(2, base.color(255, 240, 0));
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

    public void effectSingleColor(int color) {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, color);
        }
    }
}
