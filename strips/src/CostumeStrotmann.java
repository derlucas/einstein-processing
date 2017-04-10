import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeStrotmann extends CostumeEberl {

    CostumeStrotmann(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(157);
        segmentation = new int[][]{
                {28, 46},
                {82, 100},
                {112, 127},
                {141, 147},
                {62, 67},
                {7, 11},
                {13, 27},
                {68, 81},
                {136, 140},
                {148, 152},
                {132, 135},
                {135, 156},
                {128, 131},
                {108, 111},
                {54, 60},
                {0, 5},
                {48, 53},
                {102, 107},
        };
    }

    @Override
    void effect110cmLine(int color) {
        setSegmentColor(2, color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }
}
