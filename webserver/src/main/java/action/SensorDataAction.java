/**
 * 
 * Struts2 "Action" for SmartRoomV2
    - Action: SensorDataAction.java
    - View: start.jsp
    - Config: struts.xml
    - Service: /owl/SensorDataService.java
    - Model: /pojo
    - DAO: /dao
    - HttpClient: /util/HttpClient.java
 * 
 **/
package action;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.jena.rdf.model.RDFNode;

import com.opensymphony.xwork2.ActionSupport;

import util.HttpClient;
import owl.JenaFAO;
import owl.SensorDataService;
import owl.SparQL;

public class SensorDataAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L; // Java的序列化机制是通过在运行时判断类的serialVersionUID来验证版本一致性的，默认为1L
	private static boolean Executed; //监测控制执行标志
	private String message;
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"; // For Sensor_Detection_Stop()
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";  // For Sensor_Detection_Stop()
	
	/**
	 * <知识体逻辑流程：>
	 * 0.加载owl和rules文件
	 * (0.owl初始化：Device_State)
	 * 1.owl查询：Sensor_Get_URL ResultSet
	  				get_light_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor_light" 
					get_temper_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor_temper"
	 * 2.获取传感器读数：HttpRequst执行 Sensor_Get_URL
	 * 3.owl更新并推理：ServiceReturnValue、Sensor_State
	 * 4.owl查询：Device_Set_URL ResultSet
	  				URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"
					URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1"
					URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0"
					URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1"
					URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2"
	 * 5.控制设备状态：HttpRequst执行 Device_Set_URL
	 * (6.owl更新：Device_State)
	 **/
	
	public String Sensor_Detection_Run() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		// 加载本体
		JenaFAO.getModel();
		
		// owl查询Sensor_Get_URL		
		HashMap<RDFNode, String> sensorService = SensorDataService.SensorGetURL(SparQL.queryCurrentSensorService);
		
		// detection loop run!
		while (Executed) {
			Count ++;
			System.out.println("\n第[" + Count + "]次监测：");
			
			// 获得所有sensor读数，更新owl并推理
			SensorDataService.getSensorValue(sensorService, SparQL.upDateCurrentServiceReturnValue, SparQL.upDateCurrentSensorState);
							
			// owl查询Device_Set_URL，控制所有device状态
			SensorDataService.setDeviceState(SparQL.queryCurrentSmartDevcieSetService);
			
			//SearchDevice.writeModel(); // 更新本体到磁盘
		}

		// 获得执行时间
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("完成:\t监测[" + Count + "]次，\t总时长[" + Time + "]s");
		
		return SUCCESS;
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
