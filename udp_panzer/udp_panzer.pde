import hypermedia.net.*;

int PANZER = 12;
UDP udp;
int ch[][] = new int[PANZER][5];
int col;

void setup() {
  udp = new UDP(this, 6000);
  //frameRate(20);
  
  for(int i = 0; i< PANZER; i++) {
    for(int j = 0; i< 5; i++) {
      ch[i][j] = 0;
    }
  }
  udp.listen(true);
}

int steps = 0;
int panz = 0;
int mode = 0;
float freq = 0.0f;

void receive( byte[] data, String ip, int port ) {
  data = subset(data, 0, data.length);
  String message = new String( data );
  //println( "receive: \""+message+"\" from "+ip+" on port "+port );
  
  int val = 0;
  
  
  if(message.startsWith("/freq")) {
    String temp = message.split(" ")[1];
    temp = temp.trim();
    temp = temp.replaceAll(",","");
    val = (int)(Float.parseFloat(temp)) % 255; 


  }
  //val = (int)freq & 255;
  
  
  if(message.startsWith("/amp")) {
    
    String temp = message.split(" ")[1];
    temp = temp.trim();
    temp = temp.replaceAll(",","");
    
    val = (int)(Float.parseFloat(temp) * 255) % 255;
    //println("val " + val);
    //sendOutAllChannels(val);

    byte[] channels = new byte[5];
    channels[0] = (byte)val;
    channels[1] = 0;
    channels[2] = 0;
    channels[3] = 0;
    channels[4] = 0;
    udp.send(channels,"192.168.79.28", 4210);
  }
  
  
  /*
  if(message.startsWith("/impulse")) {
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

void sendOutAllChannels(int val) {
  for(int i = 0; i< PANZER; i++) {
    //client.publish("homie/panzer" + (i+1) + "/strip/out/set", val + "," + val + "," + val + "," + val + "," + val);
  }
}

void draw() {
  /*
  if(mode == 0) {
      for(int panz = 0; panz< PANZER; panz++) { 
        ch[panz][steps]+=15;    
      }
      if(ch[0][steps] == 255) { steps++; }
  } else if(mode == 1) {
    for(int panz = 0; panz< PANZER; panz++) {
      ch[panz][steps]-=15;
      
    }
    if(ch[0][steps] == 0) { steps++; }
  }*/
/*
  if(steps == 5) {
    steps = 0;
    mode = mode == 0 ? 1 : 0;
  }
 
  for(int i = 0; i< PANZER; i++) { 
    //client.publish("homie/panzer" + (i+1) + "/strip/rgb/set", ((ch[i][0]<<16) + (ch[i][1]<<8) + ch[i][2]) + "");
    client.publish("homie/panzer" + (i+1) + "/strip/out/set", ch[i][0] + "," + ch[i][1] + "," + ch[i][2] + "," + ch[i][3] + "," + ch[i][4]);
    //client.publish("homie/panzer" + (i+1) + "/strip/white1/set", ch[i][3] + "");    
    //client.publish("homie/panzer" + (i+1) + "/strip/white2/set", ch[i][4] + "");
  }
  */
}