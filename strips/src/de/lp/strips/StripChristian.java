package de.lp.strips;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class StripChristian extends Strip {

    public StripChristian(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {31, 52},
                {91, 112},
                {128, 143},
                {163, 171},
                {68, 74},
                {8, 13},
                {15, 30},
                {75, 90},
                {159, 162},
                {172, 175},
                {152, 158},
                {176, 182},
                {144, 151},
                {120, 127},
                {60, 66},
                {0, 6},
                {54, 59},
                {114, 119},
        }, 183);
    }

    @Override
    public void effect110cmLine(int color) {
        setSegmentColor(2, color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }
}
