/**
 * 
 * Struts2 "REST Server" for SmartRoomRestV2
    - Action：SensorDataRestController.java
    - Request: GET http://localhost:7777/wangye/api/sensor/*
    - Config:struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
 * 
 **/
package action;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import pojo.SensorData;
import util.HttpClient;

//@SuppressWarnings("serial")
public class SensorDataRestController extends ActionSupport implements ModelDriven<Object>{
	
	private static final long serialVersionUID = 1L;
	//wy+ 获取http://localhost:7777/wangye/api/sensor/Light,Humidity,Temperature中请求的参数
	// 封装 id 请求参数的属性
	private String id;
	private String [] split; // 对Light,Humidity,Temperature按逗号切分存储
	private String light;
	private String humidity;
	private String temperature;
	
	private SensorData sensordata;
	private static Map<String, SensorData> sensordatas = new HashMap<String,SensorData>();
	
	/*
	// SparqlString：更新get服务返回值？？
	private static ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(Constant.PREFIX 
			+ "delete { ?Service SmartMeeting:returnServiceValue ?x}"
			+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
			+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
	
	// SparqlString：更新传感器读数
	private static ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
			+ "delete { ?y SmartMeeting:hasValue ?z}" 
			+ "insert { ?y SmartMeeting:hasValue ?StateValue}"
			+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");
	
	
	// SparqlString：查询设备set服务
	private static String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
			+ "meeting_room_1806"
			+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
	*/
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	// 获取 id 请求参数的方法
	public String getId() {
		//System.out.println("get id= " + id);
		return id;
	}
	
	public void setId(String id) {
		//System.out.println("set id= " + id);
		this.id = id;
		split = id.split(",");
	}
	
	public String getLight(){
		this.light =  split[0];
		return light;
	}
	
	public String getHumidity(){
		this.humidity =  split[1];
		return humidity;
	}
	
	public String getTemperature(){
		this.temperature =  split[2];
		return temperature;
	}

	// 处理不带 id 参数的 GET 请求	/api/sensor
	public HttpHeaders index() { 
		//sensordata = sensorDataDao.getAll(); 
		System.out.println("index()");
		addActionMessage("GET !"); 
		return new DefaultHttpHeaders("index").disableCaching(); 
	} 
	
	
	// 处理带 id 参数的GET请求	http://localhost:7777/wangye/api/sensor/*,*,*  默认[1]为Light值，[2]为Humidity值，[3]Temperature值
	public HttpHeaders show() throws Exception{
		System.out.println("show()");
		//System.out.println("Light= " + this.getLight() + "\tHumidity= " + this.getHumidity() + "\tTemperature= " + this.getTemperature());
		
		/*
		int light = Integer.parseInt(this.getLight());
		light(light);
		int temperature = Integer.parseInt(this.getTemperature());
		temperature(temperature);
		*/
		
		
		// 发送 sensor http_request 请求光线、温度
		String result = HttpClient.sendSensorGET("http://192.168.1.111/arduino/digital_sensor");
		
		// 返回 sensor http_response 获取光线、温度
		String [] splitt;
		splitt = result.split(",");
		int light = Integer.parseInt(splitt[0]);
		int tempe = Integer.parseInt(splitt[1]);
		
		// 光线、温度值推理，发送相应控制URL
		light(light);
		temperature(tempe);
		
	
		// 1.更新owl并推理
		// 2.查询控制url并发送
		// 3.持久化
		/*
		SearchDevice.getModel();

		//1
		// 获得所有传感器当前读数, 写入get_SensorValue HashMap结构
		HashMap<String, String> get_SensorValue = new HashMap<String, String>(); // 将sensor在本体中的name和实时获取的value存入HashMap结构
		//get_SensorValue.put("temperature_sensor", this.getTemperature());
		get_SensorValue.put("light_sensor_room_1806", this.getLight()); //light_sensor_service_room_1806
		
		// 构建<SensorService_RDFNode, URL> HashMap结构
		HashMap<RDFNode, String> sensorService_rdf = new HashMap<RDFNode, String>();
		sensorService_rdf = SensorDataService.sensorServiceName2RDFNode(get_SensorValue); // 查询light_sensor的服务（RDFNode）
		
		// 传感器最新读数更新owl并推理
		SensorDataService.updateSensorValue2owl(sensorService_rdf, upDateCurrentServiceReturnValue, upDateCurrentSensorState);
		
		//2
		// 操作所有设备
		SensorDataService.findURLandControlDevice(queryCurrentSmartDevcieSetService);
		
		
		//3
		SensorDataService.writeSensorValue2database("light", "A", "-", Integer.parseInt(this.getLight()));
		SensorDataService.writeSensorValue2database("humidity", "D", "-", Integer.parseInt(this.getHumidity()));
		SensorDataService.writeSensorValue2database("temperature", "D", "-", Integer.parseInt(this.getTemperature()));
		*/
		addActionMessage("GET id !"); 
		return new DefaultHttpHeaders("show");
	}
	
	
	
	// 处理带 id 参数的 PUT 请求	/api/sensor/1
	public String update() { 
		System.out.println("update()");
		//sensorDataDao.Update(sensordata); 
		addActionMessage("PUT id ！"); 
	    return "success"; 
	}
	
	// 处理不带 id 参数的 POST 请求	/api/sensor
	public HttpHeaders create() {
		System.out.println("create()");
		addActionMessage("POST !"); 
	    //return new DefaultHttpHeaders("success").setLocationId(sd.getId()); 
	    return new DefaultHttpHeaders("success");
	}
	
	// 处理带 id 参数的 DELETE 请求 /api/sensor/1
	public String destroy() {
		//sensorDataDao.remove(id); 
		System.out.println("destroy()");
		addActionMessage("DELETE id !"); 
	    return "success"; 
	    }  

	
	public Object getModel() {
		if(sensordatas == null) {
			return sensordata;	
		} else {
			return sensordatas;
		}
	}
	
	
	public void light(int light) throws IOException{
		try{
			if(light < 100){
				HttpClient.sendGET(URL_LED_ON);
				System.out.println("开灯！" + "\tlight= " + light);
			}
			else{
				HttpClient.sendGET(URL_LED_OFF);
				System.out.println("关灯！" + "\tlight= " + light);
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	public void temperature(int temperature) throws IOException{
		try{
			if(temperature < 24){
				HttpClient.sendGET(URL_AC_HOT);
				System.out.println("小于24度，空调加热！" + "\ttemperature= " + temperature);
			}
			else if(temperature > 24){
				HttpClient.sendGET(URL_AC_COLD);
				System.out.println("大于24度，空调制冷！" + "\ttemperature= " + temperature);
				}
			else{
				HttpClient.sendGET(URL_AC_OFF);
				System.out.println("等24度，空调关闭！" + "\ttemperature= " + temperature);
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
}
