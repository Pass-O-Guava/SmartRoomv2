# SmartRoomv2
Struts+Rest+Jena+Mybatis+[Arduino]

# 1.由start.jsp中action触发

/**
 * Struts2 "Action" for SmartRoomV2
    - Action: SensorDataAction.java
    - View: http://localhost:7777/xxx/start.jsp
    - Config: struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
    
 **/
 
 
 # 2.由HttpRequest_URL触发
 
 /**
 * Struts2 "REST Server" for SmartRoomRestV2
    - Action：SensorDataRestController.java
    - Request: GET http://localhost:7777/xxx/api/sensor/light,humidity,temperature
    - Config:struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
    
 **/

# 3.Arduino UNO WiFi x2

/**
* Arduino_1 for Sensor(LightSensor & DHT11)
    - RestServerfor_Sensor_owl_20171122
    - RestServerfor_Sensor_20171114
    - RestClientfor_Sensor_20171107
* Arduino_2 for Device(LEDs & Motor)
    - RestServerfor_Device_20171113
    
**/

# 4.Environment Configuration 
- Arduino_1 for Sensor ip: 192.168.1.111
- Arduino_2 for Device ip: 192.168.1.112
- Rest Server ip: 192.168.1.110

![](https://github.com/Pass-O-Guava/SmartRoomv2/blob/master/pic/Arduino.jpg)
