/*
File: RestClient.ino
This example makes an HTTP request after 10 seconds and shows the result both in
serial monitor and in the wifi console of the Arduino Uno WiFi.

Note: works only with Arduino Uno WiFi Developer Edition.

http://www.arduino.org/learning/tutorials/boards-tutorials/restserver-and-restclient
*/

#include <Wire.h>
#include <UnoWiFiDevEd.h>
#include <dht11.h>
#include "DFRobot_RGBLCD.h"

const char* connector = "rest";
const char* server = "192.168.1.110"; /*const uint16_t port = 8080;*/
const char* method = "GET";
//const char* resource = "/wangye/api/sensor/5,5,5";

#define DHT11_SENSOR_PIN 7
#define LIGHT_SENSOR_PIN 0
#define CYC 1000 //上传周期

int light_value;
dht11 DHT;
//int r,g,b;
//int t=0;
DFRobot_RGBLCD lcd(16,2);  //16 characters and 2 lines of show


/*
 *
*/
void setup() {
  Serial.begin(9600);
  
  Ciao.begin();
  Serial.println("Ciao start !!");
  
  lcd.init();
  lcd.setCursor(0, 0);
  lcd.print("Sensor running !!");
  
  delay(5000);
}


/*
 *
*/
void loop() {
  /*
  //lcd
  r= (abs(sin(3.14*t/180)))*255;          //get R,G,B value
  g= (abs(sin(3.14*(t+60)/180)))*255;
  b= (abs(sin(3.14*(t+120)/180)))*255;
  t=t+3;
  lcd.setRGB(r, g, b);                  //Set R,G,B Value
  */
  char light[5] ;
  char humidity[5];
  char temperature[5];
  
  //light-sensor
  light_value = analogRead(LIGHT_SENSOR_PIN);  //Serial.println(light_value, DEC);
  itostr(light,light_value);
  //Serial.print("LIGHT: \t");
  //Serial.print(light_value);
  

  //temperature-sensor
  DHT.read(DHT11_SENSOR_PIN);
  //itostr(humidity, DHT.humidity);
  itostr(temperature, DHT.temperature);
  /*
  Serial.print("\nDHT11: \t");
  Serial.print(DHT.humidity,1);
  Serial.print(",\t");
  Serial.println(DHT.temperature,1);
  */
  
  
  char resourcewy[50] = "/wangye/api/sensor/";
  //res(resourcewy, light, humidity, temperature);
  //res2(resourcewy, light, temperature);
  strcat(resourcewy, light);
  Serial.println(resourcewy); 
  
  //rest-request
  Serial.println("Ciao.write...");
  Ciao.write(connector, server, resourcewy, method);
  Serial.println("Ciao.write done！");

  lcd.clear();
  lcd.setCursor(0,0);
  lcd.print("LIGHT: ");
  lcd.print(light_value);
  lcd.setCursor(0,1);
  lcd.print("DHT11: ");
  lcd.print(DHT.temperature,1);

  delay(CYC);
  /*doRequestt(connector, server, port, resource, method);*/
}

char* itostr(char *str, int i){
  sprintf(str, "%d", i);
  return str;
}

char* res(char *resource, char *light, char *humidity, char *temperature){
  strcat(resource, light);
  strcat(resource, ",");
  strcat(resource, humidity);
  strcat(resource, ",");
  strcat(resource, temperature);
  return resource;
}

char* res2(char *resource, char *light, char *temperature){
  strcat(resource, light);
  strcat(resource, ",");
  strcat(resource, temperature);
  return resource;
}

/*
void doRequest(const char* connector, const char* server, const char* resource, const char* method){
	CiaoData data = Ciao.write(connector, server, resource, method);
	if (!data.isEmpty()){
		Ciao.println( "State: " + String (data.get(1)) );
		Ciao.println( "Response: " + String (data.get(2)) );
		Serial.println( "State: " + String (data.get(1)) );
		Serial.println( "Response: " + String (data.get(2)) );
	}
	else{
		Ciao.println ("Write Error");
		Serial.println ("Write Error");
	}
}
*/
