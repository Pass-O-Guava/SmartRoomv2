# SmartRoomv2
Struts+Rest+Mybatis+[Arduino]

# 1.由start2.jsp中action触发

/**
 * Struts2 "Action" for SmartRoomV2
    - Action: SensorDataAction.java
    - View: start2.jsp
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
    - Request: GET http://localhost:7777/wangye/api/sensor/*
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
