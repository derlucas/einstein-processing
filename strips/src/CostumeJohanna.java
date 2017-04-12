import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeJohanna extends Costume {

    CostumeJohanna(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {27, 47},
                {81, 101},
                {114, 124},
                {137, 142},
                {60, 64},
                {6, 9},
                {11, 26},
                {65, 80},
                {134, 136},
                {143, 145},
                {131, 133},
                {146, 148},
                {125, 130},
                {108, 113},
                {54, 58},
                {0, 4},
                {49, 53},
                {103, 107},
        }, 149);
    }
}
