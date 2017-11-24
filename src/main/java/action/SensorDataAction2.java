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

import com.opensymphony.xwork2.ActionSupport;

import util.HttpClient;


public class SensorDataAction2 extends ActionSupport {
	
	private static final long serialVersionUID = 1L; // Java的序列化机制是通过在运行时判断类的serialVersionUID来验证版本一致性的，默认为1L
	private static boolean Executed; //监测控制执行标志
	private String message;
	
	private static String GET_LIGHT_SENSOR = "http://192.168.1.111/arduino/digital_sensor_light"; // 获得光线传感器读数URL
	private static String GET_LIGHT_TEMPER = "http://192.168.1.111/arduino/digital_sensor_temper"; // 获得温度传感器读数URL
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	
	
	/** 
	 * 【action="sensor_detection_run"时触发，监测控制服务开始】
	 **/
	
	public String Sensor_Detection_Run2() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		/** Detection Run! */
		while (Executed) {
			Count ++;
			System.out.println("\n第[" + Count + "]次监测：");
			
			int light = Integer.parseInt(HttpClient.sendSensorGET(GET_LIGHT_SENSOR));
			int tempe = Integer.parseInt(HttpClient.sendSensorGET(GET_LIGHT_TEMPER));
			
			// 光线、温度值推理，发送相应控制URL
			light(light);
			temperature(tempe);
			
			/** 只用一个api同时获取两个传感器的值 <RestServerfor_Sensor_20171114.ino>
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
			*/	
		}

		// 获得执行时间
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("完成:\t监测[" + Count + "]次，\t总时长[" + Time + "]s");
		
		return SUCCESS;
	}
	
	
	/** 
	 * 【固定规则判断：光线】
	 * 规则：“100”为判断阈值，小于“100”开灯，大于等于“100”关灯
	 **/
	
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
	
	public String Sensor_Detection_Stop2() {
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
