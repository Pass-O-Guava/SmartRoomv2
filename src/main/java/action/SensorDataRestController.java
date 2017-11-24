/**
 * 
 * Struts2 "REST Server" for SmartRoomRestV2
    - Action��SensorDataRestController.java
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
	//wy+ ��ȡhttp://localhost:7777/wangye/api/sensor/Light,Humidity,Temperature������Ĳ���
	// ��װ id �������������
	private String id;
	private String [] split; // ��Light,Humidity,Temperature�������зִ洢
	private String light;
	private String humidity;
	private String temperature;
	
	private SensorData sensordata;
	private static Map<String, SensorData> sensordatas = new HashMap<String,SensorData>();
	
	/*
	// SparqlString������get���񷵻�ֵ����
	private static ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(Constant.PREFIX 
			+ "delete { ?Service SmartMeeting:returnServiceValue ?x}"
			+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
			+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
	
	// SparqlString�����´���������
	private static ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
			+ "delete { ?y SmartMeeting:hasValue ?z}" 
			+ "insert { ?y SmartMeeting:hasValue ?StateValue}"
			+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");
	
	
	// SparqlString����ѯ�豸set����
	private static String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
			+ "meeting_room_1806"
			+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
	*/
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	// ��ȡ id ��������ķ���
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

	// ������ id ������ GET ����	/api/sensor
	public HttpHeaders index() { 
		//sensordata = sensorDataDao.getAll(); 
		System.out.println("index()");
		addActionMessage("GET !"); 
		return new DefaultHttpHeaders("index").disableCaching(); 
	} 
	
	
	// ����� id ������GET����	http://localhost:7777/wangye/api/sensor/*,*,*  Ĭ��[1]ΪLightֵ��[2]ΪHumidityֵ��[3]Temperatureֵ
	public HttpHeaders show() throws Exception{
		System.out.println("show()");
		//System.out.println("Light= " + this.getLight() + "\tHumidity= " + this.getHumidity() + "\tTemperature= " + this.getTemperature());
		
		/*
		int light = Integer.parseInt(this.getLight());
		light(light);
		int temperature = Integer.parseInt(this.getTemperature());
		temperature(temperature);
		*/
		
		
		// ���� sensor http_request ������ߡ��¶�
		String result = HttpClient.sendSensorGET("http://192.168.1.111/arduino/digital_sensor");
		
		// ���� sensor http_response ��ȡ���ߡ��¶�
		String [] splitt;
		splitt = result.split(",");
		int light = Integer.parseInt(splitt[0]);
		int tempe = Integer.parseInt(splitt[1]);
		
		// ���ߡ��¶�ֵ����������Ӧ����URL
		light(light);
		temperature(tempe);
		
	
		// 1.����owl������
		// 2.��ѯ����url������
		// 3.�־û�
		/*
		SearchDevice.getModel();

		//1
		// ������д�������ǰ����, д��get_SensorValue HashMap�ṹ
		HashMap<String, String> get_SensorValue = new HashMap<String, String>(); // ��sensor�ڱ����е�name��ʵʱ��ȡ��value����HashMap�ṹ
		//get_SensorValue.put("temperature_sensor", this.getTemperature());
		get_SensorValue.put("light_sensor_room_1806", this.getLight()); //light_sensor_service_room_1806
		
		// ����<SensorService_RDFNode, URL> HashMap�ṹ
		HashMap<RDFNode, String> sensorService_rdf = new HashMap<RDFNode, String>();
		sensorService_rdf = SensorDataService.sensorServiceName2RDFNode(get_SensorValue); // ��ѯlight_sensor�ķ���RDFNode��
		
		// ���������¶�������owl������
		SensorDataService.updateSensorValue2owl(sensorService_rdf, upDateCurrentServiceReturnValue, upDateCurrentSensorState);
		
		//2
		// ���������豸
		SensorDataService.findURLandControlDevice(queryCurrentSmartDevcieSetService);
		
		
		//3
		SensorDataService.writeSensorValue2database("light", "A", "-", Integer.parseInt(this.getLight()));
		SensorDataService.writeSensorValue2database("humidity", "D", "-", Integer.parseInt(this.getHumidity()));
		SensorDataService.writeSensorValue2database("temperature", "D", "-", Integer.parseInt(this.getTemperature()));
		*/
		addActionMessage("GET id !"); 
		return new DefaultHttpHeaders("show");
	}
	
	
	
	// ����� id ������ PUT ����	/api/sensor/1
	public String update() { 
		System.out.println("update()");
		//sensorDataDao.Update(sensordata); 
		addActionMessage("PUT id ��"); 
	    return "success"; 
	}
	
	// ������ id ������ POST ����	/api/sensor
	public HttpHeaders create() {
		System.out.println("create()");
		addActionMessage("POST !"); 
	    //return new DefaultHttpHeaders("success").setLocationId(sd.getId()); 
	    return new DefaultHttpHeaders("success");
	}
	
	// ����� id ������ DELETE ���� /api/sensor/1
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
				System.out.println("���ƣ�" + "\tlight= " + light);
			}
			else{
				HttpClient.sendGET(URL_LED_OFF);
				System.out.println("�صƣ�" + "\tlight= " + light);
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
				System.out.println("С��24�ȣ��յ����ȣ�" + "\ttemperature= " + temperature);
			}
			else if(temperature > 24){
				HttpClient.sendGET(URL_AC_COLD);
				System.out.println("����24�ȣ��յ����䣡" + "\ttemperature= " + temperature);
				}
			else{
				HttpClient.sendGET(URL_AC_OFF);
				System.out.println("��24�ȣ��յ��رգ�" + "\ttemperature= " + temperature);
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
}
