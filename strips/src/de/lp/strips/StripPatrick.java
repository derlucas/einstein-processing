package de.lp.strips;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class StripPatrick extends Strip {

    public StripPatrick(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {30, 48},
                {88, 106},
                {120, 131},
                {143, 149},
                {68, 73},
                {10, 14},
                {16, 29},
                {74, 87},
                {140, 142},
                {150, 152},
                {136, 139},
                {153, 156},
                {132, 135},
                {116, 119},
                {58, 66},
                {0, 8},
                {50, 57},
                {108, 115},
        }, 157);
    }

}
