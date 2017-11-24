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
	
	private static final long serialVersionUID = 1L; // Java�����л�������ͨ��������ʱ�ж����serialVersionUID����֤�汾һ���Եģ�Ĭ��Ϊ1L
	private static boolean Executed; //������ִ�б�־
	private String message;
	
	// ��ѯ Sensor_Get_URL��ȫΪget��
	public static String queryCurrentSensorService = Constant.PREFIX + " SELECT ?y ?z " + "WHERE { SmartMeeting:meeting_room_1806"
			+ " SmartMeeting:hasSensor ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceURL ?z.}";

	// ���� Sensor_returnValue
	public static ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
			+ "delete { ?y SmartMeeting:hasValue ?z}" + "insert { ?y SmartMeeting:hasValue ?StateValue}"
			+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");

	// ��ѯ Device_Set_URL
	public static String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:meeting_room_1806"
			+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
			
	// ���� Service_Return_Value
	public static ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(
			Constant.PREFIX + "delete { ?Service SmartMeeting:returnServiceValue ?x}"
			+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
			+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
	
	// ��ѯ ����״̬
	public static String queryEnviromentState = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:" + "meeting_room_1806"
			+ " SmartMeeting:hasSensor ?x. ?y SmartMeeting:measuredBy ?x. ?y SmartMeeting:hasValue ?z}";

	/*
	// ���� Device_State_Value
	public static ParameterizedSparqlString upDateCurrentSmartDeviceState = new ParameterizedSparqlString(Constant.PREFIX
		+ "delete {?x SmartMeeting:hasValue ?y }" + "insert { ?x SmartMeeting:hasValue ?StateValue}"
		+ "where  { ?x SmartMeeting:hasService ?Service.?x SmartMeeting:hasValue ?y}");
	*/
		
	/*
	// ��ѯ Device_Get_URL
	public static String queryCurrentSmartDevcieGetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
		+ "meeting_room_1806"
		+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"get\". ?y SmartMeeting:hasServiceURL ?z.}";
	*/
	
	
	/** 
	 * ��action="sensor_detection_run"ʱ�����������Ʒ���ʼ��
	 **/	
	public String Sensor_Detection_Run() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		/** Detection Run! */
		while (Executed) {
			Count ++;
			System.out.println("\n��[" + Count + "]�μ�⣺");
			
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
		}

		// ���ִ��ʱ��
		long Runtime = System.currentTimeMillis() - Start;
		double Time = new BigDecimal((float) Runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("----------------------------------------");
		System.out.println("���:\t���[" + Count + "]�Σ�\t��ʱ��[" + Time + "]s");
		
		// ������ӳ�
		ClientUtil.clear();
		return SUCCESS;
	}
	
	/**
	 * <֪ʶ���߼����̣�>
	 * 0.owl��ʼֵ��device_state �� off
	 * 1.owl��ѯ��get_sensor_value_URL = "http://192.168.1.111/arduino/digital_sensor" //��һ�β��light_sensor��temperature_sensor����
	 * 2.��ȡ������������HttpRequstִ�� get_sensor_value_URL
	 * 3.owl���£�sensor_value
	 * 4.owl�����ѯ��set_device_state_URL = URL_LED_OFF / URL_LED_ON / URL_AC_OFF / URL_AC_HOT / URL_AC_COLD
	  				URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0"
					URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1"
					URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0"
					URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1"
					URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2"
	 * 5.�����豸״̬��HttpRequstִ�� set_device_state_URL
	 * 6.owl���£�device_state
	 **/	
	public String Sensor_Detection_Run2() throws Exception {
		
		Executed = true;
		int Count = 0;
		long Start = System.currentTimeMillis();
		
		SearchDevice.getModel(); // ���ر���
		
		// ��ѯ Sensor_Get_URL��ȫΪget��		
		ResultSet SensorService = SearchDevice.runQuery(queryCurrentSensorService);
		HashMap<RDFNode, String> sensorService = new HashMap<RDFNode, String>();
		while (SensorService.hasNext()) {
			QuerySolution qs = SensorService.nextSolution();
			sensorService.put(qs.get("y"), qs.get("z").asLiteral().getLexicalForm());
			//System.out.println("[Sensor_get_url]:" + qs.get("z").asLiteral().getLexicalForm());
		}
		
		/** Detection Run! */
		
		while (Executed) {
			Count ++;
			System.out.println("\n��[" + Count + "]�μ�⣺");
			
			// �������sensor����������owl������
			SensorDataService.getSensorValue(sensorService, upDateCurrentServiceReturnValue, upDateCurrentSensorState);
			
			// owl��sensor����value
			//SensorDataService.inferenceEnvi(queryEnviromentState);
						
			// ��������device״̬��������owl
			SensorDataService.setDeviceState(queryCurrentSmartDevcieSetService);
			
			//SearchDevice.writeModel(); // ���±��嵽����
			//System.out.println("���±��壡��");
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
	
	private static String URL_LED_OFF = "http://192.168.1.112/arduino/digital_led/0";
	private static String URL_LED_ON  = "http://192.168.1.112/arduino/digital_led/1";
	
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
	
	private static String URL_AC_OFF  = "http://192.168.1.112/arduino/digital_ac/0";
	private static String URL_AC_COLD = "http://192.168.1.112/arduino/digital_ac/1";
	private static String URL_AC_HOT  = "http://192.168.1.112/arduino/digital_ac/2";
	
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
