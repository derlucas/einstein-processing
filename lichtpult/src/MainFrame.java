import controlP5.ControlEvent;
import controlP5.ControlP5;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class MainFrame extends PApplet {

    private String addresses[] = {"192.168.80.101", "192.168.80.102", "192.168.80.103", "192.168.80.104",
            "192.168.80.105", "192.168.80.106", "192.168.80.107", "192.168.80.108",
            "192.168.80.109", "192.168.80.110", "192.168.80.111", "192.168.80.112"};

    private int COUNT = 6;
    private int SENDDELAY = 20;
    private float setValues[] = new float[COUNT];
    private float outputValues[] = new float[COUNT];
    private float outputValuesLast[] = new float[COUNT];
    private ControlP5 cp5;
    private int selectedEffect;
    private boolean blackout = false;
    private OscP5 oscP5;
    private NetAddress gio;
    private float fade = 0;
    private long fadetimer;
    private long lastSendData;


    public void settings() {
        size(800, 600);
    }

    public void setup() {
        oscP5 = new OscP5(this, 2001);
        cp5 = new ControlP5(this);
        gio = new NetAddress("192.168.79.5", 3000);

        for (int i = 0; i < COUNT; i++) {
            cp5.addSlider("submaster" + i).setPosition(10, 135 + i * 35).setId(i).setSize(100, 20).setRange(0.0f, 1.0f).setValue(0.0f);
        }

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(true).setLabel("BO");
        cp5.addSlider("fade").setPosition(10, 10).setSize(100, 20).setRange(0, 1.0f).setValue(0);

        cp5.addScrollableList("effectList").setPosition(300, 120)
                .setBarHeight(20).setItemHeight(20).setLabel("effect").close()
                .addItem("amp", 0).addItem("chaser", 1);

        frameRate(30);
        surface.setTitle("LICHT");
    }

    public void draw() {
        background(0);
        fill(255);

        renderEffect();

        transformSetToOutput();
        sendOSCGIOData();
    }


    private void renderEffect() {

        if (selectedEffect == 0) {   // AMP
            for (int i = 0; i < COUNT; i++) {

            }
        } else if (selectedEffect == 1) {
            for (int i = 0; i < COUNT; i++) {

            }
        } else if (selectedEffect == 2) {

        }
    }

    private void transformSetToOutput() {
        if (fade == 0) {
            for (int i = 0; i < COUNT; i++) {
                outputValues[i] = setValues[i];

                if (outputValues[i] > 1.0f) {
                    outputValues[i] = 1.0f;
                } else if (outputValues[i] < 0.0f) {
                    outputValues[i] = 0.0f;
                }
            }
        } else if (millis() - fadetimer > 10) {

            for (int i = 0; i < COUNT; i++) {

                if (abs(setValues[i] - outputValues[i]) > 0.01f) {

                    if (setValues[i] > outputValues[i]) {
                        outputValues[i] += fade/10;
                    } else if (setValues[i] < outputValues[i]) {
                        outputValues[i] -= fade/10;
                    }

                    float diff = outputValues[i] - setValues[i];
                    if (abs(diff) < fade) {
                        outputValues[i] -= diff;
                    }

                    if (outputValues[i] > 1.0f) {
                        outputValues[i] = 1.0f;
                    } else if (outputValues[i] < 0.0f) {
                        outputValues[i] = 0.0f;
                    }
                }
            }

            fadetimer = millis();
        }
    }

    void oscEvent(OscMessage msg) {

        if (msg.checkAddrPattern("/midinote")) {

            int note = msg.get(0).intValue();
            note %= 12;

            if (note == 9) { // la

            } else if (note == 4) { // mi

            } else if (note == 0) { // do

            }
        }
    }

    public void sendOSCGIOData() {

        if (System.currentTimeMillis() - lastSendData < SENDDELAY) {
            return;
        }

        lastSendData = System.currentTimeMillis();

        for (int i = 0; i < COUNT; i++) {
            if (abs(outputValues[i] - outputValuesLast[i]) > 0.01f) {
                OscMessage myMessage = new OscMessage("/eos/sub/" + (i + 1));
                myMessage.add(blackout ? 0.0f : outputValues[i]);
                oscP5.send(myMessage, gio);
                System.out.println("sende " + (i + 1) + " : " + (blackout ? 0.0f : outputValues[i]));

                outputValuesLast[i] = outputValues[i];
            }
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("submaster")) {
                int id = theEvent.getId();

//                System.out.println(theEvent.getValue());
                setValues[id] = theEvent.getValue();

//                if (id >= 0 && id < output.length) {
//                    output[id] = theEvent.getValue() > 0;
//                }

            } else if (theEvent.getName().startsWith("effectList")) {
                selectedEffect = (int) theEvent.getValue();
            }
        }
    }


    public static void main(String args[]) {
        PApplet.main("MainFrame");
    }
}
