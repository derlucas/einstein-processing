import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeUlrike extends Costume {

    CostumeUlrike(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {32, 56},
                {96, 120},
                {134, 145},
                {160, 166},
                {70, 77},
                {6, 12},
                {15, 31},
                {78, 95},
                {157, 159},
                {167, 169},
                {153, 156},
                {170, 173},
                {146, 152},
                {128, 133},
                {64, 69},
                {0, 5},
                {57, 63},
                {121, 127}
        }, 174);

    }
}
