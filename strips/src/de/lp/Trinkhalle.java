package de.lp;

import de.lp.panzers.Panzer;
import de.lp.strips.*;
import hypermedia.net.UDP;

import java.util.ArrayList;
import java.util.List;

public class Trinkhalle {

    private Effect selectedEffect = Effect.NONE;

    private final static String ADDRESSES_STRIPS[] = {"192.168.80.121", "192.168.80.122", "192.168.80.123", "192.168.80.124",
            "192.168.80.125", "192.168.80.126", "192.168.80.127", "192.168.80.128",
            "192.168.80.129", "192.168.80.130", "192.168.80.131", "192.168.80.132"};

    private final static String ADDRESSES_PANZER[] = {"192.168.80.101", "192.168.80.102", "192.168.80.103", "192.168.80.104",
            "192.168.80.105", "192.168.80.106", "192.168.80.107", "192.168.80.108",
            "192.168.80.109", "192.168.80.110", "192.168.80.111", "192.168.80.112"};

    private final MainWindow base;
    private final UDP udp;
    private final List<Strip> strips = new ArrayList<>();
    private final List<Strip> costumesAlt = new ArrayList<>();
    private final List<Strip> costumesSopran = new ArrayList<>();
    private final List<Strip> costumesBass = new ArrayList<>();
    private final List<Strip> costumesTenor = new ArrayList<>();
    private final List<Panzer> panzers = new ArrayList<>();
    private final List<Panzer> panzersAlt = new ArrayList<>();
    private final List<Panzer> panzersSopran = new ArrayList<>();
    private final List<Panzer> panzersBass = new ArrayList<>();
    private final List<Panzer> panzersTenor = new ArrayList<>();
    private float ampRendered[] = new float[12];
    private float ampModFactor = 0.0f;
    private float overallbrightness;
    private float attack = 1.0f;
    private float release = 1.0f;
    private int effect110Value = -1;
    private int effect110Duration = 10;
    private int effectFadeValue = -1;
    private int effectFadeDuration = 10;
    private int stripsTestBang = 0;
    private Symbols effectSymbol = Symbols.OFF;
    private final int colorBlue;
    private final int colorRed;
    private boolean testCostumeMode[] = new boolean[12];

    Trinkhalle(MainWindow mainWindow) {
        this.base = mainWindow;
        this.udp = new UDP(this, 2102);

        colorBlue = base.color(0,76,255);
        colorRed = base.color(255,2,0);

        Strip anna = new StripAnna(mainWindow, udp, ADDRESSES_STRIPS[0]);                // Anna Miklashevich
        Strip katharina = new StripKatharina(mainWindow, udp, ADDRESSES_STRIPS[1]);      // Katharina Eberl
        Strip ulrike = new StripUlrike(mainWindow, udp, ADDRESSES_STRIPS[2]);            // Ulrike Hellermann
        Strip luisa = new StripLuisa(mainWindow, udp, ADDRESSES_STRIPS[3]);              // Luisa Kruppa
        Strip dominique = new StripDominique(mainWindow, udp, ADDRESSES_STRIPS[4]);      // Dominique Bilitza
        Strip johanna = new StripJohanna(mainWindow, udp, ADDRESSES_STRIPS[5]);          // Johanna Krödel
        Strip patrick = new StripPatrick(mainWindow, udp, ADDRESSES_STRIPS[6]);          // Patrick Brandt
        Strip fabian = new StripFabian(mainWindow, udp, ADDRESSES_STRIPS[7]);            // Fabian Strotmann
        Strip joerg = new StripJoerg(mainWindow, udp, ADDRESSES_STRIPS[8]);              // Jörg Deutschewitz
        Strip michael = new StripMichael(mainWindow, udp, ADDRESSES_STRIPS[9]);          // Michael Hofmeister
        Strip christian = new StripChristian(mainWindow, udp, ADDRESSES_STRIPS[10]);     // Christian Walter
        Strip julian = new StripJulian(mainWindow, udp, ADDRESSES_STRIPS[11]);           // Julian Popken

        strips.add(anna); strips.add(katharina); strips.add(ulrike);
        strips.add(luisa); strips.add(dominique); strips.add(johanna);
        strips.add(patrick); strips.add(fabian); strips.add(joerg);
        strips.add(michael); strips.add(christian); strips.add(julian);

        costumesSopran.add(anna); costumesSopran.add(katharina); costumesSopran.add(ulrike);
        costumesAlt.add(luisa); costumesAlt.add(dominique); costumesAlt.add(johanna);
        costumesTenor.add(patrick); costumesTenor.add(fabian); costumesTenor.add(joerg);
        costumesBass.add(michael); costumesBass.add(christian); costumesBass.add(julian);


        Panzer panzerAnna = new Panzer(mainWindow, udp, ADDRESSES_PANZER[0]);
        Panzer panzerKatharina = new Panzer(mainWindow, udp, ADDRESSES_PANZER[1]);
        Panzer panzerUlrike = new Panzer(mainWindow, udp, ADDRESSES_PANZER[2]);
        Panzer panzerLuisa = new Panzer(mainWindow, udp, ADDRESSES_PANZER[3]);
        Panzer panzerDominique = new Panzer(mainWindow, udp, ADDRESSES_PANZER[4]);
        Panzer panzerJohanna = new Panzer(mainWindow, udp, ADDRESSES_PANZER[5]);
        Panzer panzerPatrick = new Panzer(mainWindow, udp, ADDRESSES_PANZER[6]);
        Panzer panzerFabian = new Panzer(mainWindow, udp, ADDRESSES_PANZER[7]);
        Panzer panzerJoerg = new Panzer(mainWindow, udp, ADDRESSES_PANZER[8]);
        Panzer panzerMichael = new Panzer(mainWindow, udp, ADDRESSES_PANZER[9]);
        Panzer panzerChristian = new Panzer(mainWindow, udp, ADDRESSES_PANZER[10]);
        Panzer panzerJulian = new Panzer(mainWindow, udp, ADDRESSES_PANZER[11]);

        panzers.add(panzerAnna); panzers.add(panzerKatharina); panzers.add(panzerUlrike);
        panzers.add(panzerLuisa); panzers.add(panzerDominique); panzers.add(panzerJohanna);
        panzers.add(panzerPatrick); panzers.add(panzerFabian); panzers.add(panzerJoerg);
        panzers.add(panzerMichael); panzers.add(panzerChristian); panzers.add(panzerJulian);

        panzersSopran.add(panzerAnna); panzersSopran.add(panzerKatharina); panzersSopran.add(panzerUlrike);
        panzersAlt.add(panzerLuisa); panzersAlt.add(panzerDominique); panzersAlt.add(panzerJohanna);
        panzersTenor.add(panzerPatrick); panzersTenor.add(panzerFabian); panzersTenor.add(panzerJoerg);
        panzersBass.add(panzerMichael); panzersBass.add(panzerChristian); panzersBass.add(panzerJulian);
    }

    Effect getSelectedEffect() {
        return selectedEffect;
    }

    void setSelectedEffect(Effect selectedEffect) {
        this.selectedEffect = selectedEffect;
    }

    int getColorBlue() {
        return colorBlue;
    }

    boolean isEffect(Effect... effect) {
        for (Effect ef : effect) {
            if (this.selectedEffect.equals(ef)) {
                return true;
            }
        }
        return false;
    }

    void flash110() {
        int color = base.color(255);
        strips.forEach(costume -> costume.effect110cmLine(color));
        effect110Value = effect110Duration;
    }

    void trialPrison(Symbols symbol) {
        int color = base.color(255);
        //color = color(255, 196,135);  // warmweiß
        for (Strip strip : strips) {
            strip.effectSingleColor(0);
            strip.effectSymbol(symbol, color);
        }
        effectSymbol = symbol;
        effectFadeValue = effectFadeDuration;
    }

    void knee3(int num) {
        for (Strip strip : strips) {
            strip.effectSingleColor(0);
        }

        final int color = base.color(255);
        if (num == 1) {
            effectSymbol = Symbols.UX;
        } else if (num == 2) {
            effectSymbol = Symbols.BACKSLASH;
        } else if (num == 3) {
            effectSymbol = Symbols.SLASH;
        } else if (num == 4) {
            effectSymbol = Symbols.X;
        }

        strips.forEach(costume -> costume.effectSymbol(effectSymbol, color));
        effectFadeValue = effectFadeDuration;
    }

    void mi(boolean off) {
        if (selectedEffect == Effect.TRIAL1) {
            costumesSopran.forEach(off ? Strip::black : Strip::effectMI);
            costumesAlt.forEach(off ? Strip::black : Strip::effectMI);
            costumesBass.forEach(off ? Strip::black : Strip::black);
            costumesTenor.forEach(off ? Strip::black : Strip::black);
        } else if (selectedEffect == Effect.TRIAL2) {
            costumesAlt.forEach(Strip::black);
            costumesSopran.forEach(Strip::black);
        } else if (selectedEffect == Effect.TRIAL3) {
            costumesSopran.forEach(off ? Strip::black : Strip::effectMI);
            costumesAlt.forEach(off ? Strip::black : Strip::effectMI);
            costumesBass.forEach(off ? Strip::black : Strip::effectDO);
            costumesTenor.forEach(off ? Strip::black : Strip::effectDO);
        }
    }

    void la(boolean off) {
        if (selectedEffect == Effect.TRIAL1) {
            costumesSopran.forEach(off ? Strip::black : Strip::effectLA);
            costumesAlt.forEach(off ? Strip::black : Strip::effectMI);
            costumesBass.forEach(Strip::black);
            costumesTenor.forEach(Strip::black);
        } else if (selectedEffect == Effect.TRIAL2) {
            costumesBass.forEach(off ? Strip::black : Strip::effectLA);
            costumesTenor.forEach(off ? Strip::black : Strip::effectLA);
        } else if (selectedEffect == Effect.TRIAL3) {
            costumesSopran.forEach(off ? Strip::black : Strip::effectLA);
            costumesAlt.forEach(off ? Strip::black : Strip::effectMI);
            costumesBass.forEach(off ? Strip::black : Strip::effectLA);
            costumesTenor.forEach(off ? Strip::black : Strip::effectLA);
        }
    }

    void doo(boolean off) {
        if (selectedEffect == Effect.TRIAL2) {
            costumesBass.forEach(off ? Strip::black : Strip::effectDO);
            costumesTenor.forEach(off ? Strip::black : Strip::effectDO);
        } else if (selectedEffect == Effect.TRIAL3) {
            costumesSopran.forEach(off ? Strip::black : Strip::effectDO);
            costumesAlt.forEach(off ? Strip::black : Strip::effectLA);
            costumesBass.forEach(Strip::black);
            costumesTenor.forEach(Strip::black);
        }
    }

    void dance2(int num) {

        // was soll beim start von Dance kommen?
        final int sopran;
        final int tenor;
        final int alt;
        final int bass;

        if (num == 1) {                     // D von Orgel
            sopran = tenor = base.color(0, 0, 255);
            alt = bass = base.color(0, 255, 0);
        } else if (num == 2) {              // E von Orgel
            sopran = tenor = base.color(255, 0, 0);
            alt = bass = base.color(0, 0, 255);
        } else if (num == 3) {              // F von Orgel
            sopran = tenor = base.color(0, 255, 0);
            alt = bass = base.color(0, 0, 255);
        } else if (num == 4) {              // F von Orgel
            sopran = tenor = alt = bass = base.color(255, 0, 0);
        } else {
            sopran = tenor = alt = bass = 0;
        }

        panzersSopran.forEach(panzer -> panzer.effectSingleColor(sopran));
        panzersTenor.forEach(panzer -> panzer.effectSingleColor(tenor));
        panzersAlt.forEach(panzer -> panzer.effectSingleColor(alt));
        panzersBass.forEach(panzer -> panzer.effectSingleColor(bass));
    }

    void dance2start() {
        panzers.forEach(panzer -> panzer.effectSingleColor(colorRed));
    }

    void knee4(int mode) {
        if (mode == 1) {
            panzersSopran.forEach(panzer -> panzer.effectSingleColor(colorBlue));
            panzersAlt.forEach(panzer -> panzer.effectSingleColor(colorBlue));
        } else {
            panzersSopran.forEach(panzer -> panzer.effectSingleColor(0));
            panzersAlt.forEach(panzer -> panzer.effectSingleColor(0));
        }
        panzersTenor.forEach(panzer -> panzer.effectSingleColor(colorRed));
        panzersBass.forEach(panzer -> panzer.effectSingleColor(colorRed));
    }

    void building() {
        int color = base.color(0, 0, 255);
        panzers.forEach(panzer -> panzer.effectSingleColor(color));
    }

    void weissistalles(int farbe) {
        // weiss, gruen, rot, violett, gelb, lila
        // 0,1,2,3,4,5
        int color;
        switch (farbe) {
            case 0: color = base.color(255); break;
            case 1: color = base.color(0, 255, 0); break;
            case 2: color = base.color(255, 0, 0); break;
            case 3: color = base.color(0, 0, 255); break;
            case 4: color = base.color(255, 255, 0); break;
            case 5: color = base.color(255, 0, 255); break;
            default: color = 0;
        }
        panzers.forEach(panzer -> panzer.effectSingleColor(color));
    }

    void testCostumeBang() {
        stripsTestBang++;
        stripsTestBang %= Strip.SEGMENTS;
        for (Strip strip : strips) {
            strip.black();
            strip.setSegmentColor(stripsTestBang, base.color(255));
        }

        for (Panzer panzer : panzers) {
            panzer.black();
            panzer.setSegmentBrightness(stripsTestBang % Panzer.SEGMENTS, 1.0f);
        }
    }

    void testRGB(int color) {
        if (isEffect(Effect.RGB)) {
            for (Strip strip : strips) {
                strip.effectSingleColor(color);
            }
            for (Panzer panzer : panzers) {
                panzer.effectSingleColor(color);
            }
        }
    }

    void setTestCostume(int costume, boolean on) {
        if(!on && testCostumeMode[costume]) {
            strips.get(costume).effectSingleColor(0);
            panzers.get(costume).effectSingleColor(0);
        }
        testCostumeMode[costume] = on;
    }

    void render() {

        // Amp calculations
        for (int i = 0; i < strips.size(); i++) {
            Strip strip = strips.get(i);
            Panzer panzer = panzers.get(i);
            float brightness = 1 - ampModFactor;
            brightness = brightness + ampRendered[i] * ampModFactor;
            strip.setBrightness(brightness * overallbrightness);
            panzer.setBrightness(brightness * overallbrightness);
            strip.render();
            panzer.render();

        }

        if (effect110Value > 0) {
            effect110Value--;
            int color = base.color(255 * ((float) effect110Value / (float) effect110Duration));
            strips.forEach(costume -> costume.effect110cmLine(color));
        } else if (effect110Value == 0) {
            effect110Value--;
            int color = base.color(0);
            strips.forEach(costume -> costume.effect110cmLine(color));
        }

        if (effectFadeValue > 0) {
            effectFadeValue--;
            int color = base.color(255 * ((float) effectFadeValue / (float) effectFadeDuration));
            if (isEffect(Effect.TRIALPRI, Effect.KNEE3)) {
                strips.forEach(costume -> costume.effectSymbol(effectSymbol, color));
            }
        } else if (effectFadeValue == 0) {
            effectFadeValue--;
            int color = base.color(0);
            if (isEffect(Effect.TRIALPRI, Effect.KNEE3)) {
                strips.forEach(costume -> costume.effectSymbol(effectSymbol, color));
            }
        }

        int white = base.color(100);
        for (int i = 0; i < 12; i++) {
            if(testCostumeMode[i]) {
                strips.get(i).effectSingleColor(white);
                panzers.get(i).effectSingleColor(white);
            }
        }
    }

    void send() {
        strips.forEach(Strip::send);
        panzers.forEach(Panzer::send);
    }

    void draw(int startX, int startY) {
        int i = 0;
        for (Strip strip : strips) {
            base.pushMatrix();
            base.translate(startX + i * 65, startY);
            strip.display();
            base.popMatrix();
            i++;
        }

        i = 0;
        for (Panzer panzer : panzers) {
            base.pushMatrix();
            base.translate(startX + i * 65, startY + 140);
            panzer.display();
            base.popMatrix();
            i++;
        }
    }

    void setBlack() {
        strips.forEach(Strip::black);
        panzers.forEach(Panzer::black);
    }

    void setBlackout(boolean blackout) {
        for (Strip strip : this.strips) {
            strip.setBlackout(blackout);
        }
        for (Panzer panzer : this.panzers) {
            panzer.setBlackout(blackout);
        }
    }

    void setAmpModFactor(float ampModFactor) {
        this.ampModFactor = ampModFactor;
    }

    void setAmpValues(float ampValues[]) {
        ampRendered = ampValues;
    }

    void setOutputEnableStrip(int id, boolean b) {
        if (id >= 0 && id < strips.size() && strips.get(id) != null) {
            strips.get(id).setEnabled(b);
        }
    }

    void setOutputEnablePanzer(int id, boolean b) {
        if (id >= 0 && id < panzers.size() && panzers.get(id) != null) {
            panzers.get(id).setEnabled(b);
        }
    }

    void setBrightness(float brightness) {
        this.overallbrightness = brightness;
    }

    void setEffectFadeDuration(int effectFadeDuration) {
        this.effectFadeDuration = effectFadeDuration;
    }

    void setAttack(float value) {
        this.attack = value;
        strips.forEach(strip -> strip.setAttack(value));
        panzers.forEach(panzer -> panzer.setAttack(value));
    }

    void setRelease(float value) {
        this.release = value;
        strips.forEach(strip -> strip.setRelease(value));
        panzers.forEach(panzer -> panzer.setRelease(value));
    }
}

