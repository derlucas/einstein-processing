import controlP5.ControlP5;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.awt.*;

public class Strips extends PApplet {

    private float ampFactor = 10.0f;
    private int COUNT = 12;
    float amp[] = new float[12];
    OscP5 oscP5;
    private ControlP5 cp5;
    private int note;



    public void settings() {
        size(800, 600);
    }

    public void setup() {
        oscP5 = new OscP5(this, 2001);
        frameRate(30);

        cp5 = new ControlP5(this);
        cp5.addSlider("ampFactor").setPosition(10, 70).setSize(500, 20).setRange(0, 50.0f);
    }


    public void draw() {
        background(0);

        for (int i = 0; i < COUNT; i++) {
            fill(255);
            text(String.format("%.5f", amp[i]), 10, 120 + i * 20);
            stroke(50);
            fill(amp[i] * 255);
            rect(i * 65, 0, 60, 60);
        }

        fill(Color.HSBtoRGB(1.0f / note, 0.5f, 0.2f));
        noStroke();
        rect(200,200, 50,50);
    }


    void oscEvent(OscMessage msg) {

//        System.out.println(msg.addrPattern());
        String addr = msg.addrPattern();

        if (addr.startsWith("amp")) {
            int channel = Integer.parseInt(addr.substring(3));
            amp[channel - 1] = msg.get(0).floatValue();
            amp[channel - 1] = amp[channel - 1] * ampFactor;
        }

        if (msg.checkAddrPattern("/keyNote")) {
            note = msg.get(0).intValue();
        }


    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }

}
