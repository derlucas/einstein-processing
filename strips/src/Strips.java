import controlP5.ControlEvent;
import controlP5.ControlP5;
import hypermedia.net.UDP;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.awt.*;

public class Strips extends PApplet {

    private String addresses[] = {"192.168.80.121", "192.168.80.122", "192.168.80.123", "192.168.80.124",
        "192.168.80.125", "192.168.80.126", "192.168.80.127", "192.168.80.128",
        "192.168.80.129", "192.168.80.130", "192.168.80.131", "192.168.80.132"};

    private int SENDDELAY = 100;
    private int COUNT = 12;
    private int SEGMENTS = 13;
    private float ampFactor = 10.0f;
    float amp[] = new float[12];
    OscP5 oscP5;
    private ControlP5 cp5;
    private int note;
    private UDP udp;
    private boolean output[] = new boolean[COUNT];
    private boolean blackout = false;
    private float overallbrightness;
    private float redval;
    private float greenval;
    private float blueval;
    private int outputColors[][] = new int[COUNT][170];
    private int selectedEffect;
    private int step;
    private long lastSendData;
    private Costume costumes[] = new Costume[COUNT];

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        udp = new UDP(this, 2102);
        oscP5 = new OscP5(this, 2002);
        frameRate(30);

        cp5 = new ControlP5(this);
        for (int i = 0; i < COUNT; i++) {
            output[i] = false;
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(output[i]).setLabel("Pa " + (i + 1));

            for (int j = 0; j < 170; j++) {
                outputColors[i][j] = 0;
            }
            costumes[i] = new Costume(this, i * 65, 0, addresses[i]);
        }
        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(true).setLabel("BO");

        cp5.addSlider("overallbrightness").setPosition(10, 10).setSize(100, 20).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("ampFactor").setPosition(10, 35).setSize(100, 20).setRange(0, 50.0f);
        cp5.addSlider("redval").setPosition(10, 60).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, 85).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, 110).setSize(100, 20).setRange(0, 1.0f);

        cp5.addBang("bang").setPosition(250, 120).setSize(20, 20).plugTo(this, "impulse");

        cp5.addScrollableList("effectList").setPosition(300, 120)
           .setBarHeight(20).setItemHeight(20).setLabel("effect").close()
           .addItem("amp", 0).addItem("miladola", 1).addItem("RGBDEMO", 2).addItem("STEPS", 3).addItem("IRONMAN", 4);

        surface.setTitle("Stripse");
    }

    public void impulse() {
        step++;
        step %= SEGMENTS;
    }

    public void sendOutputs() {

        if(System.currentTimeMillis() - lastSendData < SENDDELAY) {
            return;
        }

        for(Costume costume: costumes) {
            costume.send(udp, blackout ? 0.0f : overallbrightness);
        }
    }

    public void draw() {
        background(0);

        drawInputAmps();
        fill(Color.HSBtoRGB(1.0f / note, 0.5f, 0.2f));
        noStroke();
        rect(200, 200, 50, 50);

        renderEffect();

        drawCostumes();
        sendOutputs();
    }

    private void drawCostumes() {
        pushMatrix();
        translate(10, 400);

        for(Costume costume: costumes) {
            costume.display();
        }

        popMatrix();
    }

    private void renderEffect() {

        if (selectedEffect == 0) {  //amp
            for (int costume = 0; costume < COUNT; costume++) {
                for (int seg = 0; seg < SEGMENTS; seg++) {
                    costumes[costume].setSegmentColor(seg, color(255*amp[0]));
                }
            }
        }
        else if (selectedEffect == 1) {

            if (keyPressed) {
                for (int i = 0; i < SEGMENTS; i++) {
                    costumes[i].setSegmentColor(i, 0);
                }

                if (key == 'l') {
                    costumes[0].setSegmentColor(0, color(20,0,255));
                    costumes[0].setSegmentColor(1, color(20,0,255));
                }
                else if (key == 'm') {
                    costumes[0].setSegmentColor(2, color(255,240,0));
                }
                else if (key == 'd') {
                    costumes[0].setSegmentColor(6, color(255,0,1));
                    costumes[0].setSegmentColor(7, color(255,0,1));
                }
            }
        }
        else if (selectedEffect == 2) { // RGB demo fade

            for (int costume = 0; costume < COUNT; costume++) {
                for (int seg = 0; seg < SEGMENTS; seg++) {
                    costumes[costume].setSegmentColor(seg, color(255*redval, 255*greenval, 255*blueval));
                }
            }

        }
        else if(selectedEffect == 3) {
            for (int costume = 0; costume < COUNT; costume++) {
                for (int seg = 0; seg < SEGMENTS; seg++) {
                    costumes[costume].setSegmentColor(seg, step == seg ? color(255) : 0);
                }
            }
        } else if (selectedEffect == 4) {
            for (int costume = 0; costume < COUNT; costume++) {
                for (int seg = 0; seg < SEGMENTS; seg++) {
                    costumes[costume].setSegmentColor(seg, 0);
                }
                costumes[costume].setSegmentColor(3,  color(255 * amp[costume], 0, 0));
                costumes[costume].setSegmentColor(4,  color(255 * amp[costume], 0, 0));
                costumes[costume].setSegmentColor(5,  color(255 * amp[costume], 0, 0));
                costumes[costume].setSegmentColor(12, color(255 * amp[costume], 0, 0));
            }
        }
    }

    private void drawInputAmps() {
        pushMatrix();
        translate(300, 20);
        for (int i = 0; i < COUNT; i++) {
            fill(255);
            text(String.format("%.2f", amp[i]), i * 40, 10);
            stroke(50);
            fill(amp[i] * 255);
            rect(i * 40, 12, 32, 32);
        }
        popMatrix();
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

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("output")) {
                int id = theEvent.getId();
                if (id >= 0 && id < output.length) {
                    output[id] = theEvent.getValue() > 0;
                }
            }
            else if (theEvent.getName().startsWith("effectList")) {
                selectedEffect = (int) theEvent.getValue();
            }
        }
    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }
}
