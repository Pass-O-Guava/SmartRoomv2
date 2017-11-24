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
	
	private static final long serialVersionUID = 1L; // Java�����л�������ͨ��������ʱ�ж����serialVersionUID����֤�汾һ���Եģ�Ĭ��Ϊ1L
	private static boolean Executed; //������ִ�б�־
	private String message;
	
	private static String GET_LIGHT_SENSOR = "http://192.168.1.111/arduino/digital_sensor_light"; // ��ù��ߴ���������URL
	private static String GET_LIGHT_TEMPER = "http://192.168.1.111/arduino/digital_sensor_temper"; // ����¶ȴ���������URL
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	
	
	/** 
	 * ��action="sensor_detection_run"ʱ�����������Ʒ���ʼ��
	 **/
	
	public String Sensor_Detection_Run2() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		/** Detection Run! */
		while (Executed) {
			Count ++;
			System.out.println("\n��[" + Count + "]�μ�⣺");
			
			int light = Integer.parseInt(HttpClient.sendSensorGET(GET_LIGHT_SENSOR));
			int tempe = Integer.parseInt(HttpClient.sendSensorGET(GET_LIGHT_TEMPER));
			
			// ���ߡ��¶�ֵ����������Ӧ����URL
			light(light);
			temperature(tempe);
			
			/** ֻ��һ��apiͬʱ��ȡ������������ֵ <RestServerfor_Sensor_20171114.ino>
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
			*/	
		}

		// ���ִ��ʱ��
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("���:\t���[" + Count + "]�Σ�\t��ʱ��[" + Time + "]s");
		
		return SUCCESS;
	}
	
	
	/** 
	 * ���̶������жϣ����ߡ�
	 * ���򣺡�100��Ϊ�ж���ֵ��С�ڡ�100�����ƣ����ڵ��ڡ�100���ص�
	 **/
	
	public void light(int light) throws IOException{
		try{
			if(light < 100){
				HttpClient.sendGET(URL_LED_ON);
				System.out.println("  Light=[" + light + "]\t[���ƣ�]");
			}
			else{
				HttpClient.sendGET(URL_LED_OFF);
				System.out.println("  Light=[" + light + "]\t[�صƣ�]");
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
	
	/** 
	 * ���̶������жϣ��¶ȡ� 
	 * ���򣺡�24��Ϊ�ж���ֵ��С�ڡ�24���յ����ȣ����ڡ�24���յ��رգ����ڡ�24���յ�����
	 **/
	
	public void temperature(int temperature) throws IOException{
		try{
			if(temperature < 24){
				HttpClient.sendGET(URL_AC_HOT);
				System.out.println("  Tempe=[" + temperature + "]\t<24��[�յ����ȣ�]");
			}
			else if(temperature > 24){
				HttpClient.sendGET(URL_AC_COLD);
				System.out.println("  Tempe=[" + temperature + "]\t>24��[�յ����䣡]");
				}
			else{
				HttpClient.sendGET(URL_AC_OFF);
				System.out.println("  Tempe=[" + temperature + "]\t=24��[�յ��رգ�]");
			}
		}
		catch(Exception e){
            System.out.println(e);
        }
	}
	
	
	
	/** 
	 * ��action="sensor_detection_stop"ʱ�����������Ʒ���ֹͣ��
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
