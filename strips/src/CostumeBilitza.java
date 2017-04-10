import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeBilitza extends CostumeEberl {

    CostumeBilitza(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(162);
        segmentation = new int[][]{
                {30, 51},
                {89, 110},
                {124, 136},
                {150, 164},
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
        };
    }
}
