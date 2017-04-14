package de.lp.strips;

import hypermedia.net.UDP;
import processing.core.PApplet;

public class StripMichael extends Strip {

    public StripMichael(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {30, 52},
                {89, 111},
                {124, 137},
                {151, 159},
                {66, 72},
                {7, 12},
                {14, 29},
                {73, 88},
                {148, 150},
                {160, 162},
                {144, 147},
                {163, 166},
                {138, 143},
                {119, 123},
                {60, 64},
                {0, 5},
                {54, 59},
                {113, 118},
        }, 167);
    }
}
