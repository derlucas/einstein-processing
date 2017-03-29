import hypermedia.net.UDP;
import processing.core.PApplet;

public class Costume {

    public int segmentation[][] = new int[][]{
        {35, 64}, {100, 129}, {130, 149}, {156, 163},
        {76, 81}, {11, 16}, {18, 34}, {83, 99},
        {150, 155}, {164, 169}, {65, 74}, {0, 9},
        {82, 82},
        {169, 169}, {169, 169}, {169, 169}, {169, 169}, {169, 169}
    };

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

    Costume(PApplet base, UDP udp, int x, int y, String ipAddress) {
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

    void setLedColor(int led, int color) {
        setRGB[led][0] = base.red(color) / 255.0f;
        setRGB[led][1] = base.green(color) / 255.0f;
        setRGB[led][2] = base.blue(color) / 255.0f;
    }

    void display() {
        base.fill(0);
        base.stroke(20);
        base.rect(x, y, 60, 80);
        base.fill(base.color(255 * brightness * outputRGB[0][0], 255 * brightness * outputRGB[0][1], 255 * brightness * outputRGB[0][2]));
        base.rect(x + 5, y + 5, 50, 70);
    }

    void setSegmentColor(int segment, int color) {
        if (segment < 0 || segment > Strips.SEGMENTS) {
            return;
        }

        int from = segmentation[segment][0], to = segmentation[segment][1];

/*        switch (segment) {
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
        }    */

        for (int i = from; i <= to; i++) {
            setLedColor(i, color);
        }
    }

    void setEnabled(boolean enabled) {
        this.enableOutput = enabled;
    }

    void brightness(float brightness) {
        if (brightness > 1.0f || brightness < 0.0f) {
            return;
        }
        this.brightness = brightness;
    }

    void attack(float attack) {
        this.attack = attack;
    }

    void release(float release) {
        this.release = release;
    }

    void send() {
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

    void render() {

        if (attack >= 0.99f && release >= 0.99f) {
            // direct switching
            for (int led = 0; led < ledsCount; led++) {
                for (int color = 0; color < 3; color++) {
                    outputRGB[led][color] = setRGB[led][color];
                }
            }
        }
        else if (base.millis() - fadetimer > 10) {
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

    void effectMI() {
        effectSingleColor(0);
        // mi   unten linie
        setSegmentColor(2, base.color(255, 240, 0));
    }

    void effectLA() {
        effectSingleColor(0);

        // hosenträger
        setSegmentColor(0, base.color(0, 0, 255));
        setSegmentColor(1, base.color(0, 0, 255));
    }

    void effectDO() {
        effectSingleColor(0);

        // V neck
        setSegmentColor(6, base.color(255, 0, 0));
        setSegmentColor(7, base.color(255, 0, 0));
    }

    void effectSingleColor(int color) {
        for (int i = 0; i < Strips.SEGMENTS; i++) {
            setSegmentColor(i, color);
        }
    }
}
