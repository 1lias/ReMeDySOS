#include <Wire.h>
#include <arduinoUtils.h>
#include <arduinoLoRa.h>
#include <SPI.h>

// Node Address
const int NODE_ADDRESS = 8;
// Constant timeout for the receiver function
const int TIMEOUT = 5000;
// Constant to hold the initialization status
int e;

void setup() {
  // put your setup code here, to run once:
  // Open serial communications and wait for port to open:
  Serial.begin(9600);
  startLoRa();
}

void startLoRa(){
  // Power ON the module
  e = sx1272.ON();
  // Set transmission mode and print the result
  e |= sx1272.setMode(4);
  // Set header
  e |= sx1272.setHeaderON();
  // Select frequency channel
  e |= sx1272.setChannel(CH_10_868);
  // Set CRC
  e |= sx1272.setCRC_ON();
  //Select output power (Max, High or Low)
  e |= sx1272.setPower('H');
  // Set the node address and print the result
  e|= sx1272.setNodeAddress(NODE_ADDRESS);
  // Print a success message
  if(e == 0)
    Serial.println(F("SX1272 successfully started."));
  else
    Serial.println(F("SX1272 started failed."));
}

void stopLoRa(){
  // Power OFF the module
  sx1272.OFF();
}

void loop() {
  // put your main code here, to run repeatedly:
  receiveLoRa();
}
/**
 * Function to receive LoRa messages
 */
void receiveLoRa(){
  // Receive any packet with ACK
  e = sx1272.receivePacketTimeoutACK(TIMEOUT);
  // If status 0 (OK)
  if(e==0){
    // Print the status on monitor
    Serial.print(F("Packet received state: "));
    Serial.println(e, DEC);
    // Alloc size on memory for message
    char* my_packet = (char*)malloc(sizeof(char)*sx1272.packet_received.length);
    // Get the message
    for(unsigned int i = 0; i < sx1272.packet_received.length;i++){
      my_packet[i] = (char)sx1272.packet_received.data[i];
    }
    // Print received message
    Serial.print(F("Message: "));
    Serial.println(my_packet);
    // printLoraDetails();
    // Free the memory
    free(my_packet);
    // Clean the serial for next loop
    serialFlush();
  }
}

/**
 * Function used to see the details for a received message
 * Only used for debug
 */
void printLoraDetails(){
  //sx1272.getRSSIpacket();
  //sx1272.getRSSI();
  //sx1272.getSNR();
  sx1272.getMode();
  //Serial.print(F("Packet RSSI reading is: "));
  //Serial.println(sx1272._RSSIpacket, DEC);
  //Serial.print(F("Channel RSSI reading is: "));
  //Serial.println(sx1272._RSSI, DEC);
  //Serial.print(F("SNR reading is: "));
  //Serial.println(sx1272._SNR, DEC);
  Serial.print(F("Bandwidth reading is: "));
  Serial.println(sx1272._bandwidth, DEC);
  Serial.print(F("Rate reading is: "));
  Serial.println(sx1272._codingRate, DEC);
  Serial.print(F("SF reading is: "));
  Serial.println(sx1272._spreadingFactor, DEC);
}

/**
 * Clear the Serial/UART in between strings.
 */
void serialFlush(){
    while(Serial.available() > 0) {
      char t = Serial.read();
    }
}  

