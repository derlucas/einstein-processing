import controlP5.ControlEvent;
import controlP5.ControlP5;
import controlP5.DropdownList;
import processing.core.PApplet;
import hypermedia.net.*;

import java.io.IOException;
import java.net.*;

public class MainFrame extends PApplet {

    String addresses[] = {"192.168.79.101", "192.168.79.102", "192.168.79.103", "192.168.79.104",
            "192.168.79.105", "192.168.79.106", "192.168.79.107", "192.168.79.108",
            "192.168.79.109", "192.168.79.110", "192.168.79.111", "192.168.79.112"};

    int PANZER = 12;
    UDP udp;
    int ch[][] = new int[PANZER][5];
    ControlP5 cp5;
    float ampFactor = 0.5f;
    float ampValue = 0.0f;
    float freqValue = 0.0f;
    float jitter = 0.0f;
    float minVal = 0.0f;
    float maxVal = 0.0f;
    boolean impulse = false;
    boolean output[] = new boolean[PANZER];
    DropdownList ddEffect;
    int selectedEffect;
    int chaserStep = 0;
    boolean blackout = false;

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        udp = new UDP(this, 2001);
        udp.listen(true);
        cp5 = new ControlP5(this);

        for (int i = 0; i < PANZER; i++) {
            for (int j = 0; j < 5; j++) {
                ch[i][j] = 0;
            }
            output[i] = true;
            cp5.addToggle("output" + i).setPosition(10 + i * 40, 150).setSize(30, 15).setId(i).setValue(output[i]).setLabel("Pa " + (i + 1));
        }

        cp5.addToggle("blackout").setPosition(10 + 12 * 40, 150).setSize(30, 15).setId(12).setValue(false).setLabel("BO");

        cp5.addSlider("ampFactor").setPosition(10, 10).setSize(100, 20).setRange(0, 5.0f);
        cp5.addSlider("jitter").setPosition(10, 35).setSize(100, 20).setRange(0, 1.0f);
        cp5.addSlider("minVal").setPosition(10, 60).setSize(100, 20).setRange(0, 1.0f).setValue(0.2f);
        cp5.addSlider("maxVal").setPosition(10, 85).setSize(100, 20).setRange(0, 1.0f).setValue(0.75f);

        ddEffect = cp5.addDropdownList("effectList").setPosition(200, 10).setLabel("effect");
        ddEffect.close();
        ddEffect.addItem("amp", 0);
        ddEffect.addItem("chaser", 1);

        frameRate(30);
        surface.setTitle("Panzer");
    }

    public void draw() {
        background(0);

        fill(255);
        text("amp:  " + ampValue, 500, 10);
        text("freq: " + freqValue, 650, 10);

        fill(impulse ? 255 : 0);
        rect(0, 0, 20, 20);

        renderEffect();

        pushMatrix();

        translate(10, 200);

        for (int i = 0; i < PANZER; i++) {
            for (int j = 0; j < 5; j++) {
                fill(ch[i][j]);
                rect(i * 40 + j * 6, 0, 6, 32);
            }
        }

        popMatrix();

        sendPanzer();
    }

    private void renderEffect() {

        if (selectedEffect == 0) {   // AMP

            for (int i = 0; i < PANZER; i++) {
                float val = ampValue * ampFactor;
                float a, b, c, d, e;

                a = minVal + val + (val * (random(-jitter, jitter)));
                b = minVal + val + (val * (random(-jitter, jitter)));
                c = minVal + val + (val * (random(-jitter, jitter)));
                d = minVal + val + (val * (random(-jitter, jitter)));
                e = minVal + val + (val * (random(-jitter, jitter)));

                einzeln(i, a, b, c, d, e);
            }
        } else if (selectedEffect == 1) {
            for (int i = 0; i < PANZER; i++) {
                alle(i, i == chaserStep ? (int) (maxVal * 255) : (int) (minVal * 255));
            }
            text("chaser: " + chaserStep, 0, 0);
        } else if( selectedEffect == 2) {


        }

    }

    private void einzeln(int panzer, float ch1, float ch2, float ch3, float ch4, float ch5) {
        ch[panzer][0] = (int) (255 * ch1);
        ch[panzer][1] = (int) (255 * ch2);
        ch[panzer][2] = (int) (255 * ch3);
        ch[panzer][3] = (int) (255 * ch4);
        ch[panzer][4] = (int) (255 * ch5);

        for (int j = 0; j < 5; j++) {
            if (ch[panzer][j] > 255) ch[panzer][j] = 255;
            if (ch[panzer][j] < 0) ch[panzer][j] = 0;
        }
    }

    private void alle(int i, int val) {
        if (val > 255) val = 255;
        if (val < 0) val = 0;
        for (int j = 0; j < 5; j++) {
            ch[i][j] = val;
        }
    }

    public void sendPanzer() {
        for (int i = 0; i < PANZER; i++) {
            if (output[i]) {
                byte[] channels = new byte[5];
                for (int j = 0; j < 5; j++) {
                    if (blackout) {
                        channels[j] = 0;
                    } else {
                        channels[j] = (byte) ch[i][j];
                    }
//                    channels[j] = (byte) val;
                }
                udp.send(channels, addresses[i], 4210);
            }
        }
    }

    public void receive(byte[] data) {
        data = subset(data, 0, data.length);
        String message = new String(data);

        if (message.startsWith("/freq")) {
            String temp = message.split(" ")[1];
            temp = temp.trim();
            temp = temp.replaceAll(",", "");
            freqValue = Float.parseFloat(temp);
        }

        if (message.startsWith("/amp")) {
            String temp = message.split(" ")[1];
            temp = temp.trim();
            temp = temp.replaceAll(",", "");
            ampValue = Float.parseFloat(temp);
        }

        if (message.startsWith("/impulse")) {
            impulse = !impulse;
            chaserStep++;
            chaserStep %= PANZER;
        }

  /*
     if(mode == 0) {
         mode = 1;
       } else {
         mode = 0;
         // val = 0;
       }

     for(int i = 0; i< PANZER; i++) {
       ch[i][0] = val; ch[i][1] = val; ch[i][2] = val; ch[i][3] = val; ch[i][4] = val;

       if(mode == 0) {
         //ch[i][0] = 255; ch[i][1] = 255; ch[i][2] = 255; ch[i][3] = 255; ch[i][4] = 255;
       } else {
         //ch[i][0] = 0; ch[i][1] = 0; ch[i][2] = 0; ch[i][3] = 0; ch[i][4] = 0;
       }

        client.publish("homie/panzer" + (i+1) + "/strip/out/set",
          ch[i][0] + "," + ch[i][1] + "," + ch[i][2] + "," + ch[i][3] + "," + ch[i][4]);
      }
  } */

    }

    public void controlEvent(ControlEvent theEvent) {
        if (theEvent.isController()) {
            if (theEvent.getName().startsWith("output")) {
                int id = theEvent.getId();
                if (id >= 0 && id < output.length) {
                    output[id] = theEvent.getValue() > 0;
                }
            } else if (theEvent.getName().startsWith("effectList")) {
                selectedEffect = (int) theEvent.getValue();
            }
        }
    }

    public static void main(String args[]) {
        PApplet.main("MainFrame");
    }
}
