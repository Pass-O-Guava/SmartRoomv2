/*
File: RestServer.ino
This example for the Arduino Uno WiFi shows how to access the digital and analog pins
of the board through REST calls. It demonstrates how you can create your own API using
REST style.

Possible commands created in this shetch:

	"/arduino/digital/13"     -> digitalRead(13)
	"/arduino/digital/13/1"   -> digitalWrite(13, HIGH)
	"/arduino/analog/2/123"   -> analogWrite(2, 123)
	"/arduino/analog/2"       -> analogRead(2)
	"/arduino/mode/13/input"  -> pinMode(13, INPUT)
	"/arduino/mode/13/output" -> pinMode(13, OUTPUT)

This example code is part of the public domain

Note: works only with Arduino Uno WiFi Developer Edition.

http://www.arduino.org/learning/tutorials/boards-tutorials/restserver-and-restclient
*/

#include <Wire.h>
#include <UnoWiFiDevEd.h>
#include "DFRobot_RGBLCD.h"

// 空调控制 192.168.1.112/arduino/digital_ac/* [0:关闭；1:制冷；2:制热]
// 灯光控制 192.168.1.112/arduino/digital_led/* [0:关灯；1:开灯]

#define AC_PIN 7        //小风扇
#define AC_HOT_PIN 12   //redLED
#define AC_COLD_PIN 11  //blueLED
#define LED_PIN 13      //greenLED

//int r,g,b;
//int t=0;
String AC_output = "";
String LED_output = "";

DFRobot_RGBLCD lcd(16,2);  //16 characters and 2 lines of show

void setup() {
    Serial.begin(9600);
    Wifi.begin();
    Wifi.println("REST Server is up");
    pinMode(AC_PIN, OUTPUT); // 风扇：192.168.1.112/arduino/digital/7/1（开启）
  
  Serial.println("Device running !!");
  lcd.init();
  lcd.setRGB(50,205,50); //lime green
  lcd.setCursor(0, 0);
  lcd.print("Device running.");
}

void loop() {
  /*
  r= (abs(sin(3.14*t/180)))*255;          //get R,G,B value
  g= (abs(sin(3.14*(t+60)/180)))*255;
  b= (abs(sin(3.14*(t+120)/180)))*255;
  t=t+3;
  lcd.setRGB(r, g, b);                  //Set R,G,B Value
  */
  
    while(Wifi.available()){
      process(Wifi);
    }
  delay(50);

}

void process(WifiData client) {
  // read the command
  String command = client.readStringUntil('/');
  Serial.print(command);
  Serial.print("\n");
  
  // AC
  if (command == "digital_ac") {
    acCommand(client);
  }

  // LED
  if (command == "digital_led") {
    ledCommand(client);
  }
  
}

void acCommand(WifiData client) {
  int control_flag;

  // Read contol flag
  control_flag = client.parseInt();
  
  switch(control_flag){
    case 0:{ 
      digitalWrite(AC_PIN, LOW);
      digitalWrite(AC_HOT_PIN, LOW);
      digitalWrite(AC_COLD_PIN, LOW);
      AC_output = "AC  off!      ";
      lcd.setRGB(255,255,255); //white
      break;}
    case 1:{ 
      digitalWrite(AC_PIN, HIGH);
      digitalWrite(AC_HOT_PIN, LOW);
      digitalWrite(AC_COLD_PIN, HIGH);
      AC_output = "AC  [Coo Mode]";
      lcd.setRGB(30,144,255); //dodger blue
      break;}
    case 2:{ 
      digitalWrite(AC_PIN, HIGH);
      digitalWrite(AC_HOT_PIN, HIGH);
      digitalWrite(AC_COLD_PIN, LOW);
      AC_output = "AC  [Hot Mode]";
      lcd.setRGB(255,127,80); //coral
      break;}
    default:{ 
      AC_output = "";
      break;}
  }
    
  // Send feedback to client
  client.println("HTTP/1.1 200 OK\n");
  client.print(AC_output);
  client.print(EOL);    //char terminator

  //lcd.clear();
  //lcd.setCursor(0,0);
  //lcd.print(LED_output);
  lcd.setCursor(0,1);
  lcd.print(AC_output);
}

void ledCommand(WifiData client) {
  int control_flag;

  // Read contol flag
  control_flag = client.parseInt();
  
  switch(control_flag){
    case 0:{ 
      digitalWrite(LED_PIN, LOW);
      LED_output = "LED off!        ";
      //lcd.setRGB(176,196,222); //light steel blue
      break;}
    case 1:{ 
      digitalWrite(LED_PIN, HIGH);
      LED_output = "LED [ON]        ";
      //lcd.setRGB(255,165,0); //orange
      break;}
    default:{ 
      LED_output = "";
      break;}
  }
    
  // Send feedback to client
  client.println("HTTP/1.1 200 OK\n");
  client.print(LED_output);
  client.print(EOL);    //char terminator

  //lcd.clear();
  lcd.setCursor(0,0);
  lcd.print(LED_output);
  //lcd.setCursor(0,1);
  //lcd.print(AC_output);
}
