import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.RadioButton;
import controlP5.Slider;
import hypermedia.net.UDP;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends PApplet {

    private final static int TRIAL1 = 1;
    private final static int TRIAL2 = 2;
    private final static int TRIAL3 = 3;
    private final static int KNEE3 = 5;
    private final static int TRIALPRI = 6;
    private final static int TESTMODE = 7;
    private final static int KNEE3ON = 8;
    private final static int DANCE2 = 9;
    private final static int KNEE4 = 10;
    private final static int BUILDING = 11;
    private static final String ORGAN2 = "/midi/MIDISPORT_2x2_Anniv_MIDI_1/0";
    private static final String ORGAN1 = "/midi/MIDISPORT_2x2_Anniv_MIDI_2/5";

    private String addresses[] = {"192.168.80.121", "192.168.80.122", "192.168.80.123", "192.168.80.124",
            "192.168.80.125", "192.168.80.126", "192.168.80.127", "192.168.80.128",
            "192.168.80.129", "192.168.80.130", "192.168.80.131", "192.168.80.132"};

    private String addressesPanzer[] = {"192.168.80.101", "192.168.80.102", "192.168.80.103", "192.168.80.104",
            "192.168.80.105", "192.168.80.106", "192.168.80.107", "192.168.80.108",
            "192.168.80.109", "192.168.80.110", "192.168.80.111", "192.168.80.112"};

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
    private int effectFadeValue = -1;
    private int effectFadeDuration = 10;
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
    private MidiBus midi;
    private Slider sldrOverallBrightness;
    private Slider sldrAttackAudio;
    private Slider sldrReleaseAudio;
    private Slider sldrAmpMod;
    private Slider sldrEffectDuration;
    private RadioButton effectRadio;
    private boolean midiDebug;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        frameRate(60);

        MidiBus.list();

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
        sldrOverallBrightness = cp5.addSlider("overallbrightness").setPosition(10, y).setSize(100, 20).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("preAmp").setPosition(10, y += 25).setSize(100, 20).setRange(0, 100.0f);
        y += 10;
        sldrAttackAudio = cp5.addSlider("attackAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        sldrReleaseAudio = cp5.addSlider("releaseAudio").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(1.0f);
        y += 10;
        cp5.addSlider("redval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, y += 25).setSize(100, 20).setRange(0, 1.0f);
        y += 10;
        sldrAmpMod = cp5.addSlider("ampMod").setPosition(10, y += 25).setSize(100, 20).setRange(0.0f, 1.0f).setValue(0.0f);
        y += 10;
        cp5.addSlider("effect110Duration").setPosition(10, y += 25).setSize(100, 20).setRange(5, 100);
        sldrEffectDuration = cp5.addSlider("effectFadeDuration").setPosition(10, y += 25).setSize(100, 20).setRange(1, 200);

        cp5.addToggle("blackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(false).setLabel("BO");
        cp5.addToggle("midiEnable").setPosition(210, 70).setSize(30, 15).setLabel("MIDI");
        cp5.addToggle("midiDebug").setPosition(210, 110).setSize(30, 15).setId(12).setValue(false).setLabel("MIDI DBG");

        int x = 0;
        cp5.addToggle("rgbEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("RGB");
        cp5.addToggle("ampEnable").setPosition(300 + x++ * 40, 150).setSize(30, 15).setValue(false).setLabel("AMP");
        effectRadio = cp5.addRadioButton("effectRadio").setPosition(300, 250).setSize(30, 15).setColorForeground(color(120))
                .setColorActive(color(255)).setColorLabel(color(255)).setItemsPerRow(5).setSpacingColumn(60)
                .addItem("TEST", TESTMODE)
                .addItem("TRIAL1", TRIAL1)
                .addItem("TRIAL2", TRIAL2)
                .addItem("TRIAL3", TRIAL3)
                .addItem("DANCE2", DANCE2)
                .addItem("KNEE4", KNEE4)
                .addItem("KNEE3", KNEE3)
                .addItem("KNEE3ON", KNEE3ON)
                .addItem("TRIALPRI", TRIALPRI)
                .addItem("BUILDING", BUILDING)
                .deactivateAll();

        x = 0;
        cp5.addBang("setBlack").setPosition(300 + x++ * 40, 190).setSize(30, 30).setLabel("BLK");
        cp5.addBang("flash110").setPosition(300 + x++ * 40, 190).setSize(30, 30).setLabel("F110");


        midi = new MidiBus(this, 0, 1); // this,input,outputdev

        surface.setTitle("Costumes");
    }

    public void flash110() {
        int color = color(255);
        costumes.forEach(costume -> costume.effect110cmLine(color));
        effect110Value = effect110Duration;
    }

    private void trialPrison(Symbols symbol) {
        int color = color(255);
        //color = color(255, 196,135);  // warmweiß
        for (Costume costume : costumes) {
            costume.effectSingleColor(0);
            costume.effectSymbol(symbol, color);
        }
        trialSymbol = symbol;
        effectFadeValue = effectFadeDuration;
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


        if (effectFadeValue > 0) {
            effectFadeValue--;
            int color = color(255 * ((float) effectFadeValue / (float) effectFadeDuration));
            if (selectedEffect == TRIALPRI) {
                costumes.forEach(costume -> costume.effectSymbol(trialSymbol, color));
            } else if (selectedEffect == KNEE3) {
                costumes.forEach(costume -> costume.effectSymbol(knee3Symbol, color));
            }
        } else if (effectFadeValue == 0) {
            effectFadeValue--;
            int color = color(0);
            if (selectedEffect == TRIALPRI | selectedEffect == KNEE3) {
                costumes.forEach(costume -> costume.effectSymbol(trialSymbol, color));
            }
        }
    }

    public void keyPressed() {
        if (selectedEffect == TESTMODE && key == '1') {
            bang++;
            bang %= SEGMENTS;
            for (Costume costume : costumes) {
                costume.black();
                costume.setSegmentColor(bang, color(255));
            }
        }

        if (selectedEffect == TRIAL1 || selectedEffect == TRIAL2 || selectedEffect == TRIAL3) {
            if (key == 'm') {
                mi(selectedEffect, false);
            } else if (key == 'l') {
                la(selectedEffect, false);
            } else if (key == 'd') {
                doo(selectedEffect, false);

            }
        }

        if (selectedEffect == KNEE3 || selectedEffect == KNEE3ON) {  // Knee3
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
        } else if (selectedEffect == TRIALPRI) { // trialPrison/prison
            switch (key) {
                case '1': trialPrison(Symbols.RIGHT); break;
                case '2': trialPrison(Symbols.LEFT); break;
                case '3': trialPrison(Symbols.BACKSLASH); break;
                case '4': trialPrison(Symbols.SLASH); break;
                case '5': trialPrison(Symbols.MINUS); break;
                case '6': trialPrison(Symbols.SUSPENDERS); break;
                case '7': trialPrison(Symbols.X); break;
                case '8': trialPrison(Symbols.UX); break;
                case '9': trialPrison(Symbols.OFF); break;
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
        effectFadeValue = effectFadeDuration;
    }

    private void mi(int milaNumber, boolean off) {
        milaNumber %= 4;
        if (milaNumber == TRIAL1) {
            costumesSopran.forEach(off ? Costume::black : Costume::effectMI);
            costumesAlt.forEach(off ? Costume::black : Costume::effectMI);
            costumesBass.forEach(off ? Costume::black : Costume::black);
            costumesTenor.forEach(off ? Costume::black : Costume::black);
        } else if (milaNumber == TRIAL2) {
            costumesAlt.forEach(Costume::black);
            costumesSopran.forEach(Costume::black);
        } else if (milaNumber == TRIAL3) {
            costumesSopran.forEach(off ? Costume::black : Costume::effectMI);
            costumesAlt.forEach(off ? Costume::black : Costume::effectMI);
            costumesBass.forEach(off ? Costume::black : Costume::effectDO);
            costumesTenor.forEach(off ? Costume::black : Costume::effectDO);
        }
    }

    private void la(int milaNumber, boolean off) {
        milaNumber %= 4;
        if (milaNumber == TRIAL1) {
            costumesSopran.forEach(off ? Costume::black : Costume::effectLA);
            costumesAlt.forEach(off ? Costume::black : Costume::effectMI);
            costumesBass.forEach(Costume::black);
            costumesTenor.forEach(Costume::black);
        } else if (milaNumber == TRIAL2) {
            costumesBass.forEach(off ? Costume::black : Costume::effectLA);
            costumesTenor.forEach(off ? Costume::black : Costume::effectLA);
        } else if (milaNumber == TRIAL3) {
            costumesSopran.forEach(off ? Costume::black : Costume::effectLA);
            costumesAlt.forEach(off ? Costume::black : Costume::effectMI);
            costumesBass.forEach(off ? Costume::black : Costume::effectLA);
            costumesTenor.forEach(off ? Costume::black : Costume::effectLA);
        }
    }

    private void doo(int milaNumber, boolean off) {
        milaNumber %= 4;
        if (milaNumber == TRIAL2) {
            costumesBass.forEach(off ? Costume::black : Costume::effectDO);
            costumesTenor.forEach(off ? Costume::black : Costume::effectDO);
        } else if (milaNumber == TRIAL3) {
            costumesSopran.forEach(off ? Costume::black : Costume::effectDO);
            costumesAlt.forEach(off ? Costume::black : Costume::effectLA);
            costumesBass.forEach(Costume::black);
            costumesTenor.forEach(Costume::black);
        }
    }


    private void oscOrgel(OscMessage msg) {

        if (!msg.checkAddrPattern(ORGAN1)) {
            return;
        }

        boolean noteOn = "note_on".equals(msg.get(0).stringValue());

        note = msg.get(1).intValue();
        velocity = msg.get(2).intValue();
        int noteModulo = note % 12;
        int octave = note / 12;

        if (midiDebug)
            System.out.println("organ " + note + " modnote: " + noteModulo + " oct: " + octave + " vel: " + velocity);

        /*if (selectedEffect == TRIAL1) {  // mi la do la  MILA1
            if (noteModulo == 4 && octave == 5) {
                mi(selectedEffect, !noteOn);
            } else if (noteModulo == 4 && octave == 5) {
                la(selectedEffect, !noteOn);
            }
        } else if (selectedEffect == TRIAL2) {
            // gesteuert von Bjarne oder via Tonerkennung

        } else if (selectedEffect == TRIAL3) {
            if (noteModulo == 4 && octave == 5) {   // 65
                mi(selectedEffect, !noteOn);
            } else if (noteModulo == 4 && octave == 5) {    // 72
                la(selectedEffect, !noteOn);
            } else if (noteModulo == 9 && octave == 5) {   // TODO note finden
                doo(selectedEffect, !noteOn);
            }
        } */

    }

    private void oscBjarne(OscMessage msg) {
        bjnote = msg.get(0).intValue();
        bjvelocity = msg.get(1).intValue();
        if (midiDebug) System.out.println("bjmidi " + bjnote + " vel:" + bjvelocity);

        if (bjnote == 35 && bjvelocity == 0) {
            flash110();
        } else if (bjnote == 34 && bjvelocity == 0) {
            setBlack();
        }


        if (bjvelocity == 0 && selectedEffect == KNEE3 || selectedEffect == KNEE3ON) {
            switch (bjnote) {
                case 80: knee3(4); break;
                case 81: knee3(3); break;
                case 82: knee3(2); break;
                case 83: knee3(1); break;
            }
        }

        if (selectedEffect == TRIAL2 || selectedEffect == TRIAL1 || selectedEffect == TRIAL3) { // Bjarne Fake DO LA
            switch (bjnote) {
                case 40: doo(selectedEffect, bjvelocity == 0); break;
                case 41: la(selectedEffect, bjvelocity == 0); break;
                case 42: mi(selectedEffect, bjvelocity == 0); break;
            }
        }

        if (bjvelocity == 0 && selectedEffect == TRIALPRI) {
            switch (bjnote) {
                case 73: trialPrison(Symbols.RIGHT); break;
                case 74: trialPrison(Symbols.LEFT); break;
                case 75: trialPrison(Symbols.BACKSLASH); break;
                case 76: trialPrison(Symbols.SLASH); break;
                case 89: trialPrison(Symbols.MINUS); break;
                case 90: trialPrison(Symbols.SUSPENDERS); break;
                case 91: trialPrison(Symbols.X); break;
                case 92: trialPrison(Symbols.UX); break;
                default: trialPrison(Symbols.OFF); break;
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

//            if (channel == 10) {    // Michael Hofmeister
//                if (selectedEffect == TRIAL2) {  // mi la do la  MILA2
//                    System.out.println("hofmeister: " + m);
//
//                    if (m <= 52) { // do    // gesang war 47-48
//                        doo(selectedEffect);
//                    } else if (m > 52) { // la   // gesang war 56-57
//                        la(selectedEffect);
//                    }
//                }
//            }

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
            setBlack();

            if (midi != null) { // zurückschalten der Midi Pads wenn man die Radio Buttons drückt
                for (int i = 36; i < 52; i++) {
                    midi.sendNoteOff(9, i, 0);
                }
                switch (selectedEffect) {
                    case TRIAL1: midi.sendNoteOn(9, 48, 10); break;
                    case TRIAL2: midi.sendNoteOn(9, 49, 10); break;
                    case TRIAL3: midi.sendNoteOn(9, 50, 10); break;
                    case DANCE2: midi.sendNoteOn(9, 51, 10); break;
                    case KNEE3: midi.sendNoteOn(9, 44, 10); break;
                    case KNEE3ON: midi.sendNoteOn(9, 45, 10); break;
                    case TRIALPRI: midi.sendNoteOn(9, 46, 10); break;
                    case BUILDING: midi.sendNoteOn(9, 47, 10); break;
                    case KNEE4: midi.sendNoteOn(9, 40, 10); break;
                }
            }
        }
    }

    public void noteOn(int channel, int pitch, int velocity) {
        print("Note On:");
        print(" Channel:" + channel);
        print(" Pitch:" + pitch);
        println(" Velocity:" + velocity);

        switch (pitch) {
            case 36: setBlack(); break;
            case 37: effectRadio.deactivateAll(); break;
            case 38: ; break;
            case 39: ; break;
            case 40: effectRadio.activate(5); break;
            case 41: ; break;
            case 42: ; break;
            case 43: ; break;
            case 44: effectRadio.activate(6); break;   //knee3
            case 45: effectRadio.activate(7); break;   //knee3on
            case 46: effectRadio.activate(8); break;   //trialprison
            case 47: effectRadio.activate(9); break;
            case 48: effectRadio.activate(1); break;  //trial1
            case 49: effectRadio.activate(2); break;  //trial2
            case 50: effectRadio.activate(3); break;  //trial3
            case 51: effectRadio.activate(4); break;  //dance2
        }
    }

    public void noteOff(int channel, int pitch, int velocity) {


        if (pitch >= 40 && pitch <= 51) {
            for (int i = 40; i <= 51; i++) {
                midi.sendNoteOff(9, i, 0);
            }

            midi.sendNoteOn(9, pitch, 10);
        }

    }

    public void controllerChange(int channel, int number, int value) {
        print("Controller Change:");
        print(" Channel:" + channel);
        print(" Number:" + number);
        println(" Value:" + value);

        if (number == 3) {
            sldrOverallBrightness.setValue(value / 127.0f);
        } else if (number == 12) {
            sldrAmpMod.setValue(value / 127.0f);
        } else if (number == 13) {
            sldrEffectDuration.setValue(map(value, 0, 127, 1, 200));
        } else if (number == 14) {
            sldrAttackAudio.setValue(value / 127.0f);
        } else if (number == 15) {
            sldrReleaseAudio.setValue(value / 127.0f);
        }
    }


    public static void main(String args[]) {
        PApplet.main("MainWindow");
    }
}
