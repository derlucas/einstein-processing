import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import hypermedia.net.UDP;
import processing.core.PApplet;

public class MainFrame extends PApplet {

    String addresses[] = {"192.168.79.101", "192.168.79.102", "192.168.79.103", "192.168.79.104",
        "192.168.79.105", "192.168.79.106", "192.168.79.107", "192.168.79.108",
        "192.168.79.109", "192.168.79.110", "192.168.79.111", "192.168.79.112"};

    int PANZER = 12;
    int CHANNELS = 5;
    UDP udp;
    int outputValues[][] = new int[PANZER][CHANNELS];
    int setValues[][] = new int[PANZER][CHANNELS];
    ControlP5 cp5;
    float ampFactor = 0.5f;
    float ampValue = 0.3f;
    float freqValue = 0.0f;
    float minVal = 0.0f;
    float maxVal = 0.0f;
    boolean impulse = false;
    boolean output[] = new boolean[PANZER];
    DropdownList ddEffect;
    int selectedEffect;
    int chaserStep = 0;
    boolean blackout = false;

    float jitter = 0.0f;
    float jitters[][] = new float[PANZER][CHANNELS];
    long jittertimer;
    int jitterdelay;
    int fade = 0;
    long fadetimer;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        udp = new UDP(this, 2001);
        udp.listen(true);
        cp5 = new ControlP5(this);

        for (int i = 0; i < PANZER; i++) {
            for (int j = 0; j < CHANNELS; j++) {
                outputValues[i][j] = 0;
            }
            output[i] = true;
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(output[i]).setLabel("Pa " + (i + 1));
        }

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(true).setLabel("BO");

        cp5.addSlider("ampFactor").setPosition(10, 10).setSize(100, 20).setRange(0, 5.0f);
        cp5.addSlider("jitter").setPosition(10, 35).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("jitterdelay").setPosition(10, 60).setSize(100, 20).setRange(10, 1000);
        cp5.addSlider("minVal").setPosition(10, 85).setSize(100, 20).setRange(0, 1.0f).setValue(0.2f);
        cp5.addSlider("maxVal").setPosition(10, 110).setSize(100, 20).setRange(0, 1.0f).setValue(0.75f);
        cp5.addSlider("fade").setPosition(10, 135).setSize(100, 20).setRange(0, 80).setValue(0);

        cp5.addBang("bang").setPosition(250,120).setSize(20,20).plugTo(this,"impulse");

        cp5.addScrollableList("effectList").setPosition(300, 120)
           .setBarHeight(20).setItemHeight(20).setLabel("effect").close().addItem("amp", 0).addItem("chaser", 1);

        frameRate(30);
        surface.setTitle("Panzer");
    }

    public void impulse() {
        this.impulse = !this.impulse;
        this.chaserStep++;
        this.chaserStep %= PANZER;
    }

    public void draw() {
        background(0);

        fill(255);
        text("amp:  " + ampValue, 500, 10);
        text("freq: " + freqValue, 650, 10);
        text("min: " + minVal, 350, 10);

        stroke(impulse ? 255 : 0);
        fill(impulse ? 255 : 0);
        rect(0, 0, 5, 5);

        renderEffect();
        calculateJitters();

        transformSetToOutput();

        drawOutput();
        sendPanzer();
    }

    private void transformSetToOutput() {
        if (fade == 0) {
            for (int i = 0; i < PANZER; i++) {
                for (int j = 0; j < CHANNELS; j++) {
                    outputValues[i][j] = setValues[i][j];

                    if (outputValues[i][j] > 255) { outputValues[i][j] = 255; }
                    else if (outputValues[i][j] < 0) { outputValues[i][j] = 0; }
                }
            }
        }
        else if (millis() - fadetimer > 10) {

            for (int i = 0; i < PANZER; i++) {
                for (int j = 0; j < CHANNELS; j++) {

                    if(setValues[i][j] != outputValues[i][j]) {

                        if (setValues[i][j] > outputValues[i][j]) {
                            outputValues[i][j] += fade;
                        }
                        else if (setValues[i][j] < outputValues[i][j]) {
                            outputValues[i][j] -= fade;
                        }

                        int diff = outputValues[i][j] - setValues[i][j];
                        if(abs(diff) < fade) {
                            outputValues[i][j] -= diff;
                        }

                        if (outputValues[i][j] > 255) {
                            outputValues[i][j] = 255;
                        }
                        else if (outputValues[i][j] < 0) {
                            outputValues[i][j] = 0;
                        }
                    }
                }
            }

            fadetimer = millis();
        }
    }

    private void drawOutput() {
        pushMatrix();
        translate(300, 30);

        for (int i = 0; i < PANZER; i++) {
            for (int j = 0; j < CHANNELS; j++) {
                fill(outputValues[i][j]);
                stroke(outputValues[i][j]);
                rect(i * 40 + j * 6, 0, 6, 32);

            }
            fill(255);
            text(outputValues[i][0], i * 40, 32);
        }

        popMatrix();
    }

    private void calculateJitters() {
        if (millis() - jittertimer > jitterdelay) {
            for (int i = 0; i < PANZER; i++) {
                for (int j = 0; j < CHANNELS; j++) {
                    jitters[i][j] = random(-jitter, jitter);
                }
            }
            jittertimer = millis();
        }
    }

    private void renderEffect() {

        if (selectedEffect == 0) {   // AMP

            for (int i = 0; i < PANZER; i++) {
                float val = ampValue * ampFactor;

                setValues[i][0] = (int)(255 * (minVal + val + (val * jitters[i][0])));
                setValues[i][1] = (int)(255 * (minVal + val + (val * jitters[i][1])));
                setValues[i][2] = (int)(255 * (minVal + val + (val * jitters[i][2])));
                setValues[i][3] = (int)(255 * (minVal + val + (val * jitters[i][3])));
                setValues[i][4] = (int)(255 * (minVal + val + (val * jitters[i][4])));

                for (int j = 0; j < CHANNELS; j++) {
                    if (setValues[i][j] > 255) { setValues[i][j] = 255; }
                    if (setValues[i][j] < 0) { setValues[i][j] = 0; }
                }
            }
        }
        else if (selectedEffect == 1) {
            for (int i = 0; i < PANZER; i++) {
                alle(i, i == chaserStep ? (int) (maxVal * 255) : (int) (minVal * 255));
            }
            text("chaser: " + chaserStep, 0, 0);
        }
        else if (selectedEffect == 2) {

        }
    }

    private void alle(int i, int val) {
        if (val > 255) { val = 255; }
        if (val < 0) { val = 0; }
        for (int j = 0; j < CHANNELS; j++) {
            setValues[i][j] = val;
        }
    }

    public void sendPanzer() {
        for (int i = 0; i < PANZER; i++) {
            if (output[i]) {
                byte[] channels = new byte[5];
                for (int j = 0; j < CHANNELS; j++) {
                    if (blackout) {
                        channels[j] = 0;
                    }
                    else {
                        channels[j] = (byte) (outputValues[i][j]);
                    }
                }
                udp.send(channels, addresses[i], 4210);
            }
        }
    }

    public void receive(byte[] data) {
        data = subset(data, 0, data.length);
        String message = new String(data);

        if (message.startsWith("/freq")) {
            String temp = message.split(" ")[1];
            temp = temp.trim();
            temp = temp.replaceAll(",", "");
            freqValue = Float.parseFloat(temp);
        }

        if (message.startsWith("/amp")) {
            String temp = message.split(" ")[1];
            temp = temp.trim();
            temp = temp.replaceAll(",", "");
            ampValue = Float.parseFloat(temp);
        }

        if (message.startsWith("/impulse")) {
            impulse = !impulse;
            chaserStep++;
            chaserStep %= PANZER;
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
        PApplet.main("MainFrame");
    }
}
