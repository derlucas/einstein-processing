package de.lp.strips;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class StripDominique extends Strip {

    public StripDominique(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {30, 51},
                {89, 110},
                {124, 136},
                {150, 154},
                {65, 70},
                {6, 10},
                {12, 29},
                {71, 88},
                {147, 149},
                {155, 157},
                {143, 146},
                {158, 161},
                {137, 142},
                {118, 123},
                {59, 63},
                {0, 4},
                {53, 58},
                {112, 117},
        }, 162);
    }
}
