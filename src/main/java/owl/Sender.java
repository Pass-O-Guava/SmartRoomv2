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

//负责发送get指令，获取返回值
public class Sender {

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

	/**
	 * 获得设备状态，更新本体并反馈
	 */
	public static void gettoDevice(HashMap<RDFNode, String> getService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSmartDeviceState, CloseableHttpClient httpclient,
			CookieStore cookieStore) throws ClientProtocolException, IOException {
		// 遍历Service调用服务
		Iterator<Entry<RDFNode, String>> it = getService.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> CurrentNode = it.next();
			CurrentService = CurrentNode.getKey();
			CurrentURL = CurrentNode.getValue();
			
			/*
			System.out.println("\n");
			System.out.println("CurrentService：" + CurrentService + "\n"); //
			System.out.println("CurrentURL：" + CurrentURL + "\n"); //
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
			// 反馈设备状态
			System.out.println("当前智能设备状态：" + CurrentStateValue);
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
		}
		response.getEntity().getContent().close();
	}

	/**
	 * 获得传感器读数，更新本体并反馈
	 */
	public static void gettoSensor(HashMap<RDFNode, String> sensorService,
			ParameterizedSparqlString upDateCurrentServiceReturnValue,
			ParameterizedSparqlString upDateCurrentSensorState, CloseableHttpClient httpclient, CookieStore cookieStore)
			throws ClientProtocolException, IOException {
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		// 遍历Service调用服务
		Iterator<Entry<RDFNode, String>> it = sensorService.entrySet().iterator();
		while (it.hasNext()) {
			Entry<RDFNode, String> CurrentNode = it.next();
			CurrentService = CurrentNode.getKey();
			CurrentURL = CurrentNode.getValue();
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
			// 反馈读数
			System.out.println("当前传感器读数：" + pin_value);
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
		}
		response.getEntity().getContent().close();
	}

	/**
	 * 操作设备并反馈
	 */
	public static void settoSensor(String queryCurrentSmartDevcieSetService, CloseableHttpClient httpclient)
			throws ClientProtocolException, IOException {
		ResultSet SmartDevcieSetService = SearchDevice.runQuery(queryCurrentSmartDevcieSetService);
		while (SmartDevcieSetService.hasNext()) {
			QuerySolution q = SmartDevcieSetService.next();
			CurrentURL = q.get("z").asLiteral().getLexicalForm();
			HttpGet set = new HttpGet(CurrentURL);
			response = httpclient.execute(set);
			System.out.println("设备操作已完成！");
			response.getEntity().getContent().close();
		}
	}

	// 测试新模组，暂时不用OWL
	public static void testWithoutOWL(CloseableHttpClient httpclient, CookieStore cookieStore)
			throws ClientProtocolException, IOException {
		/**测试空调逻辑
		 * rotation sensor 模拟温度传感器，arduino端将旋钮值map到(-20,40),单位摄氏度C
		 * motor风扇 模拟空调，旋转即开，配合两个LED灯，红灯制热，蓝灯制冷
		 * 每次请求http://192.168.1.108/arduino/webanalog/1读取旋钮值
		 * 根据旋钮值控制红灯、蓝灯和风扇，逻辑为：
		 * 
		 * 温度<=10视为过冷，风扇开，红灯开,蓝灯关
		 *      http://192.168.1.108/arduino/webdigital/6/1
		 * 		http://192.168.1.108/arduino/webdigital/3/1
		 * 		http://192.168.1.108/arduino/webdigital/4/0 
		 * 
		 * 10<温度<=20视为适宜，全部关闭
		 *      http://192.168.1.108/arduino/webdigital/6/0
		 *      http://192.168.1.108/arduino/webdigital/3/0
		 *      http://192.168.1.108/arduino/webdigital/4/0
		 * 
		 * 温度>20视为过热，风扇开，蓝灯开,红灯关
		 *      http://192.168.1.108/arduino/webdigital/6/1
		 *      http://192.168.1.108/arduino/webdigital/3/0
		 *      http://192.168.1.108/arduino/webdigital/4/1
		 */
		
		//初始化URL
		HttpGet getDegree = new HttpGet("http://192.168.1.108/arduino/webanalog/1");
		HttpGet openAC = new HttpGet("http://192.168.1.108/arduino/webdigital/6/1");
		HttpGet closeAC = new HttpGet("http://192.168.1.108/arduino/webdigital/6/0");
		HttpGet setWarm = new HttpGet("http://192.168.1.108/arduino/webdigital/3/1");
		HttpGet closeWarm = new HttpGet("http://192.168.1.108/arduino/webdigital/3/0");
		HttpGet setCold = new HttpGet("http://192.168.1.108/arduino/webdigital/4/1");
		HttpGet closeCold = new HttpGet("http://192.168.1.108/arduino/webdigital/4/0");
		
		//获取温度值
		Date date = new Date();
		Timestamp timeStamp = new Timestamp(date.getTime());
		response = httpclient.execute(getDegree);
		response.getEntity().getContent().close();
		cookies = cookieStore.getCookies();
		pin_num = cookies.get(0).getValue();
		pin_type = cookies.get(1).getValue();
		
		pin_value = Integer.parseInt(cookies.get(2).getValue());
		if(pin_value<=10){
			System.out.println("当前温度过低："+pin_value);
			response =httpclient.execute(openAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(setWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeCold);
			response.getEntity().getContent().close();
		}else if(pin_value>20){
			System.out.println("当前温度过高："+pin_value);
			response =httpclient.execute(openAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(setCold);
			response.getEntity().getContent().close();
		}else{
			System.out.println("当前温度适宜："+pin_value);
			response =httpclient.execute(closeAC);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeWarm);
			response.getEntity().getContent().close();
			response =httpclient.execute(closeCold);
			response.getEntity().getContent().close();
		}
		System.out.println("设备操作完成！");
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
				
				/*
				System.out.print("\n"); //
				System.out.print("D3=：" + ad.get("D3") + "\n"); //
				System.out.print("\n"); //
				*/
				/*
				System.out.print("\n"); //
				System.out.print("-----------这是一个线程-----------\n"); //
				System.out.print("Time=：" + sd.getTime() + "\n"); //
				System.out.print("Name=：" + sd.getName() + "\n"); //
				System.out.print("Type=：" + sd.getType() + "\n"); //
				System.out.print("Boardip=：" + sd.getBoardip() + "\n"); //
				System.out.print("Value=：" + sd.getValue() + "\n"); //
				System.out.print("-----------这是一个线程-----------\n"); //
				System.out.print("\n"); //
				*/
				
				// 数据库持久化
				SensorDataDao.insert(sd);
			}
		};
		Thread thread = new Thread(myRunnable);
		thread.start();
		
	}

}
