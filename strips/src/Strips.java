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

    private final static int TRIAL1 = 1;
    private final static int TRIAL2 = 2;
    private final static int TRIAL3 = 3;
    private final static int TRIAL4 = 4;
    private final static int KNEE3 = 5;
    private final static int TRIALPRI = 6;
    private final static int TESTMODE = 7;
    private static final String ORGAN2 = "/midi/MIDISPORT_2x2_Anniv_MIDI_1/0";
    private static final String ORGAN1 = "/midi/Kurzweil_Forte_MIDI_1/5";

    private String addresses[] = {"192.168.80.121", "192.168.80.122", "192.168.80.123", "192.168.80.124",
            "192.168.80.125", "192.168.80.126", "192.168.80.127", "192.168.80.128",
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
    private List<Costume> costumesAlt = new ArrayList<>();
    private List<Costume> costumesSopran = new ArrayList<>();
    private List<Costume> costumesBass = new ArrayList<>();
    private List<Costume> costumesTenor = new ArrayList<>();
    private boolean ampEnable = false;
    private boolean rgbEnable = false;
    private boolean midiEnable = true;
    private int effect110Value = -1;
    private int effect110Duration = 10;
    private int trialValue = -1;
    private int trialDuration = 10;
    private int knee3Value = -1;
    private int knee3Duration = 10;
    private float attackAudio = 1.0f;
    private float releaseAudio = 1.0f;
    private Symbols trialSymbol = Symbols.OFF;
    private Symbols knee3Symbol = Symbols.OFF;
    private int selectedEffect;
    private Costume anna;
    private Costume katharina;
    private Costume ulrike;
    private Costume luisa;
    private Costume dominique;
    private Costume johanna;
    private Costume patrick;
    private Costume fabian;
    private Costume joerg;
    private Costume michael;
    private Costume christian;
    private Costume julian;
    int bang = 0;

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

        anna = new CostumeAnna(this, udp, addresses[0]);                // Anna Miklashevich
        katharina = new CostumeKatharina(this, udp, addresses[1]);      // Katharina Eberl
        ulrike = new CostumeUlrike(this, udp, addresses[2]);            // Ulrike Hellermann
        luisa = new CostumeLuisa(this, udp, addresses[3]);              // Luisa Kruppa
        dominique = new CostumeDominique(this, udp, addresses[4]);      // Dominique Bilitza
        johanna = new CostumeJohanna(this, udp, addresses[5]);          // Johanna Krödel
        patrick = new CostumePatrick(this, udp, addresses[6]);          // Patrick Brandt
        fabian = new CostumeFabian(this, udp, addresses[7]);            // Fabian Strotmann
        joerg = new CostumeJoerg(this, udp, addresses[8]);              // Jörg Deutschewitz
        michael = new CostumeMichael(this, udp, addresses[9]);          // Michael Hofmeister
        christian = new CostumeChristian(this, udp, addresses[10]);     // Christian Walter
        julian = new CostumeJulian(this, udp, addresses[11]);           // Julian Popken

        costumes.add(anna); costumes.add(katharina); costumes.add(ulrike);
        costumes.add(luisa); costumes.add(dominique); costumes.add(johanna);
        costumes.add(patrick); costumes.add(fabian); costumes.add(joerg);
        costumes.add(michael); costumes.add(christian); costumes.add(julian);

        costumesSopran.add(anna); costumesSopran.add(katharina); costumesSopran.add(ulrike);
        costumesAlt.add(luisa); costumesAlt.add(dominique); costumesAlt.add(johanna);
        costumesTenor.add(patrick); costumesTenor.add(fabian); costumesTenor.add(joerg);
        costumesBass.add(michael); costumesBass.add(christian); costumesBass.add(julian);


        for (int i = 0; i < COUNT; i++) {
            cp5.addToggle("output" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(false).setLabel("Pa " + (i + 1));
            for (int j = 0; j < 170; j++) {
                outputColors[i][j] = 0;
            }
        }

        int y = 10;
        cp5.addSlider("overallbrightness").setPosition(10, y).setSize(100, 20).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("preAmp").setPosition(10, y += 25).setSize(100, 20).setRange(0, 100.0f);
        y+=10;
        cp5.addSlider("attackAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        cp5.addSlider("releaseAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        y+=10;
        cp5.addSlider("redval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        y+=10;
        cp5.addSlider("ampMod").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(0.0f);
        y+=10;
        cp5.addSlider("effect110Duration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);
        cp5.addSlider("trialDuration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);
        cp5.addSlider("knee3Duration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(false).setLabel("BO");
        cp5.addToggle("midiEnable").setPosition(210, 70).setSize(30, 15).setLabel("MIDI");

        int x = 0;
        cp5.addToggle("rgbEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("RGB");
        cp5.addToggle("ampEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("AMP");
        cp5.addRadioButton("effectRadio").setPosition(300, 250).setSize(30, 15).setColorForeground(color(120))
                .setColorActive(color(255)).setColorLabel(color(255)).setItemsPerRow(10).setSpacingColumn(30)
                .addItem("TEST", TESTMODE)
                .addItem("TRIAL1", TRIAL1)
                .addItem("TRIAL2", TRIAL2)
                .addItem("TRIAL3", TRIAL3)
                .addItem("TRIAL4", TRIAL4)
                .addItem("KNEE3", KNEE3)
                .addItem("TRIALPRI", TRIALPRI)
                .deactivateAll();

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
        costumes.forEach(Costume::black);
    }

    public void blackout(boolean bo) {
        if (this.blackout != bo) {
            setBlack();
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

        fill(255);
        text("bang: " + bang, 210, 50);
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

        if (knee3Value > 0) {
            knee3Value--;
            int color = color(255 * ((float) knee3Value / (float) knee3Duration));
            costumes.forEach(costume -> costume.effectSymbol(knee3Symbol, color));
        } else if (trialValue == 0) {
            knee3Value--;
            int color = color(0);
            costumes.forEach(costume -> costume.effectSymbol(knee3Symbol, color));
        }


    }

    public void keyPressed() {
        if(selectedEffect == TESTMODE && key == '1') {
            bang++;
            bang %= SEGMENTS;
            for (Costume costume: costumes) {
                costume.black();
                costume.setSegmentColor(bang, color(255));
            }
        }

        if (selectedEffect == TRIAL1 || selectedEffect == TRIAL2 || selectedEffect == TRIAL3 || selectedEffect == TRIAL4) {
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

        if (selectedEffect == KNEE3) {  // Knee3
            if (key == '4') {
                knee3(4);
            } else if (key == '3') {
                knee3(3);
            } else if (key == '2') {
                knee3(2);
            } else if (key == '1') {
                knee3(1);
            } else {
                setBlack();
            }
        } else if (selectedEffect == TRIALPRI) { // trial/prison
            switch (key) {
                case '1': trial(Symbols.RIGHT); break;
                case '2': trial(Symbols.LEFT); break;
                case '3': trial(Symbols.BACKSLASH); break;
                case '4': trial(Symbols.SLASH); break;
                case '5': trial(Symbols.MINUS); break;
                case '6': trial(Symbols.SUSPENDERS); break;
                case '7': trial(Symbols.X); break;
                case '8': trial(Symbols.UX); break;
                case '9': trial(Symbols.OFF); break;
            }
        }
    }

    private void knee3(int num) {
        for (Costume costume : costumes) {
            costume.effectSingleColor(0);
        }

        final int color = color(255);
        if (num == 1) {
            knee3Symbol = Symbols.UX;
        } else if (num == 2) {
            knee3Symbol = Symbols.BACKSLASH;
        } else if (num == 3) {
            knee3Symbol = Symbols.SLASH;
        } else if (num == 4) {
            knee3Symbol = Symbols.X;
        }

        costumes.forEach(costume -> costume.effectSymbol(knee3Symbol, color));
        knee3Value = knee3Duration;
    }

    private void mi(int milaNumber) {
        milaNumber %= 4;
        if (milaNumber == 1) {
            costumesSopran.forEach(Costume::effectMI);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::black);
            costumesTenor.forEach(Costume::black);
        } else if (milaNumber == 2) {
            costumesAlt.forEach(Costume::black);
            costumesSopran.forEach(Costume::black);
        } else if(milaNumber == 3) {
            costumesSopran.forEach(Costume::effectMI);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::effectDO);
            costumesTenor.forEach(Costume::effectDO);
        } else if(milaNumber == 4) {
            costumesSopran.forEach(Costume::effectMI);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::effectDO);
            costumesTenor.forEach(Costume::effectDO);
        }
    }

    private void la(int milaNumber) {
        milaNumber %= 4;
        if (milaNumber == 1) {
            costumesSopran.forEach(Costume::effectLA);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::black);
            costumesTenor.forEach(Costume::black);
        } else if (milaNumber == 2) {
            costumesBass.forEach(Costume::effectLA);
            costumesTenor.forEach(Costume::effectLA);
        } else if(milaNumber == 3) {
            costumesSopran.forEach(Costume::effectLA);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::effectLA);
            costumesTenor.forEach(Costume::effectLA);
        } else if(milaNumber == 4) {
            costumesSopran.forEach(Costume::effectLA);
            costumesAlt.forEach(Costume::effectMI);
            costumesBass.forEach(Costume::effectLA);
            costumesTenor.forEach(Costume::effectLA);
        }
    }

    private void doo(int milaNumber) {
        milaNumber %= 4;
        if (milaNumber == 2) {
            costumesBass.forEach(Costume::effectDO);
            costumesTenor.forEach(Costume::effectDO);
        } else if(milaNumber == 3) {

        } else if(milaNumber == 4) {
            costumesSopran.forEach(Costume::effectDO);
            costumesAlt.forEach(Costume::effectLA);
            costumesBass.forEach(Costume::black);
            costumesTenor.forEach(Costume::black);
        }
    }


    private void oscOrgel(OscMessage msg) {

        if (!msg.checkAddrPattern(ORGAN1)) {
            return;
        }

        if ("note_on".equals(msg.get(0).stringValue())) {
            note = msg.get(1).intValue();
            velocity = msg.get(2).intValue();
            int noteModulo = note % 12;
            int octave = note / 12;

            System.out.println("midinote " + note + " modnote: " + noteModulo + " oct: " + octave + " vel: " + velocity);

            if (selectedEffect == TRIAL1) {  // mi la do la  MILA1
                if (noteModulo == 4 && octave == 4) {
                    mi(selectedEffect);
                } else if (noteModulo == 9 && octave == 4) {
                    la(selectedEffect);
                }
            } else if(selectedEffect == TRIAL2) {
                // gesteuert von Bjarne oder via Tonerkennung
            } else if(selectedEffect == TRIAL3) {
                if (noteModulo == 4 && octave == 4) {
                    mi(selectedEffect);
                } else if (noteModulo == 9 && octave == 4) {
                    la(selectedEffect);
                }
            } else if(selectedEffect == TRIAL4) {
                if (noteModulo == 4 && octave == 4) {
                    mi(selectedEffect);
                } else if (noteModulo == 9 && octave == 4) {
                    la(selectedEffect);
                } else if(noteModulo == 10 && octave == 5) {   // TODO note finden
                    doo(selectedEffect);
                }
            }
        }
    }

    private void oscBjarne(OscMessage msg) {
        bjnote = msg.get(0).intValue();
        bjvelocity = msg.get(1).intValue();
//        System.out.println("bjmidi " + bjnote + " vel" + bjvelocity);
        if (bjvelocity != 0) return;

        if (bjnote == 33) {
            flash110();
        } else if (bjnote == 34) {
            setBlack();
        }

        if (selectedEffect == KNEE3) {
            switch (bjnote) {
                case 80: knee3(4); break;
                case 81: knee3(3); break;
                case 82: knee3(2); break;
                case 83: knee3(1); break;
            }
        }

        if (selectedEffect == TRIALPRI) {
            switch (bjnote) {
                case 73: trial(Symbols.RIGHT); break;
                case 74: trial(Symbols.LEFT); break;
                case 75: trial(Symbols.BACKSLASH); break;
                case 76: trial(Symbols.SLASH); break;
                case 89: trial(Symbols.MINUS); break;
                case 90: trial(Symbols.SUSPENDERS); break;
                case 91: trial(Symbols.X); break;
                case 92: trial(Symbols.UX); break;
                default: trial(Symbols.OFF); break;
            }
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
                if (selectedEffect == TRIAL2) {  // mi la do la  MILA2
                    System.out.println("hofmeister: " + m);

                    if (m <= 52) { // do    // gesang war 47-48
                        doo(selectedEffect);
                    } else if (m > 52) { // la   // gesang war 56-57
                        la(selectedEffect);
                    }
                }
            }

        } else if ((msg.checkAddrPattern(ORGAN1) || msg.checkAddrPattern(ORGAN2)) && midiEnable) {
            oscOrgel(msg);
        } else if (msg.checkAddrPattern("/bjmidi") && midiEnable) {
            oscBjarne(msg);
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("output")) {
                int id = theEvent.getId();
                if (id >= 0 && id < COUNT && costumes.get(id) != null) {
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
            setBlack();
        }
    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }
}
