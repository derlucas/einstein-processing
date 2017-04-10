import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeDeutschewitz extends CostumeEberl {

    CostumeDeutschewitz(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(180);
        segmentation = new int[][]{
                {31, 52},
                {82, 100},
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
        };
    }

    @Override
    void effect110cmLine(int color) {
        effectSingleColor(0);
        setSegmentColor(2, color);
        setSegmentColor(12, color);
        setSegmentColor(13, color);
    }
}
