import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeJulian extends Costume {

    CostumeJulian(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress, new int[][]{
                {30, 49},
                {88, 108},
                {118, 135},
                {144, 152},
                {65, 70},
                {7, 13},
                {14, 29},
                {72, 87},
                {141, 143},
                {153, 155},
                {138, 140},
                {156, 158},
                {136, 137},
                {116, 117},
                {58, 63},
                {0, 5},
                {51, 57},
                {110, 115},
        }, 159);
    }

    @Override
    void effect110cmLine(int color) {
        setSegmentColor(2, color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }
}
