package owl;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.Query;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.update.UpdateRequest;

import dao.ArduinoDao;
import dao.SensorDataDao;
import pojo.Arduino;
import pojo.SensorData;

//������getָ���ȡ����ֵ
public class SendandUpdate {

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
	private static ResultSet results = null;

	/**
	 * ����豸״̬�����±��岢����
	 */
	public static void gettoDevice(ResultSet SmartDevcieGetService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSmartDeviceState,
			ParameterizedSparqlString queryCurrentSmartDeviceState, CloseableHttpClient httpclient,
			CookieStore cookieStore) throws ClientProtocolException, IOException {

		// ���������
		for ( ; SmartDevcieGetService.hasNext() ; ){
			QuerySolution qs = SmartDevcieGetService.nextSolution();
			CurrentService = qs.get("y");
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
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
			// �����豸״̬
			queryCurrentSmartDeviceState.setParam("Service", CurrentService);
			Query qcsds = queryCurrentSmartDeviceState.asQuery();
			results = SearchDevice.runQuery(qcsds);
			System.out.print("��ǰ�����豸״̬��");
			while (results.hasNext())
				System.out.println(results.nextSolution().getLiteral("y").getLexicalForm());
		}
		response.getEntity().getContent().close();
	}

	/**
	 * ��ô��������������±��岢����
	 */
	public static void gettoSensor(ResultSet CurrentSensorService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState, ParameterizedSparqlString queryCurrentSensorState,
			CloseableHttpClient httpclient, CookieStore cookieStore) throws ClientProtocolException, IOException {

		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		// ���������
		for ( ; CurrentSensorService.hasNext() ; ){
			QuerySolution qs = CurrentSensorService.nextSolution();
			CurrentService = qs.get("y");
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
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
			// ��������
			queryCurrentSensorState.setParam("Service", CurrentService);
			Query qcss = queryCurrentSensorState.asQuery();
			results = SearchDevice.runQuery(qcss);
			System.out.print("��ǰ������������");
			while (results.hasNext())
				System.out.println(results.nextSolution().getLiteral("z").getLexicalForm());

		}
		response.getEntity().getContent().close();
	}

	/**
	 * �����豸������
	 */
	public static void settoSensor(ResultSet SmartDevcieSetService, CloseableHttpClient httpclient)
			throws ClientProtocolException, IOException {
		for ( ; SmartDevcieSetService.hasNext() ; ){
			QuerySolution qs = SmartDevcieSetService.nextSolution();
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
			HttpGet get = new HttpGet(CurrentURL);
			response = httpclient.execute(get);
			System.out.println("�豸��������ɣ�");
			response.getEntity().getContent().close();
		}
	}

}
