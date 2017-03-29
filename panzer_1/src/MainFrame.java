import controlP5.ControlEvent;
import controlP5.ControlP5;
import hypermedia.net.UDP;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class MainFrame extends PApplet {

    private String addresses[] = {"192.168.80.101", "192.168.80.102", "192.168.80.103", "192.168.80.104",
            "192.168.80.105", "192.168.80.106", "192.168.80.107", "192.168.80.108",
            "192.168.80.109", "192.168.80.110", "192.168.80.111", "192.168.80.112"};

    private int COUNT = 12;
    private int CHANNELS = 5;
    private UDP udp;
    private int outputValues[][] = new int[COUNT][CHANNELS];
    private int setValues[][] = new int[COUNT][CHANNELS];
    private ControlP5 cp5;
    private float ampFactor = 1.0f;
    private float amp[] = new float[12];
    private float freqValue = 0.0f;
    private float minVal = 0.0f;
    private float maxVal = 0.0f;
    private boolean impulse = false;
    private boolean output[] = new boolean[COUNT];
    private int selectedEffect;
    private int chaserStep = 0;
    private boolean blackout = false;
    private OscP5 oscP5;
    private int note;


    private float jitter = 0.0f;
    private float jitters[][] = new float[COUNT][CHANNELS];
    private long jittertimer;
    private int jitterdelay;
    private int fade = 0;
    private int fade2 = 80;
    private long fadetimer;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        udp = new UDP(this, 2101);
        udp.listen(true);
        oscP5 = new OscP5(this,2001);
        cp5 = new ControlP5(this);

        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < CHANNELS; j++) {
                outputValues[i][j] = 0;
            }
            output[i] = false;
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(output[i]).setLabel("Pa " + (i + 1));
        }

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(true).setLabel("BO");

        cp5.addSlider("ampFactor").setPosition(10, 10).setSize(100, 20).setRange(0, 100.0f);
        cp5.addSlider("jitter").setPosition(10, 35).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("jitterdelay").setPosition(10, 60).setSize(100, 20).setRange(10, 1000);
        cp5.addSlider("minVal").setPosition(10, 85).setSize(100, 20).setRange(0, 1.0f).setValue(0.0f);
        cp5.addSlider("maxVal").setPosition(10, 110).setSize(100, 20).setRange(0, 1.0f).setValue(1.0f);
        cp5.addSlider("fade").setPosition(10, 135).setSize(100, 20).setRange(0, 80).setValue(0);
        cp5.addSlider("fade2").setPosition(10, 160).setSize(100, 20).setRange(0, 80).setValue(80);

        cp5.addBang("bang").setPosition(250, 120).setSize(20, 20).plugTo(this, "impulse");

        cp5.addScrollableList("effectList").setPosition(300, 120)
                .setBarHeight(20).setItemHeight(20).setLabel("effect").close()
                .addItem("amp", 0).addItem("chaser", 1);

        frameRate(30);
        surface.setTitle("Panzer");
    }

    public void impulse() {
        this.impulse = !this.impulse;
        this.chaserStep++;
        this.chaserStep %= COUNT;
    }

    public void draw() {
        background(0);

        fill(255);

        text("freq: " + freqValue, 650, 10);

        drawInputAmps();

        stroke(impulse ? 255 : 0);
        fill(impulse ? 255 : 0);
        rect(0, 0, 5, 5);

        renderEffect();
        calculateJitters();

        transformSetToOutput();

        drawOutput();
        sendPanzer();
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

    private void transformSetToOutput() {
        if (fade == 0) {
            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < CHANNELS; j++) {
                    outputValues[i][j] = setValues[i][j];

                    if (outputValues[i][j] > 255) {
                        outputValues[i][j] = 255;
                    } else if (outputValues[i][j] < 0) {
                        outputValues[i][j] = 0;
                    }
                }
            }
        } else if (millis() - fadetimer > 10) {

            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < CHANNELS; j++) {

                    if (setValues[i][j] != outputValues[i][j]) {

                        if (setValues[i][j] > outputValues[i][j]) {
                            outputValues[i][j] += fade;
                            if (outputValues[i][j] > 255) {
                                outputValues[i][j] = 255;
                            }
                        } else if (setValues[i][j] < outputValues[i][j]) {
                            outputValues[i][j] -= fade2;
                            if (outputValues[i][j] < 0) {
                                outputValues[i][j] = 0;
                            }
                        }

                        int diff = outputValues[i][j] - setValues[i][j];
                        if(diff < 0) {
                            if (abs(diff) < fade2) {
                                outputValues[i][j] -= diff;
                            }
                        } else {
                            if (abs(diff) < fade) {
                                outputValues[i][j] -= diff;
                            }
                        }

                        if (outputValues[i][j] > 255) {
                            outputValues[i][j] = 255;
                        } else if (outputValues[i][j] < 0) {
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
        translate(300, 150);

        for (int i = 0; i < COUNT; i++) {
            for (int j = 0; j < CHANNELS; j++) {
                fill(outputValues[i][j]);
                stroke(outputValues[i][j]);
                rect(i * 40 + j * 6, 0, 6, 30);

            }
            stroke(50);
            noFill();
            rect(i* 40, 0, 30, 30);
        }

        popMatrix();
    }

    private void calculateJitters() {
        if (millis() - jittertimer > jitterdelay) {
            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < CHANNELS; j++) {
                    jitters[i][j] = random(-jitter, jitter);
                }
            }
            jittertimer = millis();
        }
    }

    private void renderEffect() {

        if (selectedEffect == 0) {   // AMP
            for (int i = 0; i < COUNT; i++) {
                for (int j = 0; j < CHANNELS; j++) {
                    setValues[i][j] = min((int) (maxVal * 255), (int) (255 * (minVal + amp[i] + (amp[i] * jitters[i][j]))));
                    if (setValues[i][j] > 255) {
                        setValues[i][j] = 255;
                    }
                    if (setValues[i][j] < 0) {
                        setValues[i][j] = 0;
                    }
                }
            }
        } else if (selectedEffect == 1) {
            for (int i = 0; i < COUNT; i++) {
                int val = i == chaserStep ? (int) (maxVal * 255) : (int) (minVal * 255);

                for (int j = 0; j < CHANNELS; j++) {

                    setValues[i][j] = (int) (val + val * jitters[i][j] * amp[i]);

                    if (setValues[i][j] > 255) {
                        setValues[i][j] = 255;
                    }
                    if (setValues[i][j] < 0) {
                        setValues[i][j] = 0;
                    }
                }

            }
        } else if (selectedEffect == 2) {

        }
    }

    private void alle(int i, int val) {
        if (val > 255) {
            val = 255;
        }
        if (val < 0) {
            val = 0;
        }
        for (int j = 0; j < CHANNELS; j++) {
            setValues[i][j] = val;
        }
    }

    public void sendPanzer() {
        for (int i = 0; i < COUNT; i++) {
            if (output[i]) {
                byte[] buffer = new byte[CHANNELS];
                for (int j = 0; j < CHANNELS; j++) {
                    if (blackout) {
                        buffer[j] = 0;
                    }
                    else {
                        buffer[j] = (byte) (outputValues[i][j]);
                    }
                }
                udp.send(buffer, addresses[i], 4210);
            }
        }
    }

    void oscEvent(OscMessage msg) {

        if(msg.checkAddrPattern("/freq") && msg.checkTypetag("f")) {
            freqValue = msg.get(0).floatValue();
        } else if(msg.checkAddrPattern("/impulse") ) {
            impulse = !impulse;
            chaserStep++;
            chaserStep %= COUNT;
        } else if(msg.checkAddrPattern("/zahl")) {
            chaserStep=msg.get(0).intValue() - 1;
            chaserStep %= COUNT;
        }

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
            } else if (theEvent.getName().startsWith("effectList")) {
                selectedEffect = (int) theEvent.getValue();
            }
        }
    }


    public static void main(String args[]) {
        PApplet.main("MainFrame");
    }
}
