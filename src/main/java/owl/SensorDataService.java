/**
 * 
 * Struts2 "Service" for SmartRoomV2
    - Action: SensorDataAction.java \ SensorDataRestController.java
    - DAO: /dao
 * 
 **/
package owl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateRequest;

import dao.SensorDataDao;
import pojo.SensorData;
import util.HttpClient;

public class SensorDataService {

	/*
	// ��ʼ��HTTP��ض���
	private static List<Cookie> cookies = null;
	private static CloseableHttpResponse response = null;

	// ��ʼ�����ֱ���
	private static String pin_num = null;
	private static String pin_type = null;
	private static int pin_value = 0;
	private static String CurrentURL = null;
	private static RDFNode CurrentService = null;
	//private static String CurrentService = null;
	private static String CurrentStateValue = null;
	 */
	
	/**
	 * wy:��ѯlight_sensor�ķ���RDFNode��, sensorService_name to SensorService_RDFNode
	 **/
	public static HashMap<RDFNode, String> sensorServiceName2RDFNode(HashMap<String, String> get_SensorValue){
		
		HashMap<RDFNode, String> sensorService_rdf = new HashMap<RDFNode, String>();
		// ����get_SensorValue,ͨ����ѯowl
		Iterator<Entry<String, String>> it = get_SensorValue.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> entry = it.next();
			String q_xxxSensorService = Constant.PREFIX + " SELECT ?x WHERE { SmartMeeting:" + entry.getKey() + " SmartMeeting:hasService ?x.}";
			ResultSet xxxSensorService = SearchDevice.runQuery(q_xxxSensorService);
			if (xxxSensorService.hasNext()){
				QuerySolution qs = xxxSensorService.nextSolution(); //��ѯlight_sensor��temperature_sensor�ڱ����ж�Ӧ�����RDFNode
				RDFNode rdf_xxxSensor = qs.get("x"); 
				sensorService_rdf.put(rdf_xxxSensor, entry.getValue()); //
				//System.out.println("rdf_xxxSensor=" + rdf_xxxSensor + "\tgetValue=" + entry.getValue()); /* wy */
			}
			else{
				System.out.println("Error: owl�в鲻��" + entry.getKey() + "�ķ���");
			}
		}
		return sensorService_rdf;
	}
	
	/**
	 * wy:����sensorData�־û����ݽṹ
	 **/
	public static SensorData makeSensorData(String name, String type, String boardip, Integer value, Date time){
		SensorData sd = new SensorData();
		sd.setName(name);
		sd.setType(type);
		sd.setBoardip(boardip);
		sd.setValue(value);
		sd.setTime(time);
		return sd;
	}
	
	/**
	 * wy:���������¶���д�����ݿ�
	 **/
	public static void writeSensorValue2database(String sensor_name, String sensor_type, String boardip, Integer sensor_value){
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		//Arduino ad = ArduinoDao.selectByVersion("sensor"); // ��version=sensor��arduino�����������Ϣ����A0��light_sensor,D7��dht11_sensor
		//SensorData sd = makeSensorData(ad.get("A0"), "A", ad.getIp(), Integer.parseInt(this.getLight()), timeStamp); // ����light_sensor insert���ݽṹ
		SensorData sd;
		sd = makeSensorData(sensor_name, sensor_type, boardip, sensor_value, timeStamp);
		SensorDataDao.insert(sd); // ���ݿ�־û�
		System.out.println("���������¶���д��database��" + sd.getName() + "\t" + sd.getType() + "\t" + sd.getBoardip() + "\t" + sd.getValue() + "\t" + sd.getTime());
	}
	
	
	/**
	 * wy:���������¶�������owl
	 **/
	public static void updateSensorValue2owl(HashMap<RDFNode, String> sensorService_rdf,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState){

		// ����Service���÷���
		Iterator<Entry<RDFNode, String>> it = sensorService_rdf.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> entry = it.next();
			RDFNode CurrentService = entry.getKey();
			//int pin_value = Integer.parseInt(entry.getValue());
			String pin_value = entry.getValue();
			
			//System.out.println(" ------[002]: getkey + getvalue" + CurrentService + "\t" + pin_value); ///////
			
			// ����owl:get���񷵻�ֵ
			upDateCurrentServiceReturnValue.setParam("Service", CurrentService);
			upDateCurrentServiceReturnValue.setLiteral("StateValue", pin_value);
			UpdateRequest usrv = upDateCurrentServiceReturnValue.asUpdate();
			SearchDevice.UpdateModel(usrv);
			
			// ����owl:����������
			upDateCurrentSensorState.setParam("Service", CurrentService);
			upDateCurrentSensorState.setLiteral("StateValue", pin_value);
			UpdateRequest uss = upDateCurrentSensorState.asUpdate();
			SearchDevice.UpdateModel(uss);
		}
		System.out.println(" [device state init.]");
	}

	
	/**
	 * owl��ѯ�豸����url������
	 **/
	public static void findURLandControlDevice(String queryCurrentSmartDevcieSetService)
			throws ClientProtocolException, IOException {
		// owl�в�ѯ�豸��Ҫ��ȡ�Ŀ���url��ResultSet
		ResultSet SmartDevcieSetService = SearchDevice.runQuery(queryCurrentSmartDevcieSetService);
	
		// ����ResultSet������Http��������
		while (SmartDevcieSetService.hasNext()) {
			QuerySolution q = SmartDevcieSetService.next();
			String CurrentURL = q.get("z").asLiteral().getLexicalForm();
			//System.out.println("currentURL = " + CurrentURL);
			HttpClient.sendGET(CurrentURL);
			System.out.println("�豸��������ɣ�");
		}
	}
	
	
	/**
	 * �������sensor����������owl <20171122>
	 **/
	public static void getSensorValue(HashMap<RDFNode, String> sensorService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState)
			throws ClientProtocolException, IOException {
		
		RDFNode CurrentService; // SensorService RDF����
		String CurrentURL;		// SensorGetURL
		String pin_value;			// SensorValue
		
		Iterator<Entry<RDFNode, String>> it = sensorService.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> CurrentNode = it.next();
			CurrentService = CurrentNode.getKey();
			CurrentURL = CurrentNode.getValue();
			//String result = HttpClient.sendSensorGET("http://192.168.1.111/arduino/digital_sensor");
			//System.out.println("  [CurrentURL]:" + CurrentURL);
			//pin_value = Integer.parseInt(HttpClient.sendSensorGET(CurrentURL));
			pin_value = HttpClient.sendSensorGET(CurrentURL);
			
			// ����get���񷵻�ֵ
			upDateCurrentServiceReturnValue.setParam("Service", CurrentService);
			upDateCurrentServiceReturnValue.setLiteral("StateValue", pin_value);
			UpdateRequest usrv = upDateCurrentServiceReturnValue.asUpdate();
			SearchDevice.UpdateModel(usrv);
			
			// ���´���������
			upDateCurrentSensorState.setParam("Service", CurrentService);
			upDateCurrentSensorState.setLiteral("StateValue", pin_value);
			UpdateRequest uss = upDateCurrentSensorState.asUpdate();
			SearchDevice.UpdateModel(uss);
			
			//System.out.println("  [��ѯ����������]��" + CurrentService + "\t" + pin_value);
		}
		System.out.println("  [���Sensor����]��");
	}
	
	/**
	 * ��������device״̬ <20171122>
	 **/
	public static void inferenceEnvi(String queryEnviromentState){
		Iterator<QuerySolution> Envi = SearchDevice.runQuery(queryEnviromentState);
		while (Envi.hasNext()){
			QuerySolution q = Envi.next();
			System.out.println("  [owl��sensor����value]��" + q.get("y") + "\t" + q.get("z"));
		}
	}
	
	/**
	 * ��������device״̬ ������owl<20171122>
	 **/
	public static void setDeviceState(String queryCurrentSmartDevcieSetService)
			throws ClientProtocolException, IOException {
		
		String CurrentURL;	// DeviceSetURL
		String device_state;
		//HashMap<RDFNode, String> getService = new HashMap<RDFNode, String>();
		
		ResultSet SmartDevcieSetService = SearchDevice.runQuery(queryCurrentSmartDevcieSetService);		
		while (SmartDevcieSetService.hasNext()) {
			QuerySolution qs = SmartDevcieSetService.nextSolution();
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
			device_state = HttpClient.sendSensorGET(CurrentURL);
			System.out.println("  [����DeviceURL]:" + CurrentURL + "\t" + device_state);
			//getService.put(q.get("y"), device_state); //�����豸״̬��ʼ��0	
		}
		
		// ����device state owl
		//updateSensorValue2owl(getService, upDateCurrentServiceReturnValue, upDateCurrentSmartDeviceState);
	}

}
