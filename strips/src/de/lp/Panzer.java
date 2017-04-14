package de.lp;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class Panzer {

    final PApplet base;
    final String ipAddress;
    final UDP udp;

    Panzer(PApplet base, UDP udp, String ipAddress) {
        this.base = base;
        this.ipAddress = ipAddress;
        this.udp = udp;
    }

    void effectSingleColor(int color) {


    }

    private void fadeDelta() {



    }

    void black() {
        effectSingleColor(0);
    }

}
