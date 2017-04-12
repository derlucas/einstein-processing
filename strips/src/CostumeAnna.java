import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeAnna extends Costume {

    CostumeAnna(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {32, 57},
                {96, 121},
                {134, 145},
                {160, 166},
                {70, 78},
                {6, 13},
                {15, 31},
                {79, 95},
                {157, 159},
                {167, 169},
                {153, 156},
                {170, 173},
                {146, 152},
                {128, 133},
                {64, 68},
                {0, 4},
                {59, 63},
                {123, 127},
        }, 174);
    }
}
