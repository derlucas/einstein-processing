import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeDemo extends Costume {

    CostumeDemo(PApplet base, UDP udp, int x, int y, String ipAddress) {
        super(base, udp, x, y, ipAddress);
        setLedCount(170);
        segmentation = new int[][]{
            {35, 64}, {100, 129}, {130, 149}, {156, 163},
            {76, 81}, {11, 16}, {18, 34}, {83, 99},
            {150, 155}, {164, 169}, {65, 74}, {0, 9},
            {82, 82}
        };
    }

    void setSegmentColor(int segment, int color) {
        if (segment < 0 || segment > 11) {
            return;
        }

        for (int i = segmentation[segment][0]; i <= segmentation[segment][1]; i++) {
            setLedColor(i, color);
        }
    }

    void effectMI() {
        // unten linie
        effectSingleColor(0);
        setSegmentColor(2, base.color(255, 240, 0));
    }

    void effectLA() {
        // hosenträger
        effectSingleColor(0);
        int col = base.color(0, 0, 255);
        setSegmentColor(0, col);
        setSegmentColor(1, col);
    }

    void effectDO() {
        // X
        effectSingleColor(0);
        int col = base.color(255, 0, 0);
        setSegmentColor(6, col);
        setSegmentColor(7, col);
        setSegmentColor(4, col);
        setSegmentColor(5, col);
        setSegmentColor(10, col);
        setSegmentColor(11, col);
    }

    void effectSingleColor(int color) {
        for (int i = 0; i < Strips.SEGMENTS; i++) {
            setSegmentColor(i, color);
        }
    }

    void effect110cmLine(int color) {
        setSegmentColor(3, color);
        setSegmentColor(8, color);
        setSegmentColor(9, color);
    }
}
