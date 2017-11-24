/**
 * 
 * Struts2 "REST Server" for SmartRoomRestV2
    - Action：SensorDataRestController.java
    - Request: GET http://localhost:7777/xxx/api/sensor/Light,Humidity,Temperature
    - Config:struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
    - Arduino：RestClientfor_Sensor_20171107.ino
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

public class SensorDataRestController extends ActionSupport implements ModelDriven<Object>{
	
	private static final long serialVersionUID = 1L;
	
	// 封装 id 请求参数的属性
	private String id;
	private String [] split; // 对Light,Humidity,Temperature按逗号切分存储
	private String light;
	private String humidity;
	private String temperature;
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	private SensorData sensordata;
	private static Map<String, SensorData> sensordatas = new HashMap<String,SensorData>();
	
	public Object getModel() {
		if(sensordatas == null) {
			return sensordata;	
		} else {
			return sensordatas;
		}
	}
	
	
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
		
		System.out.println("Light= " + this.getLight() + "\tHumidity= " + this.getHumidity() + "\tTemperature= " + this.getTemperature());
		
		// 业务逻辑
		light(Integer.parseInt(this.getLight()));
		temperature(Integer.parseInt(this.getTemperature()));
	
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
	
	// light业务处理
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
	
	// temperature业务处理
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
