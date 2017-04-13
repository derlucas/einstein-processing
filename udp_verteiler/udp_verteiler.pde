import hypermedia.net.*;
import controlP5.*;

UDP udp;
ControlP5 cp5;
boolean output0 = false;
boolean output1 = false;
boolean output2 = false;
boolean output3 = false;
int port0 = 2001;
int port1 = 2002;
int port2 = 2003;
int port3 = 2004;

void setup() {
  size(400,250);
  background(0);
  
  udp = new UDP(this, 6000);
  udp.listen(true);
  cp5 = new ControlP5(this);
  
  for(int i = 0; i < 4; i++) {
    cp5.addToggle("output" + i).setPosition(10, i * 35).setSize(50,20);  
    cp5.addNumberbox("port" + i).setPosition(100, i * 35).setSize(100,20).setScrollSensitivity(1);
  }

}

void receive( byte[] data, String ip, int port ) {
  data = subset(data, 0, data.length);
  String message = new String( data );
    
  if(output0) udp.send(data, "127.0.0.1", port0);
  if(output1) udp.send(data, "127.0.0.1", port1);
  if(output2) udp.send(data, "127.0.0.1", port2);
  if(output3) udp.send(data, "127.0.0.1", port3);
  
}


void draw() {
  
   
  
}