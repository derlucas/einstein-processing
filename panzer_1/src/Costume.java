import hypermedia.net.UDP;
import processing.core.PApplet;

public class Costume {

    final PApplet base;
    final String ipAddress;
    final UDP udp;

    Costume(PApplet base, UDP udp, String ipAddress) {
        this.base = base;
        this.ipAddress = ipAddress;
        this.udp = udp;
    }

}
