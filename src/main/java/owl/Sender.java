package owl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateRequest;

import dao.ArduinoDao;
import dao.SensorDataDao;
import pojo.Arduino;
import pojo.SensorData;

//������getָ���ȡ����ֵ
public class Sender {

	// ��ʼ��HTTP��ض���
	private static List<Cookie> cookies = null;
	private static CloseableHttpResponse response = null;

	// ��ʼ�����ֱ���
	private static String pin_num = null;
	private static String pin_type = null;
	private static int pin_value = 0;
	private static String CurrentURL = null;
	private static RDFNode CurrentService = null;
	private static String CurrentStateValue = null;

	/**
	 * ����豸״̬�����±��岢����
	 */
	public static void gettoDevice(HashMap<RDFNode, String> getService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSmartDeviceState, CloseableHttpClient httpclient,
			CookieStore cookieStore) throws ClientProtocolException, IOException {
		// ����Service���÷���
		Iterator<Entry<RDFNode, String>> it = getService.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> CurrentNode = it.next();
			CurrentService = CurrentNode.getKey();
			CurrentURL = CurrentNode.getValue();
			
			/*
			System.out.println("\n");
			System.out.println("CurrentService��" + CurrentService + "\n"); //
			System.out.println("CurrentURL��" + CurrentURL + "\n"); //
			*/
			
			HttpGet get = new HttpGet(CurrentURL);
			response = httpclient.execute(get);
			cookies = cookieStore.getCookies();
			if (null != cookies && !cookies.isEmpty() && cookies.size() > 1) {
				pin_value = Integer.parseInt(cookies.get(2).getValue());
				if (pin_value == 1) {
					CurrentStateValue = "on";
				} else {
					CurrentStateValue = "off";
				}
			}
			// �����豸״̬
			System.out.println("��ǰ�����豸״̬��" + CurrentStateValue);
			// ����get���񷵻�ֵ
			upDateCurrentServiceReturnValue.setParam("Service", CurrentService);
			upDateCurrentServiceReturnValue.setLiteral("StateValue", CurrentStateValue);
			UpdateRequest usrv = upDateCurrentServiceReturnValue.asUpdate();
			SearchDevice.UpdateModel(usrv);
			// �����豸״̬
			upDateCurrentSmartDeviceState.setParam("Service", CurrentService);
			upDateCurrentSmartDeviceState.setLiteral("StateValue", CurrentStateValue);
			UpdateRequest usds = upDateCurrentSmartDeviceState.asUpdate();
			SearchDevice.UpdateModel(usds);
		}
		response.getEntity().getContent().close();
	}

	/**
	 * ��ô��������������±��岢����
	 */
	public static void gettoSensor(HashMap<RDFNode, String> sensorService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState, CloseableHttpClient httpclient, CookieStore cookieStore)
			throws ClientProtocolException, IOException {
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		// ����Service���÷���
		Iterator<Entry<RDFNode, String>> it = sensorService.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> CurrentNode = it.next();
			CurrentService = CurrentNode.getKey();
			CurrentURL = CurrentNode.getValue();
			HttpGet get = new HttpGet(CurrentURL);
			response = httpclient.execute(get);
			cookies = cookieStore.getCookies();
			if (null != cookies && !cookies.isEmpty() && cookies.size() > 1) {
				// cookiesĬ�ϰ��������������˳��Ϊpin_num,pin_type,pin_value
				pin_num = cookies.get(0).getValue();
				pin_type = cookies.get(1).getValue();
				pin_value = Integer.parseInt(cookies.get(2).getValue());
				// �����߳�������ݿ����
				Runnable myRunnable = new Runnable() {
					public void run() {
						// ��ʷ��¼�洢��һ��SensorData������
						Arduino ad = ArduinoDao.selectByPrimaryKey("192.168.1.108");
						SensorData sd = new SensorData();
						sd.setBoardip("192.168.1.108");
						sd.setName(ad.get(pin_type + pin_num));
						sd.setType(pin_type);
						sd.setValue(pin_value);
						sd.setTime(timeStamp);
						// ���ݿ�־û�
						SensorDataDao.insert(sd);
					}
				};
				Thread thread = new Thread(myRunnable);
				thread.start();

			}
			// ��������
			System.out.println("��ǰ������������" + pin_value);
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
		}
		response.getEntity().getContent().close();
	}

	/**
	 * �����豸������
	 */
	public static void settoSensor(String queryCurrentSmartDevcieSetService, CloseableHttpClient httpclient)
			throws ClientProtocolException, IOException {
		ResultSet SmartDevcieSetService = SearchDevice.runQuery(queryCurrentSmartDevcieSetService);
		while (SmartDevcieSetService.hasNext()) {
			QuerySolution q = SmartDevcieSetService.next();
			CurrentURL = q.get("z").asLiteral().getLexicalForm();
			HttpGet set = new HttpGet(CurrentURL);
			response = httpclient.execute(set);
			System.out.println("�豸��������ɣ�");
			response.getEntity().getContent().close();
		}
	}

	// ������ģ�飬��ʱ����OWL
	public static void testWithoutOWL(CloseableHttpClient httpclient, CookieStore cookieStore)
			throws ClientProtocolException, IOException {
		/**���Կյ��߼�
		 * rotation sensor ģ���¶ȴ�������arduino�˽���ťֵmap��(-20,40),��λ���϶�C
		 * motor���� ģ��յ�����ת�������������LED�ƣ�������ȣ���������
		 * ÿ������http://192.168.1.108/arduino/webanalog/1��ȡ��ťֵ
		 * ������ťֵ���ƺ�ơ����ƺͷ��ȣ��߼�Ϊ��
		 * 
		 * �¶�<=10��Ϊ���䣬���ȿ�����ƿ�,���ƹ�
		 *      http://192.168.1.108/arduino/webdigital/6/1
		 * 		http://192.168.1.108/arduino/webdigital/3/1
		 * 		http://192.168.1.108/arduino/webdigital/4/0 
		 * 
		 * 10<�¶�<=20��Ϊ���ˣ�ȫ���ر�
		 *      http://192.168.1.108/arduino/webdigital/6/0
		 *      http://192.168.1.108/arduino/webdigital/3/0
		 *      http://192.168.1.108/arduino/webdigital/4/0
		 * 
		 * �¶�>20��Ϊ���ȣ����ȿ������ƿ�,��ƹ�
		 *      http://192.168.1.108/arduino/webdigital/6/1
		 *      http://192.168.1.108/arduino/webdigital/3/0
		 *      http://192.168.1.108/arduino/webdigital/4/1
		 */
		
		//��ʼ��URL
		HttpGet getDegree = new HttpGet("http://192.168.1.108/arduino/webanalog/1");
		HttpGet openAC = new HttpGet("http://192.168.1.108/arduino/webdigital/6/1");
		HttpGet closeAC = new HttpGet("http://192.168.1.108/arduino/webdigital/6/0");
		HttpGet setWarm = new HttpGet("http://192.168.1.108/arduino/webdigital/3/1");
		HttpGet closeWarm = new HttpGet("http://192.168.1.108/arduino/webdigital/3/0");
		HttpGet setCold = new HttpGet("http://192.168.1.108/arduino/webdigital/4/1");
		HttpGet closeCold = new HttpGet("http://192.168.1.108/arduino/webdigital/4/0");
		
		//��ȡ�¶�ֵ
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		response = httpclient.execute(getDegree);
		response.getEntity().getContent().close();
		cookies = cookieStore.getCookies();
		pin_num = cookies.get(0).getValue();
		pin_type = cookies.get(1).getValue();
		
		pin_value = Integer.parseInt(cookies.get(2).getValue());
		if(pin_value<=10){
			System.out.println("��ǰ�¶ȹ��ͣ�"+pin_value);
			response =httpclient.execute(openAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(setWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeCold);
			response.getEntity().getContent().close();
		}else if(pin_value>20){
			System.out.println("��ǰ�¶ȹ��ߣ�"+pin_value);
			response =httpclient.execute(openAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(setCold);
			response.getEntity().getContent().close();
		}else{
			System.out.println("��ǰ�¶����ˣ�"+pin_value);
			response =httpclient.execute(closeAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeCold);
			response.getEntity().getContent().close();
		}
		System.out.println("�豸������ɣ�");
		// �����߳�������ݿ����
		Runnable myRunnable = new Runnable() {
			public void run() {
				// ��ʷ��¼�洢��һ��SensorData������
				Arduino ad = ArduinoDao.selectByPrimaryKey("192.168.1.108");
				SensorData sd = new SensorData();
				sd.setBoardip("192.168.1.108");
				sd.setName(ad.get(pin_type + pin_num));
				sd.setType(pin_type);
				sd.setValue(pin_value);
				sd.setTime(timeStamp);
				
				/*
				System.out.print("\n"); //
				System.out.print("D3=��" + ad.get("D3") + "\n"); //
				System.out.print("\n"); //
				*/
				/*
				System.out.print("\n"); //
				System.out.print("-----------����һ���߳�-----------\n"); //
				System.out.print("Time=��" + sd.getTime() + "\n"); //
				System.out.print("Name=��" + sd.getName() + "\n"); //
				System.out.print("Type=��" + sd.getType() + "\n"); //
				System.out.print("Boardip=��" + sd.getBoardip() + "\n"); //
				System.out.print("Value=��" + sd.getValue() + "\n"); //
				System.out.print("-----------����һ���߳�-----------\n"); //
				System.out.print("\n"); //
				*/
				
				// ���ݿ�־û�
				SensorDataDao.insert(sd);
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
		
	}

}
