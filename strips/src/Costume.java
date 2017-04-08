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
//    float setRGB[][] = new float[ledsCount][3];
    boolean enableOutput = false;
    float brightness = 1.0f;
//    float attack = 1.0f;
//    float release = 1.0f;
//    private long fadetimer;

    Costume(PApplet base, UDP udp, int x, int y, String ipAddress) {
        this.base = base;
        this.x = x;
        this.y = y;
        this.ipAddress = ipAddress;
        this.udp = udp;
        for (int led = 0; led < ledsCount; led++) {
            for (int color = 0; color < 3; color++) {
//                setRGB[led][color] = 0;
                outputRGB[led][color] = 0;
            }
        }
    }

    void setLedCount(int count) {
        this.ledsCount = count;
        outputRGB = new float[ledsCount][3];
//        setRGB = new float[ledsCount][3];
        for (int led = 0; led < ledsCount; led++) {
            for (int color = 0; color < 3; color++) {
//                setRGB[led][color] = 0;
                outputRGB[led][color] = 0;
            }
        }
    }

    void setLedColor(int led, int color) {
//        setRGB[led][0] = base.red(color) / 255.0f;
//        setRGB[led][1] = base.green(color) / 255.0f;
//        setRGB[led][2] = base.blue(color) / 255.0f;
        outputRGB[led][0] = base.red(color) / 255.0f;
        outputRGB[led][1] = base.green(color) / 255.0f;
        outputRGB[led][2] = base.blue(color) / 255.0f;
    }

    void display() {
        base.fill(0);
        base.stroke(20);
        base.pushMatrix();
        base.translate(x, y);
        //base.rect(0, 0, 60, ledsCount / 10 * 6);

        int ledId = 0;
        for (int j = 0; j < 20; j++) {
            for(int i = 0; i < 10; i++) {
                if(ledId < ledsCount) {
                    base.fill(base.color(255 * brightness * outputRGB[ledId][0], 255 * brightness * outputRGB[ledId][1], 255 * brightness * outputRGB[ledId][2]));
                    base.rect(6 * i, 6 * j, 6, 6);
                }
                ledId++;
            }
        }

        base.popMatrix();
    }

    void setSegmentColor(int segment, int color) {
        if (segment < 0 || segment > Strips.SEGMENTS) {
            return;
        }

        for (int i = segmentation[segment][0]; i <= segmentation[segment][1]; i++) {
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

//    void attack(float attack) {
//        this.attack = attack;
//    }
//
//    void release(float release) {
//        this.release = release;
//    }

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

//    void render() {
//
//        if (attack >= 0.99f && release >= 0.99f) {
//             direct switching
//            for (int led = 0; led < ledsCount; led++) {
//                for (int color = 0; color < 3; color++) {
//                    outputRGB[led][color] = setRGB[led][color];
//                }
//            }
//        }
//        else if (base.millis() - fadetimer > 10) {
//            fadedelta();
//            fadetimer = base.millis();
//        }
//    }

//    private void fadedelta() {
//        for (int i = 0; i < ledsCount; i++) {
//            for (int color = 0; color < 3; color++) {
//
//                float diff = outputRGB[i][color] - setRGB[i][color];
//
//                if (Math.abs(diff) > 0.01) {
//                    if (diff > 0) {
//                        outputRGB[i][color] -= diff * release;
//                        if (outputRGB[i][color] > 1.0f) {
//                            outputRGB[i][color] = 1.0f;
//                        }
//                    }
//                    else {
//                        outputRGB[i][color] -= diff * attack;
//                        if (outputRGB[i][color] < 0.0f) {
//                            outputRGB[i][color] = 0.0f;
//                        }
//                    }
//                }
//            }
//        }
//    }

    void effectMI() {
        effectSingleColor(0);
        // mi   unten linie
        setSegmentColor(2, base.color(255, 240, 0));
        setSegmentColor(12, base.color(255, 240, 0));
        setSegmentColor(13, base.color(255, 240, 0));
    }

    void effectLA() {
        effectSingleColor(0);

        // hosenträger
        setSegmentColor(0, base.color(0, 0, 255));
        setSegmentColor(1, base.color(0, 0, 255));
        setSegmentColor(16, base.color(0, 0, 255));
        setSegmentColor(17, base.color(0, 0, 255));
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

    void effect110cmLine(int color) {
        //effectSingleColor(0);
        setSegmentColor(3, color);
        setSegmentColor(8, color);
        setSegmentColor(9, color);
        setSegmentColor(10, color);
        setSegmentColor(11, color);
    }
}
