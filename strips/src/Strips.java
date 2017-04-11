import controlP5.ControlEvent;
import controlP5.ControlP5;
import hypermedia.net.UDP;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Strips extends PApplet {

    private String addresses[] = {"192.168.80.121", "192.168.80.122", "192.168.80.123", "192.168.80.124",
            "192.168.80.125", "192.168.80.133", "192.168.80.127", "192.168.80.128",
            "192.168.80.129", "192.168.80.130", "192.168.80.131", "192.168.80.132"};

    private static int SENDDELAY = 40;
    static final int COUNT = 12;
    static final int SEGMENTS = 18;

    private float preAmp = 1.0f;
    float amp[] = new float[12];
    float ampRendered[] = new float[12];
    OscP5 oscP5;
    private ControlP5 cp5;
    private int note;
    private int bjnote;
    private int velocity;
    private int bjvelocity;
    private UDP udp;
    private boolean blackout = false;
    private float overallbrightness;
    private float redval;
    private float greenval;
    private float blueval;
    private float ampMod;
    private int outputColors[][] = new int[COUNT][170];
    private long millisDataSend;
    private long millisAudioRender;
    private List<Costume> costumes = new ArrayList<>();
    private boolean ampEnable = false;
    private boolean rgbEnable = false;
    private boolean milaEnable = false;
    private boolean milaFakeEnable = false;
    private boolean knee3Enable = false;
    private boolean trialEnable = false;
    private boolean midiEnable = true;
    private int effect110Value = -1;
    private int effect110Duration = 10;
    private int trialValue = -1;
    private int trialDuration = 10;
    private float attack = 1.0f;
    private float attackAudio = 1.0f;
    private float release = 1.0f;
    private float releaseAudio = 1.0f;
    private Symbols trialSymbol = Symbols.OFF;
    private int selectedEffect;

    enum MiLaDo {
        MI,
        LA,
        DO
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        frameRate(60);

        udp = new UDP(this, 2102);
        oscP5 = new OscP5(this, 2002);
        cp5 = new ControlP5(this);

        costumes.add(new CostumeMiklasherich(this, udp, addresses[0]));      // Anna Miklashevich
        costumes.add(new CostumeEberl(this, udp, addresses[1]));             // Katharina Eberl
        costumes.add(new CostumeHellermann(this, udp, addresses[2]));        // Ulrike Hellermann
        costumes.add(new CostumeKruppa(this, udp, addresses[3]));            // Luisa Kruppa
        costumes.add(new CostumeBilitza(this, udp, addresses[4]));           // Dominique Bilitza
        costumes.add(new CostumeKroedel(this, udp, addresses[5]));           // Johanna Krödel
        costumes.add(new CostumeBrandt(this, udp, addresses[6]));            // Patrick Brandt
        costumes.add(new CostumeStrotmann(this, udp, addresses[7]));         // Fabian Strotmann
        costumes.add(new CostumeDeutschewitz(this, udp, addresses[8]));      // Jörg Deutschewitz
        costumes.add(new CostumeHofmeister(this, udp, addresses[9]));              // Michael Hofmeister
        costumes.add(new CostumeWalter(this, udp, addresses[10]));           // Christian Walter
        costumes.add(new CostumePopken(this, udp, addresses[11]));           // Julian Popken

        for (int i = 0; i < COUNT; i++) {
            //output[i] = false;
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(false).setLabel("Pa " + (i + 1));
            for (int j = 0; j < 170; j++) {
                outputColors[i][j] = 0;
            }
        }

        int y = 10;
        cp5.addSlider("overallbrightness").setPosition(10, y).setSize(100, 20).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("preAmp").setPosition(10, y += 25).setSize(100, 20).setRange(0, 100.0f);
        cp5.addSlider("redval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("attack").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        cp5.addSlider("release").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        cp5.addSlider("ampMod").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(0.0f);
        cp5.addSlider("effect110Duration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);
        cp5.addSlider("trialDuration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);
        cp5.addSlider("attackAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        cp5.addSlider("releaseAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(false).setLabel("BO");
        cp5.addToggle("midiEnable").setPosition(210, 70).setSize(30, 15).setLabel("MIDI");

        int x = 0;
        cp5.addToggle("rgbEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("RGB");
        cp5.addToggle("ampEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("AMP");
//        cp5.addToggle("milaEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("MILA");
//        cp5.addToggle("milaFakeEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("MILAFK");
//        cp5.addToggle("knee3Enable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("KNEE3");
//        cp5.addToggle("trialEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("TRIAL");
//        cp5.addToggle("opt3").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("OPT3");
        cp5.addRadioButton("effectRadio").setPosition(450, 150).setSize(30, 15).setColorForeground(color(120))
                .setColorActive(color(255)).setColorLabel(color(255)).setItemsPerRow(10).setSpacingColumn(30)
                .addItem("MILA1", 1)
                .addItem("MILA2", 2)
                .addItem("MILA3", 3)
                .addItem("MILA4", 4)
                .addItem("TRIAL", 5)
                .addItem("KNEE3", 6)
        ;


        x = 0;
        cp5.addBang("setBlack").setPosition(300 + x++ * 40, 190).setSize(30, 30).setLabel("BLK");
        cp5.addBang("flash110").setPosition(300 + x++ * 40, 190).setSize(30, 30).setLabel("F110");

        surface.setTitle("STRIP CONTROLLER");
    }

    public void flash110() {
        int color = color(255);
        costumes.forEach(costume -> costume.effect110cmLine(color));
        effect110Value = effect110Duration;
    }

    private void trial(Symbols symbol) {
        int color = color(255);
        //color = color(255, 196,135);  // warmweiß
        for (Costume costume : costumes) {
            costume.effectSingleColor(0);
            costume.effectSymbol(symbol, color);
        }
        trialSymbol = symbol;
        trialValue = trialDuration;
    }

    public void setBlack() {
        for (Costume costume : costumes) {
            costume.effectSingleColor(0);
        }
    }

//    public void milaEnable(boolean en) {
//        if (this.milaEnable && !en) {
//            for (Costume costume : costumes) {
//                costume.effectSingleColor(0);
//            }
//        }
//        this.milaEnable = en;
//    }

    public void blackout(boolean bo) {
        if (this.blackout != bo) {
            costumes.forEach(costume -> costume.blackout(bo));
        }
        this.blackout = bo;
    }

    public void draw() {
        background(0);

        fadeAudio();

        // draw amp inputs
        stroke(50);

        for (int i = 0; i < COUNT; i++) {
            fill(255);
            text(String.format("%.2f", amp[i]), 300 + i * 40, 20);
            fill(amp[i] * 255);
            rect(300 + i * 40, 32, 30, 15);
            fill(ampRendered[i] * 255);
            rect(300 + i * 40, 32 + 15, 30, 15);
        }

        stroke(20);

        // draw input midi notes
//        fill(Color.HSBtoRGB(map(note, 0, 127, 0.0f, 1.0f), 0.75f, 0.7f));
        fill(Color.HSBtoRGB(map(note % 12, 0, 12, 0.0f, 1.0f), 0.75f, 0.7f));
        rect(220, 10, 20, 20);
        fill(Color.HSBtoRGB(map(bjnote, 0, 127, 0.0f, 1.0f), 0.75f, 0.7f));
        rect(245, 10, 20, 20);

        renderEffect();

        // draw costumes
        int i = 0;
        for (Costume costume : costumes) {
            costume.render();

            pushMatrix();
            translate(10 + i * 65, 400);
            costume.display();
            popMatrix();
            i++;
        }

        // send data via UDP
        sendOutputs();
    }

    private void fadeAudio() {
        if (attackAudio >= 0.99f && releaseAudio >= 0.99f) {
            // direct switching
            for (int ch = 0; ch < COUNT; ch++) {
                ampRendered[ch] = amp[ch];
            }
        } else if (millis() - millisAudioRender > 10) {
            for (int ch = 0; ch < COUNT; ch++) {

                float diff = ampRendered[ch] - amp[ch];

                if (Math.abs(diff) > 0.01) {
                    if (diff > 0) {
                        ampRendered[ch] -= diff * releaseAudio;
                        if (ampRendered[ch] > 1.0f) {
                            ampRendered[ch] = 1.0f;
                        }
                    } else {
                        ampRendered[ch] -= diff * attackAudio;
                        if (ampRendered[ch] < 0.0f) {
                            ampRendered[ch] = 0.0f;
                        }
                    }
                }
            }

            millisAudioRender = millis();
        }
    }

    private void sendOutputs() {
        if (System.currentTimeMillis() - millisDataSend < SENDDELAY) {
            return;
        }

        millisDataSend = System.currentTimeMillis();

        for (Costume costume : costumes) {
            costume.send();
        }
    }

    private void renderEffect() {

        if (ampEnable) {

            for (int i = 0; i < costumes.size(); i++) {
                float a = ampRendered[i];
                Costume costume = costumes.get(i);

                float brightness = 1 - ampMod;
                brightness = brightness + a * ampMod;
                costume.brightness(brightness * overallbrightness);
            }

            //costumes.get(channel - 1).brightness(bright * overallbrightness);

        }

        //if (selectedEffect == 0) {  //amp
//            for (int costume = 0; costume < COUNT; costume++) {
//                costumes[costume].effectSingleColor(color(255 * amp[costume]));
//            }
        if (rgbEnable) { // RGB demo fade
            int color = color(255 * redval, 255 * greenval, 255 * blueval);
            costumes.forEach(e -> e.effectSingleColor(color));
        }

        if (effect110Value > 0) {
            effect110Value--;
            int color = color(255 * ((float) effect110Value / (float) effect110Duration));
            costumes.forEach(costume -> costume.effect110cmLine(color));
        } else if (effect110Value == 0) {
            effect110Value--;
            int color = color(0);
            costumes.forEach(costume -> costume.effect110cmLine(color));
        }

        if (trialValue > 0) {
            trialValue--;
            int color = color(255 * ((float) trialValue / (float) trialDuration));
            costumes.forEach(costume -> costume.effectSymbol(trialSymbol, color));
        } else if (trialValue == 0) {
            trialValue--;
            int color = color(0);
            costumes.forEach(costume -> costume.effectSymbol(trialSymbol, color));
        }


    }

    public void keyPressed() {
        if (selectedEffect == 1) {
            if (key == 'm') {
                for (Costume costume : costumes) {
                    costume.effectMI();
                }
            } else if (key == 'l') {
                for (Costume costume : costumes) {
                    costume.effectLA();
                }
            } else if (key == 'd') {
                for (Costume costume : costumes) {
                    costume.effectDO();
                }
            }
        }

        if (selectedEffect == 6) {  // Knee3
            if (key == '4') {
                knee3(4);
            } else if (key == '3') {
                knee3(3);
            } else if (key == '2') {
                knee3(2);
            }
        } else if (selectedEffect == 5) { // trial
            switch (key) {
                case '1':
                    trial(Symbols.RIGHT);
                    break;
                case '2':
                    trial(Symbols.LEFT);
                    break;
                case '3':
                    trial(Symbols.BACKSLASH);
                    break;
                case '4':
                    trial(Symbols.SLASH);
                    break;
                case '5':
                    trial(Symbols.MINUS);
                    break;
                case '6':
                    trial(Symbols.SUSPENDERS);
                    break;
                case '7':
                    trial(Symbols.X);
                    break;
                case '8':
                    trial(Symbols.UX);
                    break;
                case '9':
                    trial(Symbols.OFF);
                    break;
            }
        }
    }

    private void knee3(int num) {

        for (Costume costume : costumes) {
            costume.effectSingleColor(0);
        }

        int color;

        if (num == 2) {
            color = color(0, 0, 255);
            costumes.get(1).effectSymbol(Symbols.BACKSLASH, color);
            costumes.get(2).effectSymbol(Symbols.BACKSLASH, color);
            costumes.get(3).effectSymbol(Symbols.BACKSLASH, color);
            costumes.get(4).effectSymbol(Symbols.BACKSLASH, color);
        } else if (num == 3) {
            color = color(255, 0, 0);
            costumes.get(6).effectSymbol(Symbols.SLASH, color);
            costumes.get(0).effectSymbol(Symbols.SLASH, color);
            costumes.get(5).effectSymbol(Symbols.SLASH, color);
            costumes.get(9).effectSymbol(Symbols.SLASH, color);
        } else if (num == 4) {
            color = color(255, 255, 0);
            costumes.get(7).effectSymbol(Symbols.X, color);
            costumes.get(8).effectSymbol(Symbols.X, color);
            costumes.get(10).effectSymbol(Symbols.X, color);
            costumes.get(11).effectSymbol(Symbols.X, color);
        }
    }

    private void mi(int milaNumber) {
        milaNumber %= 4;
        if(milaNumber == 1) {
            // Alt          MI MI
            // Sopran       MI LA
        }

    }
    private void la(int milaNumber) {
        milaNumber %= 4;

    }
    private void doo(int milaNumber) {
        milaNumber %= 4;

    }

    private void alt(MiLaDo milado) {
        if(milado == MiLaDo.MI) {
            costumes.get(3).effectMI();
            costumes.get(4).effectMI();
            costumes.get(5).effectMI();
        } else if(milado == MiLaDo.LA) {
            costumes.get(3).effectLA();
            costumes.get(4).effectLA();
            costumes.get(5).effectLA();
        } else if(milado == MiLaDo.DO) {
            costumes.get(3).effectDO();
            costumes.get(4).effectDO();
            costumes.get(5).effectDO();
        }
    }


    void oscEvent(OscMessage msg) {

        String addr = msg.addrPattern();

        if (addr.startsWith("amp")) {
            int channel = Integer.parseInt(addr.substring(3));

            float newAmpValue = msg.get(0).floatValue() * preAmp;
            amp[channel - 1] = newAmpValue;
        } else if (addr.startsWith("freq")) {
            int channel = Integer.parseInt(addr.substring(4));
            float m = msg.get(0).floatValue();

//            System.out.println("freq " + channel + "  f:" + m);

            if (channel == 10) {    // Michael Hofmeister
                if (selectedEffect == 2) {  // mi la do la  MILA2
                    if (m == 74) { // do
                        costumes.forEach(Costume::effectDO);
                    } else if (m == 53) { // la
                        costumes.forEach(Costume::effectLA);
                    }
                }
            }

        } else if (msg.checkAddrPattern("/midinote") && midiEnable) {
            note = msg.get(0).intValue();
            //velocity = msg.get(1).intValue();
            int noteModulo = note % 12;
            int octave = note / 12;

            System.out.println("midinote " + note + " modnote: " + noteModulo + "oct: " + octave + " vel: " + velocity);


            if (selectedEffect == 0 && octave == 4) {  // mi la do la  MILA1
                if (noteModulo == 9) { // la
                    costumes.forEach(Costume::effectLA);
                } else if (noteModulo == 4) { // mi
                    costumes.forEach(Costume::effectMI);
//                    } else if (noteModulo == 0) { // do
//                        costumes.forEach(Costume::effectDO);
                }

            }

        } else if (msg.checkAddrPattern("/bjmidi") && midiEnable) {
            bjnote = msg.get(0).intValue();
            bjvelocity = msg.get(1).intValue();
            System.out.println("bjmidi " + bjnote);

            if (bjnote == 33 && bjvelocity != 0) {
                flash110();
            }

            if (selectedEffect == 6 && bjvelocity != 0) {
                switch (bjnote) {
                    case 25:
                    case 28:
                    case 31:
                        // 4
                        knee3(4);
                        break;
                    case 26:
                    case 29:
                    case 32:
                        // 3
                        knee3(3);
                        break;
                    case 27:
                    case 30:
                        // 2
                        knee3(2);
                        break;
                }

            }

            if (trialEnable && bjvelocity != 0) {
                switch (bjnote) {
                    case 73:
                        trial(Symbols.RIGHT);
                        break;
                    case 74:
                        trial(Symbols.LEFT);
                        break;
                    case 75:
                        trial(Symbols.BACKSLASH);
                        break;
                    case 76:
                        trial(Symbols.SLASH);
                        break;
                    case 89:
                        trial(Symbols.MINUS);
                        break;
                    case 90:
                        trial(Symbols.SUSPENDERS);
                        break;
                    case 91:
                        trial(Symbols.X);
                        break;
                    case 92:
                        trial(Symbols.UX);
                        break;
                    default:
                        trial(Symbols.OFF);
                        break;
                }
            }
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("output")) {
                int id = theEvent.getId();
                if (id >= 0 && id < COUNT) {
                    //output[id] = theEvent.getValue() > 0;
                    costumes.get(id).setEnabled(theEvent.getValue() > 0);
                }
            } else if (theEvent.getName().startsWith("attack")) {
                for (Costume costume : costumes) {
                    costume.attack(theEvent.getValue());
                }
            } else if (theEvent.getName().startsWith("release")) {
                for (Costume costume : costumes) {
                    costume.release(theEvent.getValue());
                }
            } else if (theEvent.getName().startsWith("overallbrightness")) {

                costumes.forEach(costume -> costume.brightness(theEvent.getValue()));

            }
        }


        if (theEvent.getName().startsWith("effectRadio")) {
            selectedEffect = (int) theEvent.getValue();
            System.out.println("effect: " + selectedEffect);

        }
    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }
}
