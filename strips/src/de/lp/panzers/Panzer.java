package de.lp.panzers;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class Panzer {

    public static final int SEGMENTS = 5;
    private float outputValues[] = new float[SEGMENTS];
    private float setValues[] = new float[SEGMENTS];
    private final PApplet base;
    private final String ipAddress;
    private final UDP udp;
    private boolean blackout;
    private boolean enableOutput;
    private float brightness = 1.0f;
    private float attack = 1.0f;
    private float release = 1.0f;
    private long fadetimer;

    public Panzer(PApplet base, UDP udp, String ipAddress) {
        this.base = base;
        this.ipAddress = ipAddress;
        this.udp = udp;
    }

    public void effectSingleColor(int color) {
        setValues[0] = base.red(color) / 255.0f;
        setValues[1] = base.green(color) / 255.0f;
        setValues[2] = base.blue(color) / 255.0f;
        setValues[3] = setValues[2];
        setValues[4] = setValues[2];
    }

    public void setSegmentBrightness(int segment, float brightness) {
        setValues[segment] = (byte) (PApplet.constrain(brightness, 0, 1));
    }

    public void black() {
        effectSingleColor(0);
    }

    public void setBlackout(boolean blackout) {
        this.blackout = blackout;
    }

    public void display() {

        int j = 0;
        base.fill(255 * outputValues[j] * brightness, 0, 0);
        base.stroke(255 * outputValues[j] * brightness, 0, 0);
        base.rect(j * 10, 0, 10, 30);
        j++;

        base.fill(0, 255 * outputValues[j] * brightness, 0);
        base.stroke(0, 255 * outputValues[j] * brightness, 0);
        base.rect(j * 10, 0, 10, 30);

        j++;
        base.fill(0, 0, 255 * outputValues[j] * brightness);
        base.stroke(0, 0, 255 * outputValues[j] * brightness);
        base.rect(j * 10, 0, 10, 30);

        j++;
        base.fill(0, 0, 255 * outputValues[j] * brightness);
        base.stroke(0, 0, 255 * outputValues[j] * brightness);
        base.rect(j * 10, 0, 10, 30);

        j++;
        base.fill(0, 0, 255 * outputValues[j] * brightness);
        base.stroke(0, 0, 255 * outputValues[j] * brightness);
        base.rect(j * 10, 0, 10, 30);


        base.stroke(50);
        base.noFill();
        base.rect(0, 0, 50, 30);

    }

    public void render() {

        if (attack >= 0.99f && release >= 0.99f) {
            // direct switching
            for (int led = 0; led < SEGMENTS; led++) {
                outputValues[led] = setValues[led];
            }
        } else if (base.millis() - fadetimer > 10) {
            fadedelta();
            fadetimer = base.millis();
        }
    }

    private void fadedelta() {
        for (int i = 0; i < SEGMENTS; i++) {

            float diff = outputValues[i] - setValues[i];

            if (Math.abs(diff) > 0.01) {
                if (diff > 0) {
                    outputValues[i] -= diff * release;
                    if (outputValues[i] > 1.0f) {
                        outputValues[i] = 1.0f;
                    }
                } else {
                    outputValues[i] -= diff * attack;
                    if (outputValues[i] < 0.0f) {
                        outputValues[i] = 0.0f;
                    }
                }
            }
        }
    }


    public void send() {
        if (!enableOutput) {
            return;
        }

        byte[] buffer = new byte[SEGMENTS];
        for (int j = 0; j < SEGMENTS; j++) {
            if (blackout) {
                buffer[j] = 0;
            } else {
                buffer[j] = (byte) (255 * outputValues[j] * brightness);
            }
        }

        udp.send(buffer, ipAddress, 4210);
    }

    public void setEnabled(boolean enabled) {
        this.enableOutput = enabled;
    }

    public void setBrightness(float brightness) {
        this.brightness = brightness;
    }

    public void setAttack(float attack) {
        this.attack = attack;
    }

    public void setRelease(float release) {
        this.release = release;
    }
}
