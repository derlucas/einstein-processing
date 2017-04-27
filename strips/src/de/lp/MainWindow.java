package de.lp;

import controlP5.*;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import themidibus.MidiBus;

import java.awt.*;

public class MainWindow extends PApplet {

    private static final String ORGAN1 = "/midi/Kurzweil_Forte_MIDI_1/5";
    private static final String ORGAN2 = "/midi/MIDISPORT_2x2_Anniv_MIDI_2";
    private static int SENDDELAY = 60;
    private static final int COUNT = 12;

    private float preAmp = 1.0f;
    private float amp[] = new float[12];
    private float ampRendered[] = new float[12];
    private int note;
    private int bjnote;
    private int velocity;
    private int bjvelocity;
    private boolean blackout = false;
    private float redval;
    private float greenval;
    private float blueval;
    private long millisDataSend;
    private long millisAudioRender;
    private boolean midiEnable = true;
    private boolean midiBJEnable = true;
    private boolean midiDebug;

    private float attackAudio = 1.0f;
    private float releaseAudio = 1.0f;

    private MidiBus midi;
    private Slider sldrOverallBrightness;
    private Slider sldrAttackAudio;
    private Slider sldrReleaseAudio;
    private Slider sldrReleaseEffect;
    private Slider sldrAttackEffect;
    private Slider sldrAmpMod;
    private Slider sldrEffectDuration;
    private RadioButton effectRadio;
    private Trinkhalle trinkhalle;

    private long rgbTestMillis;

    public void settings() {
        size(800, 450);
    }

    public void setup() {
        frameRate(60);

        MidiBus.list();

        new OscP5(this, 6000);
        ControlP5 cp5 = new ControlP5(this);
        trinkhalle = new Trinkhalle(this);

        for (int i = 0; i < COUNT; i++) {
            cp5.addToggle("outputStrip" + i).setPosition(300 + i * 40, 70).setSize(30, 15).setId(i).setValue(false).setLabel("STRP " + (i + 1));
            cp5.addToggle("outputPanzer" + i).setPosition(300 + i * 40, 110).setSize(30, 15).setId(i).setValue(false).setLabel("PANZ " + (i + 1));
            cp5.addToggle("testCostume" + i).setPosition(300 + i * 40, 235).setSize(30, 15).setId(i).setValue(false).setLabel("TEST " + (i + 1));
        }

        int y = 10;
        sldrOverallBrightness = cp5.addSlider("overallbrightness").setPosition(10, y).setSize(100, 10).setRange(0, 1.0f).setValue(0.5f);
        cp5.addSlider("preAmp").setPosition(10, y += 15).setSize(100, 10).setRange(1, 40.0f).setValue(1.0f);
        y += 10;
        sldrAttackAudio = cp5.addSlider("attackAudio").setPosition(10, y += 15).setSize(100, 10).setRange(0.0f, 1.0f).setValue(1.0f);
        sldrReleaseAudio = cp5.addSlider("releaseAudio").setPosition(10, y += 15).setSize(100, 10).setRange(0.0f, 1.0f).setValue(1.0f);
        y += 10;
        cp5.addSlider("redval").setPosition(10, y += 15).setSize(100, 10).setRange(0, 1.0f);
        cp5.addSlider("greenval").setPosition(10, y += 15).setSize(100, 10).setRange(0, 1.0f);
        cp5.addSlider("blueval").setPosition(10, y += 15).setSize(100, 10).setRange(0, 1.0f);
        y += 15;
        sldrAmpMod = cp5.addSlider("ampMod").setPosition(10, y += 15).setSize(100, 10).setRange(0.0f, 1.0f).setValue(0.0f);
        sldrEffectDuration = cp5.addSlider("effectFadeDuration").setPosition(10, y += 15).setSize(100, 10).setValue(10).setRange(-1, 126);
        sldrAttackEffect = cp5.addSlider("attackEffect").setPosition(10, y += 15).setSize(100, 10).setRange(0.0f, 1.0f).setValue(1.0f);
        sldrReleaseEffect = cp5.addSlider("releaseEffect").setPosition(10, y += 15).setSize(100, 10).setRange(0.0f, 1.0f).setValue(1.0f);

        cp5.addToggle("setBlackout").setPosition(250, 70).setSize(30, 15).setId(12).setValue(false).setLabel("BO");
        cp5.addToggle("midiEnable").setPosition(210, 70).setSize(30, 15).setLabel("MIDI");
        cp5.addToggle("midiBJEnable").setPosition(210, 110).setSize(30, 15).setLabel("MIDI BJ");
        cp5.addToggle("midiDebug").setPosition(210, 150).setSize(30, 15).setId(12).setValue(false).setLabel("MIDI DBG");

        effectRadio = cp5.addRadioButton("effectRadio").setPosition(300, 160).setSize(30, 15)
                .setColorForeground(color(120)).setColorActive(color(255)).setColorLabel(color(255))
                .setItemsPerRow(5).setSpacingColumn(60).setSpacingRow(10)
                .addItem("TRIAL1", Effect.TRIAL1.getValue()).addItem("TRIAL2", Effect.TRIAL2.getValue())
                .addItem("TRIAL3", Effect.TRIAL3.getValue()).addItem("KNEE3", Effect.KNEE3.getValue())
                .addItem("KNEE3ON", Effect.KNEE3ON.getValue()).addItem("TRIALPRI", Effect.TRIALPRI.getValue())
                .addItem("DANCE2", Effect.DANCE2.getValue()).addItem("KNEE4", Effect.KNEE4.getValue())
                .addItem("BUILDING", Effect.BUILDING.getValue()).addItem("TEST", Effect.TESTMODE.getValue())
                .addItem("RGB", Effect.RGB.getValue()).deactivateAll();

        cp5.addBang("setBlack").setPosition(250, 110).setSize(30, 30).setLabel("BLK");
        cp5.addBang("flash110").setPosition(250, 160).setSize(30, 30).setLabel("F110");

        midi = new MidiBus(this, 0, 1); // this,input,outputdev

        midi.sendControllerChange(0, 3, 127 / 2); // brightness
        midi.sendControllerChange(0, 14, 127);  // sync attack and release audio knobs
        midi.sendControllerChange(0, 15, 127);
        midi.sendControllerChange(0, 12, 0);    // ampMod
        midi.sendControllerChange(0, 13, 11);   // effectFadeDuration
        midi.sendControllerChange(0, 9, 127);   // releaseEffect

        surface.setTitle("Costumes");
        textSize(11);
    }

    public void setBlack() {
        trinkhalle.setBlack();
    }

    public void setBlackout(boolean bo) {      // function for the ControlP5 Toggle
        if (this.blackout != bo && bo) {
            trinkhalle.setBlack();
        }
        this.blackout = bo;
        trinkhalle.setBlackout(bo);
    }

    public void flash110() {                   // function for the ControlP5 Button
        trinkhalle.flash110();
    }

    float hue = 0;

    public void draw() {
        background(0);

        fadeAudio();

        // draw amp inputs
        stroke(50);

        for (int i = 0; i < COUNT; i++) {
            fill(255);
            text(String.format("%.2f", amp[i]), 300 + i * 40, 20);

            if (amp[i] > 0.95) {
                fill(amp[i] * 255, 0, 0);
            } else {
                fill(amp[i] * 255);
            }

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

        if(millis() - rgbTestMillis > 100) {

            int col = Color.HSBtoRGB(hue, 1, 1);
            trinkhalle.testRGB(col);
            hue += 0.01;
            if(hue > 1.0f) hue = 0.0f;
        }

        trinkhalle.render();

        // draw costumes
        trinkhalle.draw(10, 270);

        // send data via UDP
        if (System.currentTimeMillis() - millisDataSend > SENDDELAY) {
            millisDataSend = System.currentTimeMillis();
            trinkhalle.send();
        }
    }

    private void fadeAudio() {

        if (millis() - millisAudioRender > 10) {
            if (attackAudio >= 0.99f && releaseAudio >= 0.99f) {
                // direct switching
                for (int ch = 0; ch < COUNT; ch++) {
                    ampRendered[ch] = amp[ch];
                }
            } else {
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
            }

            trinkhalle.setAmpValues(ampRendered);
            millisAudioRender = millis();
        }
    }

    public void keyPressed() {
        if (trinkhalle.isEffect(Effect.TESTMODE)) {
            if (key == '1') {
                trinkhalle.testCostumeBang();
            }
        } else if (trinkhalle.isEffect(Effect.TRIAL1, Effect.TRIAL2, Effect.TRIAL3)) {
            if (key == 'm') {
                trinkhalle.mi(false);
            } else if (key == 'l') {
                trinkhalle.la(false);
            } else if (key == 'd') {
                trinkhalle.doo(false);
            }
        } else if (trinkhalle.isEffect(Effect.KNEE3, Effect.KNEE3ON)) {  // Knee3
            trinkhalle.knee3(key - 48);
        } else if (trinkhalle.isEffect(Effect.TRIALPRI)) { // trial prison
            switch (key) {
                case '1': trinkhalle.trialPrison(Symbols.RIGHT); break;
                case '2': trinkhalle.trialPrison(Symbols.LEFT); break;
                case '3': trinkhalle.trialPrison(Symbols.BACKSLASH); break;
                case '4': trinkhalle.trialPrison(Symbols.SLASH); break;
                case '5': trinkhalle.trialPrison(Symbols.MINUS); break;
                case '6': trinkhalle.trialPrison(Symbols.SUSPENDERS); break;
                case '7': trinkhalle.trialPrison(Symbols.X); break;
                case '8': trinkhalle.trialPrison(Symbols.UX); break;
                case '9': trinkhalle.trialPrison(Symbols.OFF); break;
            }
        } else if (trinkhalle.isEffect(Effect.DANCE2)) {
            trinkhalle.dance2(key - 48);
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

        if (midiDebug && noteOn) {
            System.out.println("organ " + note + " modnote: " + noteModulo + " oct: " + octave + " vel: " + velocity);
        }

//        if(trinkhalle.isEffect(Effect.DANCE2)) {
//            // Orgel D E F auf Panzer
//            //trinkhalle.dance2();
//        }

        if (trinkhalle.isEffect(Effect.TRIAL1)) {  // mi la do la  MILA1
            if (noteModulo == 4 && octave == 3) {
                trinkhalle.mi(false);
            } else if (noteModulo == 9 && octave == 3) {
                trinkhalle.la(false);
            }
        } else if (trinkhalle.isEffect(Effect.TRIAL2)) {
            // gesteuert von Bjarne oder via Tonerkennung


        } else if (trinkhalle.isEffect(Effect.TRIAL3)) {
            if (noteModulo == 4 && octave == 3) {           // 40
                trinkhalle.mi(false);
            } else if (noteModulo == 9 && octave == 3) {    // 45
                trinkhalle.la(false);
            } else if (noteModulo == 0 && octave == 4) {    // 48
                trinkhalle.doo(false);
            }
        }

    }

    private void oscBjarne(OscMessage msg) {
        bjnote = msg.get(0).intValue();
        bjvelocity = msg.get(1).intValue();
        if (midiDebug && bjvelocity != 0) {
            System.out.println("bjmidi " + bjnote + " vel:" + bjvelocity);
        }

        if (bjnote == 35 && bjvelocity == 0) {
            trinkhalle.flash110();
        } else if (bjnote == 34 && bjvelocity == 0) {
            trinkhalle.setBlack();
        }

        if (bjvelocity == 0 && trinkhalle.isEffect(Effect.KNEE3, Effect.KNEE3ON)) {
            switch (bjnote) {
                case 80: trinkhalle.knee3(4); break;
                case 81: trinkhalle.knee3(3); break;
                case 82: trinkhalle.knee3(2); break;
                case 83: trinkhalle.knee3(1); break;
            }
        }

        if (trinkhalle.isEffect(Effect.TRIAL1, Effect.TRIAL2, Effect.TRIAL3)) { // Bjarne Fake DO LA
            switch (bjnote) {
                case 40: trinkhalle.doo(bjvelocity == 0); break;
                case 41: trinkhalle.la(bjvelocity == 0); break;
                case 42: trinkhalle.mi(bjvelocity == 0); break;
            }
        }

        if (bjvelocity == 0 && trinkhalle.isEffect(Effect.DANCE2)) {
            switch (bjnote) {
                case 31: trinkhalle.weissistalles(0); break;
                case 28: trinkhalle.weissistalles(1); break;
                case 32: trinkhalle.weissistalles(2); break;
                case 29: trinkhalle.weissistalles(3); break;
                case 33: trinkhalle.weissistalles(4); break;
                case 30: trinkhalle.weissistalles(5); break;
                default: trinkhalle.weissistalles(6); break;
            }
        }

        if (bjvelocity == 0 && trinkhalle.isEffect(Effect.TRIALPRI)) {
            switch (bjnote) {
                case 73: trinkhalle.trialPrison(Symbols.RIGHT); break;
                case 74: trinkhalle.trialPrison(Symbols.LEFT); break;
                case 75: trinkhalle.trialPrison(Symbols.BACKSLASH); break;
                case 76: trinkhalle.trialPrison(Symbols.SLASH); break;
                case 89: trinkhalle.trialPrison(Symbols.MINUS); break;
                case 90: trinkhalle.trialPrison(Symbols.SUSPENDERS); break;
                case 91: trinkhalle.trialPrison(Symbols.X); break;
                case 92: trinkhalle.trialPrison(Symbols.UX); break;
                default: trinkhalle.trialPrison(Symbols.OFF); break;
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


        } else if ((msg.checkAddrPattern(ORGAN1) || msg.checkAddrPattern(ORGAN2))) {
            if (midiEnable) {
                oscOrgel(msg);
            }
        } else if (msg.checkAddrPattern("/bjmidi") && midiBJEnable) {
            oscBjarne(msg);
        } else if (msg.checkAddrPattern("/midi/QX61_MIDI_1/0")) {
            boolean noteOn = "note_on".equals(msg.get(0).stringValue());
            note = msg.get(1).intValue();
            velocity = msg.get(2).intValue();
            if (noteOn) {
                System.out.println("note: " + note + " vel: " + velocity);
            }
        } else {
            System.out.println("uknown OSC address " + addr);
        }
    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {

            String name = theEvent.getName();
            float value = theEvent.getValue();

            if (name.startsWith("outputStrip")) {
                trinkhalle.setOutputEnableStrip(theEvent.getId(), value > 0);
            } else if (name.startsWith("outputPanzer")) {
                trinkhalle.setOutputEnablePanzer(theEvent.getId(), value > 0);
            } else if (name.startsWith("testCostume")) {
                trinkhalle.setTestCostume(theEvent.getId(), value > 0);
            } else if (name.startsWith("overallbrightness")) {
                trinkhalle.setBrightness(value);
                if (midi != null) {
                    midi.sendControllerChange(0, 3, (int) map(value, 0, 1.0f, 0, 127.0f));
                }
            } else if (name.startsWith("attackAudio")) {
                if (midi != null) {
                    midi.sendControllerChange(0, 14, (int) map(value, 0, 1.0f, 0, 127.0f));
                }
            } else if (name.startsWith("releaseAudio")) {
                if (midi != null) {
                    midi.sendControllerChange(0, 15, (int) map(value, 0, 1.0f, 0, 127.0f));
                }
            } else if (name.startsWith("ampMod")) {
                trinkhalle.setAmpModFactor(value);
                if (midi != null) {
                    midi.sendControllerChange(0, 12, (int) map(value, 0, 1.0f, 0, 127.0f));
                }
            } else if (name.startsWith("effectFadeDuration")) {
                trinkhalle.setEffectFadeDuration((int) value);
                if (midi != null) {
                    midi.sendControllerChange(0, 13, (int) map(value, -1, 126f, 0, 127.0f));
                }
            } else if (name.startsWith("attackEffect")) {
                trinkhalle.setAttack(value);
            } else if (name.startsWith("releaseEffect")) {
                trinkhalle.setRelease(value);
                if (midi != null) {
                    midi.sendControllerChange(0, 9, (int) map(value, 0, 1.0f, 0, 127.0f));
                }
            } else if (name.startsWith("redval") || name.startsWith("greenval") || name.startsWith("blueval")) {
                trinkhalle.testRGB(color(redval * 255, greenval * 255, blueval * 255));

            }
        }

        if (theEvent.getName().startsWith("effectRadio")) {

            trinkhalle.setSelectedEffect(Effect.from(theEvent.getValue()));
            trinkhalle.setBlack();

            if (midi != null) { // zurückschalten der Midi Pads wenn man die Radio Buttons drückt
                for (int i = 36; i < 52; i++) {
                    midi.sendNoteOff(9, i, 0);
                }
                switch (trinkhalle.getSelectedEffect()) {    // dinge tun beim start der Effekte
                    case TRIAL1: midi.sendNoteOn(9, 48, 10); break;
                    case TRIAL2: midi.sendNoteOn(9, 49, 10); break;
                    case TRIAL3: midi.sendNoteOn(9, 50, 10); break;
                    case KNEE3:
                        midi.sendNoteOn(9, 51, 10);
                        sldrEffectDuration.setValue(15);
                        midi.sendControllerChange(0, 13, 14);
                        sldrAmpMod.setValue(0.2f);
                        break;
                    case KNEE3ON:
                        midi.sendNoteOn(9, 44, 10);
                        sldrEffectDuration.setValue(-1);
                        midi.sendControllerChange(0, 13, 0);
                        sldrAmpMod.setValue(1.0f);
                        trinkhalle.knee3(1);
                        break;
                    case TRIALPRI:
                        midi.sendNoteOn(9, 45, 10);
                        sldrEffectDuration.setValue(38);
                        midi.sendControllerChange(0, 13, 19);
                        break;
                    case DANCE2:
                        midi.sendNoteOn(9, 46, 10);
                        sldrAmpMod.setValue(0.22f);
                        sldrReleaseEffect.setValue(1.0f);
                        sldrAttackAudio.setValue(0.22f);
                        sldrReleaseAudio.setValue(0.17f);
                        trinkhalle.dance2start();   // rot als start setzen
                        break;
                    case KNEE4:
                        midi.sendNoteOn(9, 47, 10);
                        trinkhalle.knee4(0);
                        break;
                    case KNEE4END:
                        midi.sendNoteOn(9, 41, 10);
                        trinkhalle.knee4(1);
                    case BUILDING:
                        midi.sendNoteOn(9, 40, 10);
                        trinkhalle.building();
                        break;

                }
            }
        }
    }

    private void setEffectRadio(Effect effect) {
        int i = 0;
        for (Toggle item : effectRadio.getItems()) {
            if (effect.toString().equals(item.getAddress().substring(1))) {
                effectRadio.activate(i);
                return;
            }
            i++;
        }
    }

    public void noteOn(int channel, int pitch, int velocity) {
        if (midiDebug) {
            print("Note On:");
            print(" Channel:" + channel);
            print(" Pitch:" + pitch);
            println(" Velocity:" + velocity);
        }

        switch (pitch) {
            case 36: trinkhalle.setBlack(); break;
            case 37: effectRadio.deactivateAll(); break;
            case 38: ; break;
            case 39: ; break;
            case 40: setEffectRadio(Effect.BUILDING); break;
            case 41: setEffectRadio(Effect.KNEE4END); break;
            case 42: ; break;
            case 43: ; break;
            case 44: setEffectRadio(Effect.KNEE3ON); break;
            case 45: setEffectRadio(Effect.TRIALPRI); break;
            case 46: setEffectRadio(Effect.DANCE2); break;
            case 47: setEffectRadio(Effect.KNEE4); break;
            case 48: setEffectRadio(Effect.TRIAL1); break;
            case 49: setEffectRadio(Effect.TRIAL2); break;
            case 50: setEffectRadio(Effect.TRIAL3); break;
            case 51: setEffectRadio(Effect.KNEE3); break;
        }

        // demo only, trigger auf Pad Bank B
        if (trinkhalle.isEffect(Effect.TRIALPRI)) {
            switch (pitch) {
                case 64: trinkhalle.trialPrison(Symbols.RIGHT); break;
                case 65: trinkhalle.trialPrison(Symbols.LEFT); break;
                case 66: trinkhalle.trialPrison(Symbols.BACKSLASH); break;
                case 67: trinkhalle.trialPrison(Symbols.SLASH); break;
                case 60: trinkhalle.trialPrison(Symbols.MINUS); break;
                case 61: trinkhalle.trialPrison(Symbols.SUSPENDERS); break;
                case 62: trinkhalle.trialPrison(Symbols.X); break;
                case 63: trinkhalle.trialPrison(Symbols.UX); break;
                default: trinkhalle.trialPrison(Symbols.OFF); break;
            }
        } else if (trinkhalle.isEffect(Effect.TRIAL1, Effect.TRIAL2, Effect.TRIAL3)) {
            switch (pitch) {
                case 64: trinkhalle.mi(false); break;
                case 65: trinkhalle.la(false); break;
                case 66: trinkhalle.doo(false); break;
            }
        } else if (trinkhalle.isEffect(Effect.KNEE3)) {
            switch (pitch) {
                case 64: trinkhalle.knee3(1); break;
                case 65: trinkhalle.knee3(2); break;
                case 66: trinkhalle.knee3(3); break;
                case 67: trinkhalle.knee3(4); break;
            }
        } else if (trinkhalle.isEffect(Effect.DANCE2)) {
            switch (pitch) {
                case 64: trinkhalle.weissistalles(0); break;
                case 65: trinkhalle.weissistalles(1); break;
                case 66: trinkhalle.weissistalles(2); break;
                case 67: trinkhalle.weissistalles(3); break;
                case 60: trinkhalle.weissistalles(4); break;
                case 61: trinkhalle.weissistalles(5); break;
                case 62: trinkhalle.weissistalles(6); break;
                case 63: trinkhalle.weissistalles(6); break;
            }
        }
    }

    public void noteOff(int channel, int pitch, int velocity) {
        // set Pads colors
        if (midi != null && pitch >= 40 && pitch <= 51) {
            for (int i = 40; i <= 51; i++) {
                midi.sendNoteOff(9, i, 0);
            }
            midi.sendNoteOn(9, pitch, 10);
        }

        // demo only, trigger auf Pad Bank B
        if (trinkhalle.isEffect(Effect.TRIAL1, Effect.TRIAL2, Effect.TRIAL3)) {
            switch (pitch) {
                case 64: trinkhalle.mi(true); break;
                case 65: trinkhalle.la(true); break;
                case 66: trinkhalle.doo(true); break;
            }
        }

    }

    public void controllerChange(int channel, int number, int value) {
        if (midiDebug) {
            print("Controller Change:");
            print(" Channel:" + channel);
            print(" Number:" + number);
            println(" Value:" + value);
        }

        if (number == 3) {
            sldrOverallBrightness.setValue(value / 127.0f);
        } else if (number == 9) {
            sldrReleaseEffect.setValue(value / 127.0f);
        } else if (number == 12) {
            sldrAmpMod.setValue(value / 127.0f);
        } else if (number == 13) {
            sldrEffectDuration.setValue(map(value, 0, 127, -1, 126));
        } else if (number == 14) {
            sldrAttackAudio.setValue(value / 127.0f);
        } else if (number == 15) {
            sldrReleaseAudio.setValue(value / 127.0f);
        }
    }

    public static void main(String args[]) {
        PApplet.main("de.lp.MainWindow");
    }
}
