import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeEberl extends Costume {

    CostumeEberl(PApplet base, UDP udp, int x, int y, String ipAddress) {
        super(base, udp, x, y, ipAddress);
        setLedCount(174);
        segmentation = new int[][]{
            {32, 56}, {96, 120}, {134, 145}, {160, 166},
            {70, 77}, {6, 12}, {14, 31}, {78, 95},
            {157, 159}, {167, 169}, {153, 156}, {170, 173},
            {146, 152}, {128, 133}, {64, 68}, {0, 4},
            {58, 63}, {122, 127}};
    }

    @Override
    void effectMI() {
        effectSingleColor(0);

        // mi   unten linie
        setSegmentColor(2, base.color(255, 240, 0));
        setSegmentColor(12, base.color(255, 240, 0));
        setSegmentColor(13, base.color(255, 240, 0));
    }

    @Override
    void effectLA() {
        effectSingleColor(0);

        // hosenträger
        setSegmentColor(0, base.color(0, 0, 255));
        setSegmentColor(1, base.color(0, 0, 255));
        setSegmentColor(16, base.color(0, 0, 255));
        setSegmentColor(17, base.color(0, 0, 255));
    }

    @Override
    void effectDO() {
        effectSingleColor(0);

        // V neck
        setSegmentColor(6, base.color(255, 0, 0));
        setSegmentColor(7, base.color(255, 0, 0));
    }


}
