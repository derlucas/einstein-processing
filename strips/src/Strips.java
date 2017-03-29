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

    private int SENDDELAY = 40;
    private int COUNT = 12;
    private int SEGMENTS = 18;
    private float ampFactor = 10.0f;
    float amp[] = new float[12];
    OscP5 oscP5;
    private ControlP5 cp5;
    private int note;
    private int velocity;
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
        frameRate(60);

        udp = new UDP(this, 2102);
        oscP5 = new OscP5(this, 2002);
        cp5 = new ControlP5(this);

        //        costumes[0] = new CostumeKatharina(this, 0, 0, addresses[0]);
        costumes[0] = new Costume(this, udp, 0, 0, addresses[0]);
        for (int i = 1; i < COUNT; i++) {
            costumes[i] = new Costume(this, udp, i * 65, 0, addresses[i]);
        }

        for (int i = 0; i < COUNT; i++) {
            output[i] = false;
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(output[i]).setLabel("Pa " + (i + 1));
            for (int j = 0; j < 170; j++) {
                outputColors[i][j] = 0;
            }
        }



        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(true).setLabel("BO");
        cp5.addSlider("overallbrightness").setPosition(10, 10).setSize(100, 20).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("ampFactor").setPosition(10, 35).setSize(100, 20).setRange(0, 100.0f);
        cp5.addSlider("redval").setPosition(10, 60).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, 85).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, 110).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("attack").setPosition(10, 135).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        cp5.addSlider("release").setPosition(10, 160).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);

        cp5.addBang("bang").setPosition(250, 120).setSize(20, 20).plugTo(this, "impulse");
        cp5.addScrollableList("effectList").setPosition(300, 120)
                .setBarHeight(20).setItemHeight(20).setLabel("effect").close()
                .addItem("amp", 0).addItem("miladola", 1)
                .addItem("RGBDEMO", 2).addItem("STEPS", 3);

        surface.setTitle("Stripse");
    }

    public void impulse() {
        step++;
        step %= SEGMENTS;
    }

    public void sendOutputs() {
        if (System.currentTimeMillis() - lastSendData < SENDDELAY) {
            return;
        }

        lastSendData = System.currentTimeMillis();

        for (Costume costume : costumes) {
            costume.send();
        }
    }

    public void draw() {
        background(0);

        drawInputAmps();
        fill(Color.HSBtoRGB(1.0f / 12 * note, 0.5f, 0.5f));
        noStroke();
        rect(200, 200, 50, 50);

        renderEffect();

        for(Costume costume: costumes) {
            costume.render();
        }

        drawCostumes();
        sendOutputs();
    }

    private void drawCostumes() {
        pushMatrix();
        translate(10, 400);

        for (Costume costume : costumes) {
            costume.display();
        }

        popMatrix();
    }

    private void renderEffect() {

        if (selectedEffect == 0) {  //amp
            for (int costume = 0; costume < COUNT; costume++) {
                costumes[costume].effectSingleColor(color(255 * amp[costume]));
            }
        } else if (selectedEffect == 2) { // RGB demo fade
            for (int costume = 0; costume < COUNT; costume++) {
                costumes[costume].effectSingleColor(color(255 * redval, 255 * greenval, 255 * blueval));
            }
        } else if (selectedEffect == 3) {
            for (int costume = 0; costume < COUNT; costume++) {
                for (int seg = 0; seg < SEGMENTS; seg++) {
                    costumes[costume].setSegmentColor(seg, step == seg ? color(255) : 0);
                }
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
            rect(i * 40, 12, 30, 30);
        }
        popMatrix();
    }

    void oscEvent(OscMessage msg) {

        String addr = msg.addrPattern();

        if (addr.startsWith("amp")) {
            int channel = Integer.parseInt(addr.substring(3));
            amp[channel - 1] = msg.get(0).floatValue();
            amp[channel - 1] = amp[channel - 1] * ampFactor;
        } else if (msg.checkAddrPattern("/midinote")) {
            note = msg.get(0).intValue() % 12;
            velocity = msg.get(1).intValue();
            System.out.println("addr: " + addr + " " + msg.get(0).intValue() + " vel: " + msg.get(1).intValue());

            if (selectedEffect == 1 && velocity != 0) {  // mi la do la
                if (note == 9) { // la
                    for (Costume costume: costumes) {
                        costume.effectLA();
                    }
                } else if (note == 4) { // mi   unten linie
                    for (Costume costume: costumes) {
                        costume.effectMI();
                    }
                } else if (note == 0) { // do
                    for (Costume costume: costumes) {
                        costume.effectDO();
                    }
                }
            }
        } else if (msg.checkAddrPattern("/bjmidi")) {
            System.out.println(msg.get(0).intValue());
        }

    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("output")) {
                int id = theEvent.getId();
                if (id >= 0 && id < COUNT) {
                    output[id] = theEvent.getValue() > 0;
                    costumes[id].setEnabled(output[id]);
                }
            } else if (theEvent.getName().startsWith("effectList")) {
                selectedEffect = (int) theEvent.getValue();
            } else if(theEvent.getName().startsWith("attack")) {
                for (Costume costume: costumes) {
                    costume.attack(theEvent.getValue());
                }
            } else if(theEvent.getName().startsWith("release")) {
                for (Costume costume: costumes) {
                    costume.release(theEvent.getValue());
                }
            } else if(theEvent.getName().startsWith("overallbrightness")) {
                for (Costume costume: costumes) {
                    costume.brightness(theEvent.getValue());
                }
            }
        }
    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }
}
