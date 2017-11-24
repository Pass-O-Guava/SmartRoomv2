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
	
	private static final long serialVersionUID = 1L; // Java�����л�������ͨ��������ʱ�ж����serialVersionUID����֤�汾һ���Եģ�Ĭ��Ϊ1L
	private static boolean Executed; //������ִ�б�־
	private String message;
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"; // For Sensor_Detection_Stop()
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";  // For Sensor_Detection_Stop()
	
	/**
	 * <֪ʶ���߼����̣�>
	 * 0.����owl��rules�ļ�
	 * (0.owl��ʼ����Device_State)
	 * 1.owl��ѯ��Sensor_Get_URL ResultSet
	  				get_light_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor_light" 
					get_temper_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor_temper"
	 * 2.��ȡ������������HttpRequstִ�� Sensor_Get_URL
	 * 3.owl���²�����ServiceReturnValue��Sensor_State
	 * 4.owl��ѯ��Device_Set_URL ResultSet
	  				URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"
					URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1"
					URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0"
					URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1"
					URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2"
	 * 5.�����豸״̬��HttpRequstִ�� Device_Set_URL
	 * (6.owl���£�Device_State)
	 **/
	
	public String Sensor_Detection_Run() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		// ���ر���
		JenaFAO.getModel();
		
		// owl��ѯSensor_Get_URL		
		HashMap<RDFNode, String> sensorService = SensorDataService.SensorGetURL(SparQL.queryCurrentSensorService);
		
		// detection loop run!
		while (Executed) {
			Count ++;
			System.out.println("\n��[" + Count + "]�μ�⣺");
			
			// �������sensor����������owl������
			SensorDataService.getSensorValue(sensorService, SparQL.upDateCurrentServiceReturnValue, SparQL.upDateCurrentSensorState);
							
			// owl��ѯDevice_Set_URL����������device״̬
			SensorDataService.setDeviceState(SparQL.queryCurrentSmartDevcieSetService);
			
			//SearchDevice.writeModel(); // ���±��嵽����
		}

		// ���ִ��ʱ��
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("���:\t���[" + Count + "]�Σ�\t��ʱ��[" + Time + "]s");
		
		return SUCCESS;
	}
	
	
	/** 
	 * ��action="sensor_detection_stop"ʱ�����������Ʒ���ֹͣ��
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
			
			setMessage("Detection STOP��");
			return SUCCESS;
		} else {
			setMessage("ERROR: Detection terminate failed��");
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
