import controlP5.ControlP5;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;

public class Strips extends PApplet {

    private float ampFactor = 1.0f;
    private int COUNT = 12;
    float amp[] = new float[12];
    OscP5 oscP5;
    private ControlP5 cp5;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        oscP5 = new OscP5(this, 2001);
        frameRate(60);

        cp5 = new ControlP5(this);
        cp5.addSlider("ampFactor").setPosition(10, 70).setSize(500, 20).setRange(0, 50.0f);
    }


    public void draw() {
        background(0);

        for (int i = 0; i < COUNT; i++) {
            fill(255);
            text(amp[i] + "", 10, 120 + i * 20);
            stroke(50);
            fill(amp[i] * 255);
            rect(i * 65, 0, 60, 60);
        }

//        float bla = 10*log(ampFactor+1) / log(10);
//        fill(255);
//        text("bla: " + bla, 100, 100);
    }


    void oscEvent(OscMessage msg) {

        String addr = msg.addrPattern();

        if (addr.startsWith("amp")) {
            int channel = Integer.parseInt(addr.substring(3));
            amp[channel - 1] = msg.get(0).floatValue();
            amp[channel - 1] = amp[channel - 1] * ampFactor;
        }

        if (msg.checkAddrPattern("keyNote")) {
            int note = msg.get(0).intValue();
            System.out.println("note: " + note);
        }

        if (msg.checkAddrPattern("keyVel")) {
            int vel = msg.get(0).intValue();
            System.out.println("vel =" + vel);
        }

    }

    public static void main(String args[]) {
        PApplet.main("Strips");
    }

}
