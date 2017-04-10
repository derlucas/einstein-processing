import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeMiklasherich extends CostumeEberl {

    CostumeMiklasherich(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(174);
        segmentation = new int[][]{
            {32, 5}, {96, 121}, {134, 145}, {160, 166},
            {70, 77}, {6, 13}, {15, 31}, {79, 95},
            {157, 159}, {167, 169}, {153, 156}, {170, 173},
            {146, 152}, {128, 133}, {64, 68}, {0, 4},
            {59, 63}, {123, 127},
        };
    }
}
