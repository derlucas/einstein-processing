import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeKruppa extends CostumeEberl {

    CostumeKruppa(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(170);
        segmentation = new int[][]{
                {31, 55},
                {93, 117},
                {129, 144},
                {156, 163},
                {67, 74},
                {5, 12},
                {13, 30},
                {76, 92},
                {153, 155},
                {164, 166},
                {150, 152},
                {167, 169},
                {145, 149},
                {124, 128},
                {62, 65},
                {0, 3},
                {57, 61},
                {119, 123},
        };
    }
}
