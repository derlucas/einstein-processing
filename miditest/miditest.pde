import themidibus.*; //Import the library

MidiBus myBus; // The MidiBus

void setup() {
  size(150, 150);
  background(0);
  MidiBus.list();
  
  myBus = new MidiBus(this, 0, 1); // Create a new MidiBus object
  
}

void mousePressed() {
   for (int i = 36; i < 52; i++) {
                    myBus.sendNoteOff(9, i, 10);
                }
                
                delay(1000);
  for (int i = 36; i < 52; i++) {
                    myBus.sendNoteOn(9, i, 10);
                }
  
  //myBus.sendNoteOn(9, 38, 10);
  println("sende");
}

void draw() {
  
}

void noteOn(int channel, int pitch, int velocity) {
  // Receive a noteOn
  println();
  println("Note On:");
  println("--------");
  println("Channel:"+channel);
  println("Pitch:"+pitch);
  println("Velocity:"+velocity);
  
  
  myBus.sendNoteOn(channel, pitch, 10);
}

void noteOff(int channel, int pitch, int velocity) {
  // Receive a noteOff
  println();
  println("Note Off:");
  println("--------");
  println("Channel:"+channel);
  println("Pitch:"+pitch);
  println("Velocity:"+velocity);
}

void controllerChange(int channel, int number, int value) {
  // Receive a controllerChange
  println();
  println("Controller Change:");
  println("--------");
  println("Channel:"+channel);
  println("Number:"+number);
  println("Value:"+value);
}

void delay(int time) {
  int current = millis();
  while (millis () < current+time) Thread.yield();
}