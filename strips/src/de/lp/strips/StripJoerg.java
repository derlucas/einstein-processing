package de.lp.strips;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class StripJoerg extends Strip {

    public StripJoerg(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {31, 52},
                {91, 112},
                {127, 143},
                {159, 170},
                {66, 73},
                {6, 12},
                {14, 30},
                {74, 90},
                {156, 158},
                {171, 173},
                {150, 155},
                {174, 179},
                {144, 149},
                {120, 126},
                {60, 64},
                {0, 4},
                {54, 59},
                {114, 119},
        }, 180);
    }

    @Override
    public void effect110cmLine(int color) {
        setSegmentColor(2, color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }
}
