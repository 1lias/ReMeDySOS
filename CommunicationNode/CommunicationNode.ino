#include <Wire.h>
#include <arduinoUtils.h>
#include <arduinoLoRa.h>
#include <SPI.h>

// Variable with the final message
String finalMessage = "";
// Constant with the Node Address
const int NODE_ADDRESS = 3;
// Constant with the string to append on the message
const String NODE_ADDRESS_STRING = ";Node:" + String(NODE_ADDRESS) + ";";
// Constant timeout for the receiver function
const int TIMEOUT = 5000;
// List of Node Addresses that can receive the message
int nodesToSend[] = {8,6};
// Status for the LoRa initialization
int e;

void setup() {
  // opens serial port, sets data rate to 9600 bps
  Serial.begin(9600);     
  setupLoraConfiguration();
  finalMessage.reserve(130);
  Serial.println(F("Setup done"));
}

void setupLoraConfiguration(){
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
        Serial.println(F("SX1272 successfully configured."));
      else
        Serial.println(F("SX1272 initialization failed."));
}

/**
 * Loop function
 */
void loop() {
  // First try to receive from another node
  receiveLoRa();
  // Then try to receive from App Mobile
  receiveAndSendLoRa();
}

/**
 * Function to receive message from mobile and send to LoRa
 */
 void receiveAndSendLoRa(){
  //while there is data on the UART we read it
  while(Serial.available() >0){
    // read the incoming string    
    String inputString=Serial.readString();
    //checks if the input on the UART is the SOS or not
    if(inputString.indexOf("*SOS*") != -1){
      String message = getLoraMessage(inputString);
      sendMessageLora(message);
    }
    serialFlush();
  }//end while
 }

 /**
 * Called whenever a SOS message is received.
 */
String getLoraMessage(String msg) {
  // Remove any string before *SOS*
  msg.remove(0,msg.indexOf("*SOS*"));
  // Remove any char on the message not necessary
  msg.remove(msg.lastIndexOf(';'));
  return msg + NODE_ADDRESS_STRING;
 }

/**
 * Clear the Serial/UART in between strings.
 */
void serialFlush(){
  while(Serial.available() > 0) {
    char t = Serial.read();
  }
}

/**
 * Function to receive LoRa messages
 */
void receiveLoRa(){
  // Receive the message with ACK
  e = sx1272.receivePacketTimeoutACK(TIMEOUT);
  if(e==0){
    // If status 0 (OK), print the received message
    Serial.print(F("Packet received state: "));
    Serial.println(e, DEC);
    // Allocate the memory for received message
    char* my_packet = (char*)malloc(sizeof(char)*sx1272.packet_received.length);
    // Put the data on the array
    for(unsigned int i = 0; i < sx1272.packet_received.length;i++){
      my_packet[i] = (char)sx1272.packet_received.data[i];
    }
    // Redirect the message
    sendMessage(my_packet);
    // Free the memory
    free(my_packet);
    serialFlush();
  }
}

/**
 * Function to Send the Message
 */
void sendMessageLora(String messageToLora){
  // Put the message on the allocated memory
  finalMessage = messageToLora;
  // Allocate the memory for the packet
  char* my_packet = (char*)malloc(sizeof(char)*finalMessage.length());
  // Transform the String to array
  finalMessage.toCharArray(my_packet, finalMessage.length()+1);
  // Send the message
  sendMessage(my_packet);
  // Free and clean memory
  free(my_packet);
  finalMessage = "";
}

/**
 * Function to send message
 * Also used to redirect the message
 */
void sendMessage(char* my_packet){
  // Print the message
  Serial.print(F("My packet:"));
  Serial.print(my_packet);
  Serial.println(F("Sending packet..."));
  // Try to send the message using the node list
  for(int i = 0; i < (sizeof(nodesToSend)/sizeof(int)); i++){
    e = sx1272.sendPacketTimeoutACK(nodesToSend[i],my_packet);
    // If the status is 0 (OK), success
    if(e == 0)
      break;
  }
  // Print status
  Serial.print(F("Packet sent, state: "));
  Serial.println(e, DEC);
  Serial.println(F("Packet sent!"));
}  

