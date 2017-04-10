import hypermedia.net.UDP;
import processing.core.PApplet;

public class CostumeEberl extends Costume {

    CostumeEberl(PApplet base, UDP udp, String ipAddress) {
        super(base, udp, ipAddress);
        setLedCount(174);
        segmentation = new int[][]{
            {32, 56}, {96, 120}, {134, 145}, {160, 166},
            {70, 77}, {6, 12}, {14, 31}, {78, 95},
            {157, 159}, {167, 169}, {153, 156}, {170, 173},
            {146, 152}, {128, 133}, {64, 68}, {0, 4},
            {58, 63}, {122, 127}};
    }
}
