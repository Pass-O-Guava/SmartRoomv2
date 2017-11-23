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
#include <dht11.h>
#include "DFRobot_RGBLCD.h"

// sensor读数httpserver 192.168.1.111/arduino/digital_sensor/

#define DHT11_SENSOR_PIN 7  //温湿传感器
#define LIGHT_SENSOR_PIN 0  //光线传感器  
int light_value;
dht11 DHT;

//int r,g,b;
//int t=0;
DFRobot_RGBLCD lcd(16,2);

void setup() {
    Wifi.begin();
    Wifi.println("REST Server is up");

    Serial.begin(9600);
    Serial.println("Sensor start!");
    lcd.init();
    lcd.setRGB(0,191,255); //deep sky blue
    lcd.setCursor(0, 0);
    lcd.print("Sensor running.");
}

void loop() {
    /*
    r= (abs(sin(3.14*t/180)))*255;
    g= (abs(sin(3.14*(t+60)/180)))*255;
    b= (abs(sin(3.14*(t+120)/180)))*255;
    t=t+3;
    lcd.setRGB(r, g, b);
    */
    
    
    while(Wifi.available()){
      process(Wifi);
    }
  delay(50);

}

void process(WifiData client) {
  // read the command
  String command = client.readStringUntil('/');
  
  if (command == "digital_sensor") {
    sensorCommand(client);
  }
  
/*
  if (command == "digital_light") {
    lightCommand(client);
  }

  if (command == "digital_temperature") {
    temperatureCommand(client);
  }
*/
}

void sensorCommand(WifiData client) {
    char light[5] ;
    char temperature[5];
    //char humidity[5];

    //获取光线传感器读数
    light_value = analogRead(LIGHT_SENSOR_PIN);  //Serial.println(light_value, DEC);
    itostr(light,light_value);

    //获取温湿传感器读数
    DHT.read(DHT11_SENSOR_PIN);
    itostr(temperature, DHT.temperature);
    //itostr(humidity, DHT.humidity);

    char resource[8] = "";
    res2(resource, light, temperature);
    
    // Send feedback to client
    client.println("HTTP/1.1 200 OK\n");
    client.print(resource);
    client.print(EOL);    //char terminator

    lcd.clear();
    lcd.setCursor(0,0);
    lcd.print("LIG: ");
    lcd.print(light_value);
    lcd.setCursor(0,1);
    lcd.print("TEP: ");
    lcd.print(DHT.temperature,1);
}

char* itostr(char *str, int i){
  sprintf(str, "%d", i);
  return str;
}

char* res2(char *resource, char *light, char *temperature){
  strcat(resource, light);
  strcat(resource, ",");
  strcat(resource, temperature);
  return resource;
}

/*
void lightCommand(WifiData client) {
    int value = analogRead(LIGHT_SENSOR_PIN);
    String data;
    data.concat(value); //将数字追加到字符串结尾

    // Send feedback to client
    client.println("HTTP/1.1 200 OK\n");
    client.print(data);
    client.print(EOL);    //char terminator

    lcd.setCursor(0,0);
    lcd.print("LIGHT: ");
    lcd.print(data);
}

void temperatureCommand(WifiData client) {
    int value;
    String data;
    DHT.read(DHT11_SENSOR_PIN);
    value = DHT.temperature;
    data.concat(value); //将数字追加到字符串结尾

    // Send feedback to client
    client.println("HTTP/1.1 200 OK\n");
    client.print(data);
    client.print(EOL);    //char terminator

    lcd.setCursor(0,1);
    lcd.print("TEMPER: ");
    lcd.print(data);
}
*/
