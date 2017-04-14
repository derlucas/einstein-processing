package de.lp.strips;

import de.lp.Symbols;
import hypermedia.net.UDP;
import processing.core.PApplet;

public class Strip {

    public static final int SEGMENTS = 18;
    int segmentation[][];
    final PApplet base;
    private final String ipAddress;
    private final UDP udp;
    int ledsCount;
    private float outputRGB[][];
    private boolean enableOutput = false;
    private float brightness = 1.0f;
    private boolean blackout = false;

    public Strip(PApplet base, UDP udp, String ipAddress, int segmentation[][], int ledsCount) {
        this.base = base;
        this.ipAddress = ipAddress;
        this.udp = udp;
        setLedCount(ledsCount);
        this.segmentation = segmentation;
    }

    private void setLedCount(int count) {
        this.ledsCount = count;
        outputRGB = new float[ledsCount][3];
        for (int led = 0; led < ledsCount; led++) {
            for (int color = 0; color < 3; color++) {
                outputRGB[led][color] = 0;
            }
        }
    }

    void setLedColor(int led, int color) {
        outputRGB[led][0] = base.red(color) / 255.0f;
        outputRGB[led][1] = base.green(color) / 255.0f;
        outputRGB[led][2] = base.blue(color) / 255.0f;
    }

    public void display() {
        base.fill(0);
        base.stroke(20);

        int ledId = 0;
        for (int j = 0; j < 20; j++) {
            for (int i = 0; i < 10; i++) {
                if (ledId < ledsCount) {
                    base.fill(base.color(255 * brightness * outputRGB[ledId][0], 255 * brightness * outputRGB[ledId][1], 255 * brightness * outputRGB[ledId][2]));
                    base.rect(6 * i, 6 * j, 6, 6);
                }
                ledId++;
            }
        }
    }

    public void setSegmentColor(int segment, int color) {
        if (segment < 0 || segment > SEGMENTS) {
            return;
        }

        for (int i = segmentation[segment][0]; i <= segmentation[segment][1]; i++) {
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

    public void blackout(boolean bo) {
        this.blackout = bo;
    }


    public void send() {
        if (!enableOutput) {
            return;
        }

        byte[] buffer = new byte[190 * 3 + 3];  // 3 control bytes at the beginning
        buffer[0] = 0;  // 1 for gamma = on
        buffer[1] = 0;  // strip ID (0-3)
        buffer[2] = 0;  // reserved

        for (int j = 0; j < 190; j++) {
            if (blackout || brightness < 0.01) {
                buffer[3 + (j * 3)] = (byte) (0);
                buffer[3 + (j * 3) + 1] = (byte) (0);
                buffer[3 + (j * 3) + 2] = (byte) (0);
            }
            else {
                if(j < ledsCount) {
                    buffer[3 + (j * 3)] = (byte) (255 * outputRGB[j][0] * brightness);
                    buffer[3 + (j * 3) + 1] = (byte) (255 * outputRGB[j][1] * brightness);
                    buffer[3 + (j * 3) + 2] = (byte) (255 * outputRGB[j][2] * brightness);
                }
            }
        }

        udp.send(buffer, ipAddress, 4210);
    }

    public void black() {
        effectSingleColor(0);
    }

    public void effectMI() {
        effectSingleColor(0);
        // mi   unten linie
        int color = base.color(0,0,255);
        setSegmentColor(2,  color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }

    public void effectLA() {
        effectSingleColor(0);

        // hosenträger
        int color = base.color(0,0,255);
        setSegmentColor(0,  color);
        setSegmentColor(1,  color);
        setSegmentColor(16, color);
        setSegmentColor(17, color);
    }

    public void effectDO() {
        effectSingleColor(0);
        // X
        int color = base.color(255,0,0);
        setSegmentColor(6,  color);
        setSegmentColor(7,  color);
        setSegmentColor(4,  color);
        setSegmentColor(5,  color);
        setSegmentColor(14, color);
        setSegmentColor(15, color);
    }

    public void effectSingleColor(int color) {
        for (int i = 0; i < SEGMENTS; i++) {
            setSegmentColor(i, color);
        }
    }


    public void effect110cmLine(int color) {       // obere Linie
        setSegmentColor(3, color);
        setSegmentColor(8, color);
        setSegmentColor(9, color);
        setSegmentColor(10, color);
        setSegmentColor(11, color);
    }

    public void effectSymbol(Symbols symbol, int color) {

        switch (symbol) {
            case RIGHT:
                setSegmentColor(0, color);
                setSegmentColor(16, color);
                break;
            case LEFT:
                setSegmentColor(1, color);
                setSegmentColor(17, color);
                break;
            case BACKSLASH:
                setSegmentColor(5, color);
                setSegmentColor(6, color);
                setSegmentColor(15, color);
                break;
            case SLASH:
                setSegmentColor(4, color);
                setSegmentColor(7, color);
                setSegmentColor(14, color);
                break;
            case MINUS:
                effect110cmLine(color);
                break;
            case SUSPENDERS:
                setSegmentColor(0, color);
                setSegmentColor(1, color);
                setSegmentColor(16, color);
                setSegmentColor(17, color);
                break;
            case X:
                setSegmentColor(4, color);
                setSegmentColor(5, color);
                setSegmentColor(6, color);
                setSegmentColor(7, color);
                setSegmentColor(14, color);
                setSegmentColor(15, color);
                break;
            case UX:
                setSegmentColor(0, color);
                setSegmentColor(1, color);
                setSegmentColor(2, color);
                setSegmentColor(3, color);
                setSegmentColor(4, color);
                setSegmentColor(5, color);
                setSegmentColor(6, color);
                setSegmentColor(7, color);
                setSegmentColor(8, color);
                setSegmentColor(9, color);
                setSegmentColor(14, color);
                setSegmentColor(15, color);
                setSegmentColor(16, color);
                setSegmentColor(17, color);
                break;
            default:
                effectSingleColor(0);
                break;
        }
    }
}
