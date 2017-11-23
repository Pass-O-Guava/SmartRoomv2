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

//负责发送get指令，获取返回值
public class SendandUpdate {

	// 初始化HTTP相关对象
	private static List<Cookie> cookies = null;
	private static CloseableHttpResponse response = null;

	// 初始化部分变量
	private static String pin_num = null;
	private static String pin_type = null;
	private static int pin_value = 0;
	private static String CurrentURL = null;
	private static RDFNode CurrentService = null;
	private static String CurrentStateValue = null;
	private static ResultSet results = null;

	/**
	 * 获得设备状态，更新本体并反馈
	 */
	public static void gettoDevice(ResultSet SmartDevcieGetService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSmartDeviceState,
			ParameterizedSparqlString queryCurrentSmartDeviceState, CloseableHttpClient httpclient,
			CookieStore cookieStore) throws ClientProtocolException, IOException {

		// 遍历结果集
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
			// 更新get服务返回值
			upDateCurrentServiceReturnValue.setParam("Service", CurrentService);
			upDateCurrentServiceReturnValue.setLiteral("StateValue", CurrentStateValue);
			UpdateRequest usrv = upDateCurrentServiceReturnValue.asUpdate();
			SearchDevice.UpdateModel(usrv);
			// 更新设备状态
			upDateCurrentSmartDeviceState.setParam("Service", CurrentService);
			upDateCurrentSmartDeviceState.setLiteral("StateValue", CurrentStateValue);
			UpdateRequest usds = upDateCurrentSmartDeviceState.asUpdate();
			SearchDevice.UpdateModel(usds);
			// 反馈设备状态
			queryCurrentSmartDeviceState.setParam("Service", CurrentService);
			Query qcsds = queryCurrentSmartDeviceState.asQuery();
			results = SearchDevice.runQuery(qcsds);
			System.out.print("当前智能设备状态：");
			while (results.hasNext())
				System.out.println(results.nextSolution().getLiteral("y").getLexicalForm());
		}
		response.getEntity().getContent().close();
	}

	/**
	 * 获得传感器读数，更新本体并反馈
	 */
	public static void gettoSensor(ResultSet CurrentSensorService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState, ParameterizedSparqlString queryCurrentSensorState,
			CloseableHttpClient httpclient, CookieStore cookieStore) throws ClientProtocolException, IOException {

		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		// 遍历结果集
		for ( ; CurrentSensorService.hasNext() ; ){
			QuerySolution qs = CurrentSensorService.nextSolution();
			CurrentService = qs.get("y");
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
			HttpGet get = new HttpGet(CurrentURL);
			response = httpclient.execute(get);
			cookies = cookieStore.getCookies();
			if (null != cookies && !cookies.isEmpty() && cookies.size() > 1) {
				// cookies默认按照名称排序，因此顺序为pin_num,pin_type,pin_value
				pin_num = cookies.get(0).getValue();
				pin_type = cookies.get(1).getValue();
				pin_value = Integer.parseInt(cookies.get(2).getValue());
				// 另起线程完成数据库操作
				Runnable myRunnable = new Runnable() {
					public void run() {
						// 历史记录存储到一个SensorData对象中
						Arduino ad = ArduinoDao.selectByPrimaryKey("192.168.1.108");
						SensorData sd = new SensorData();
						sd.setBoardip("192.168.1.108");
						sd.setName(ad.get(pin_type + pin_num));
						sd.setType(pin_type);
						sd.setValue(pin_value);
						sd.setTime(timeStamp);
						// 数据库持久化
						SensorDataDao.insert(sd);
					}
				};
				Thread thread = new Thread(myRunnable);
				thread.start();

			}
			// 更新get服务返回值
			upDateCurrentServiceReturnValue.setParam("Service", CurrentService);
			upDateCurrentServiceReturnValue.setLiteral("StateValue", pin_value);
			UpdateRequest usrv = upDateCurrentServiceReturnValue.asUpdate();
			SearchDevice.UpdateModel(usrv);
			// 更新传感器读数
			upDateCurrentSensorState.setParam("Service", CurrentService);
			upDateCurrentSensorState.setLiteral("StateValue", pin_value);
			UpdateRequest uss = upDateCurrentSensorState.asUpdate();
			SearchDevice.UpdateModel(uss);
			// 反馈读数
			queryCurrentSensorState.setParam("Service", CurrentService);
			Query qcss = queryCurrentSensorState.asQuery();
			results = SearchDevice.runQuery(qcss);
			System.out.print("当前传感器读数：");
			while (results.hasNext())
				System.out.println(results.nextSolution().getLiteral("z").getLexicalForm());

		}
		response.getEntity().getContent().close();
	}

	/**
	 * 操作设备并反馈
	 */
	public static void settoSensor(ResultSet SmartDevcieSetService, CloseableHttpClient httpclient)
			throws ClientProtocolException, IOException {
		for ( ; SmartDevcieSetService.hasNext() ; ){
			QuerySolution qs = SmartDevcieSetService.nextSolution();
			CurrentURL = qs.get("z").asLiteral().getLexicalForm();
			HttpGet get = new HttpGet(CurrentURL);
			response = httpclient.execute(get);
			System.out.println("设备操作已完成！");
			response.getEntity().getContent().close();
		}
	}

}
