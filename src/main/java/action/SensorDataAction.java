/**
 * 
 * Struts2 "Action" for SmartRoomV2
    - Action: SensorDataAction.java
    - View: start2.jsp
    - Config: struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
 * 
 **/
package action;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;










/*
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import owl.Constant;

import owl.Sender;
*/
import com.opensymphony.xwork2.ActionSupport;

import util.ClientUtil;
import util.HttpClient;
import owl.Constant;
import owl.SearchDevice;
import owl.SensorDataService;

public class SensorDataAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L; // Java的序列化机制是通过在运行时判断类的serialVersionUID来验证版本一致性的，默认为1L
	private static boolean Executed; //监测控制执行标志
	private String message;
	
	/** 
	 * 【action="sensor_detection_run"时触发，监测控制服务开始】
	 **/
	
	public String Sensor_Detection_Run() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		//SearchDevice.getModel(); // 加载本体
		
		/** Detection Run! */
		while (Executed) {
			Count ++;
			System.out.println("\n第[" + Count + "]次监测：");
			
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
			
			
			/**
			 * <知识体逻辑流程：>
			 * 0.owl初始值：device_state 置 off
			 * 1.owl查询：get_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor" //可一次查出light_sensor和temperature_sensor读数
			 * 2.获取传感器读数：HttpRequst执行 get_sensor_value_URL
			 * 3.owl更新：sensor_value
			 * 4.owl推理查询：set_device_state_URL = URL_LED_OFF / URL_LED_ON / URL_AC_OFF / URL_AC_HOT / URL_AC_COLD
			  				URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"
							URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1"
							URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0"
							URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1"
							URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2"
			 * 5.控制设备状态：HttpRequst执行 set_device_state_URL
			 * 6.owl更新：device_state
			 **/
			
			
			// SearchDevice.writeModel(); // 更新本体到磁盘
		}

		// 获得执行时间
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("完成:\t监测[" + Count + "]次，\t总时长[" + Time + "]s");
		
		// 清空连接池
		ClientUtil.clear();
		return SUCCESS;
	}
	
	
public String Sensor_Detection_Run2() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		SearchDevice.getModel(); // 加载本体
		
		/**
		 * 初始化SPARQL语句
		 */
		
		/**
		 *  0.owl初始值：device_state 置 0
		 *  更新device初始状态
		 **/
		// 查询设备get服务
		String queryCurrentSmartDevcieGetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"get\". ?y SmartMeeting:hasServiceURL ?z.}";
		// 更新get服务返回值
		ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(
				Constant.PREFIX + "delete { ?Service SmartMeeting:returnServiceValue ?x}"
						+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
						+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
		// 更新设备状态值
		ParameterizedSparqlString upDateCurrentSmartDeviceState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete {?x SmartMeeting:hasValue ?y }" + "insert { ?x SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?x SmartMeeting:hasValue ?y}");
				
		// 查询device get服务
		ResultSet SmartDevcieGetService = SearchDevice.runQuery(queryCurrentSmartDevcieGetService);
		HashMap<RDFNode, String> getService = new HashMap<RDFNode, String>();
		while (SmartDevcieGetService.hasNext()) {
			QuerySolution qs = SmartDevcieGetService.nextSolution();
			getService.put(qs.get("y"), "0"); //所有设备状态初始化0
			System.out.println("[1]:" + qs.get("y"));
		}
		// 更新所有device初始状态为0
		SensorDataService.updateSensorValue2owl(getService, upDateCurrentServiceReturnValue, upDateCurrentSmartDeviceState);
		
		
		/**
		 *  获得所有sensor的url 存在 sensorService里
		 **/
		// 查询传感器服务（全为get）
		String queryCurrentSensorService = Constant.PREFIX + " SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSensor ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceURL ?z.}";
		// 查询sensor get服务 url		
		ResultSet SensorService = SearchDevice.runQuery(queryCurrentSensorService);
		HashMap<RDFNode, String> sensorService = new HashMap<RDFNode, String>();
		while (SensorService.hasNext()) {
			QuerySolution qs = SensorService.nextSolution();
			sensorService.put(qs.get("y"), qs.get("z").asLiteral().getLexicalForm());
		}
		
		
		/**
		 *  循环中会用到的owl查询语句
		 **/
		// 更新传感器读数
		ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete { ?y SmartMeeting:hasValue ?z}" + "insert { ?y SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");
	/*	// 查询环境状态
		String queryEnviromentState = Constant.PREFIX + "SELECT ?y " + "WHERE { SmartMeeting:" + "meeting_room_1806"
				+ " SmartMeeting:hasState ?x. ?x SmartMeeting:hasValue ?y}";
	*/	
		// 查询环境状态
		String queryEnviromentState2 = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:" + "meeting_room_1806"
				+ " SmartMeeting:hasSensor ?x. ?y SmartMeeting:measuredBy ?x. ?y SmartMeeting:hasValue ?z}";

		// 查询设备set服务
		String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
				
		
		/** Detection Run! */
		while (Executed) {
			Count ++;
			System.out.println("\n第[" + Count + "]次监测：");
			

			/**
			 * <知识体逻辑流程：>
			 * 0.owl初始值：device_state 置 off
			 * 1.owl查询：get_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor" //可一次查出light_sensor和temperature_sensor读数
			 * 2.获取传感器读数：HttpRequst执行 get_sensor_value_URL
			 * 3.owl更新：sensor_value
			 * 4.owl推理查询：set_device_state_URL = URL_LED_OFF / URL_LED_ON / URL_AC_OFF / URL_AC_HOT / URL_AC_COLD
			  				URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"
							URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1"
							URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0"
							URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1"
							URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2"
			 * 5.控制设备状态：HttpRequst执行 set_device_state_URL
			 * 6.owl更新：device_state
			 **/
			
			
			// 获得所有sensor读数，并更新owl
			SensorDataService.getSensorValue(sensorService, upDateCurrentServiceReturnValue, upDateCurrentSensorState);
			
			// 推理当前环境状态？?
			//SensorDataService.inferenceEnvi(queryEnviromentState2);
						
			// 控制所有device状态，并更新owl
			SensorDataService.setDeviceState(queryCurrentSmartDevcieSetService);
			
			
			SearchDevice.writeModel(); // 更新本体到磁盘
			System.out.println("更新本体！！");
		}

		// 获得执行时间
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("完成:\t监测[" + Count + "]次，\t总时长[" + Time + "]s");
		
		// 清空连接池
		ClientUtil.clear();
		return SUCCESS;
	}
	
	
	
	/** 
	 * 【固定规则判断：光线】
	 * 规则：“100”为判断阈值，小于“100”开灯，大于等于“100”关灯
	 **/
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	public void light(int light) throws IOException{
		try{
			if(light < 100){
				HttpClient.sendGET(URL_LED_ON);
				System.out.println("  Light=[" + light + "]\t[开灯！]");
			}
			else{
				HttpClient.sendGET(URL_LED_OFF);
				System.out.println("  Light=[" + light + "]\t[关灯！]");
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
	
	/** 
	 * 【固定规则判断：温度】 
	 * 规则：“24”为判断阈值，小于“24”空调加热，等于“24”空调关闭，大于“24”空调制冷
	 **/
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	
	public void temperature(int temperature) throws IOException{
		try{
			if(temperature < 24){
				HttpClient.sendGET(URL_AC_HOT);
				System.out.println("  Tempe=[" + temperature + "]\t<24，[空调加热！]");
			}
			else if(temperature > 24){
				HttpClient.sendGET(URL_AC_COLD);
				System.out.println("  Tempe=[" + temperature + "]\t>24，[空调制冷！]");
				}
			else{
				HttpClient.sendGET(URL_AC_OFF);
				System.out.println("  Tempe=[" + temperature + "]\t=24，[空调关闭！]");
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
	
	/** 
	 * 【action="sensor_detection_stop"时触发，监测控制服务停止】
	 **/
	
	public String Sensor_Detection_Stop() {
		if (Executed = true) {
			Executed = false;
			
			try{
				HttpClient.sendGET(URL_LED_OFF);
				System.out.println("LED:OFF!");
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			try{
				HttpClient.sendGET(URL_AC_OFF);
				System.out.println("AC :OFF!");
			}
			catch(Exception e){
				System.out.println(e);
			}
			
			setMessage("Detection STOP！");
			return SUCCESS;
		} else {
			setMessage("ERROR: Detection terminate failed！");
			return "fail";
		}
	}
	
	
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
