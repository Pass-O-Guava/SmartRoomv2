package action;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import com.opensymphony.xwork2.ActionSupport;

import owl.Constant;
import owl.SearchDevice;
import owl.Sender;
import util.ClientUtil;

public class StartAction extends ActionSupport {

	/**
	 * 智能会议室实现流程
	 */
	private static final long serialVersionUID = 1L;
	private static boolean executed;
	private String message;
	// 初始化HttpClient
	private static CookieStore cookieStore = new BasicCookieStore();
	private static CloseableHttpClient httpclient = ClientUtil.getHttpClient(cookieStore);

	public String Start() throws Exception {
		/*
		System.out.println("\n001\n");//wy
		//RESTServer.main(null);
		RESTServer R = new RESTServer();
		R.start1(4001);
		System.out.println("\nend\n");//wy
		*/
		
		executed = true;
		int count = 0;
		// 加载本体
		SearchDevice.getModel();
		/**
		 * 初始化SPARQL语句
		 */
		// 查询设备get服务
		String queryCurrentSmartDevcieGetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"get\". ?y SmartMeeting:hasServiceURL ?z.}";
		// 查询设备set服务
		String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
		// 查询传感器服务（全为get）
		String queryCurrentSensorService = Constant.PREFIX + " SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSensor ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceURL ?z.}";
		// 查询环境状态
		String queryEnviromentState = Constant.PREFIX + "SELECT ?y " + "WHERE { SmartMeeting:" + "meeting_room_1806"
				+ " SmartMeeting:hasState ?x. ?x SmartMeeting:hasValue ?y}";
		// //查询设备状态
		// ParameterizedSparqlString queryCurrentSmartDeviceState =new
		// ParameterizedSparqlString( Constant.PREFIX + " SELECT ?y where { ?x
		// SmartMeeting:hasService ?Service. ?x SmartMeeting:hasValue ?y }");
		// //查询传感器读数
		// ParameterizedSparqlString queryCurrentSensorState =new
		// ParameterizedSparqlString( Constant.PREFIX +" SELECT ?z where { ?x
		// SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x. ?y
		// SmartMeeting:hasValue ?z }");

		// 更新get服务返回值
		ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(
				Constant.PREFIX + "delete { ?Service SmartMeeting:returnServiceValue ?x}"
						+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
						+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
		// 更新设备状态值
		ParameterizedSparqlString upDateCurrentSmartDeviceState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete {?x SmartMeeting:hasValue ?y }" + "insert { ?x SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?x SmartMeeting:hasValue ?y}");
		// 更新传感器读数
		ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete { ?y SmartMeeting:hasValue ?z}" + "insert { ?y SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");
		
		
		
		
		// 查询所有get服务 ！！！！直接用ResultSet 遍历会报错，暂不知为何，先放到map中！！！！
		HashMap<RDFNode, String> getService = new HashMap<RDFNode, String>();
		HashMap<RDFNode, String> sensorService = new HashMap<RDFNode, String>();
		ResultSet SmartDevcieGetService = SearchDevice.runQuery(queryCurrentSmartDevcieGetService);
		
		while (SmartDevcieGetService.hasNext()) {
			QuerySolution qs = SmartDevcieGetService.nextSolution();
			getService.put(qs.get("y"), qs.get("z").asLiteral().getLexicalForm());
		}
		ResultSet SensorService = SearchDevice.runQuery(queryCurrentSensorService);
		while (SensorService.hasNext()) {
			QuerySolution qs = SensorService.nextSolution();
			sensorService.put(qs.get("y"), qs.get("z").asLiteral().getLexicalForm());
		}
		
		/**
		 * 监测开始
		 */
		// 循环调用Service
		long start = System.currentTimeMillis();
		while (executed) {
			count++;
			System.out.println("第" + count + "次监测：");
			// 获得所有设备当前状态
			Sender.gettoDevice(getService, upDateCurrentServiceReturnValue, upDateCurrentSmartDeviceState, httpclient,
					cookieStore);
			cookieStore.clear();
			
			// 获得所有传感器当前读数
			Sender.gettoSensor(sensorService, upDateCurrentServiceReturnValue, upDateCurrentSensorState, httpclient,
					cookieStore);
			cookieStore.clear();
			
			// 推理当前环境状态
			Iterator<QuerySolution> EnviromentState = SearchDevice.runQuery(queryEnviromentState);
			System.out.print("当前环境状态为：");
			while (EnviromentState.hasNext())
				System.out.println(EnviromentState.next().getLiteral("y").getLexicalForm());
			
			// 操作所有设备
			Sender.settoSensor(queryCurrentSmartDevcieSetService, httpclient);
			cookieStore.clear();
			System.out.println("--------------------------");
			System.out.println("以下为测试：");
			Sender.testWithoutOWL(httpclient, cookieStore);
			cookieStore.clear();
			
			// 更新本体到磁盘
			// SearchDevice.writeModel();
		}

		// 获得执行时间
		long runtime = System.currentTimeMillis() - start;
		double time = new BigDecimal((float) runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("结束成功！共运行" + count + "次,总时长" + time + "s");
		// 清空连接池
		ClientUtil.clear();
		return SUCCESS;

	}

	public String Stop() {
		if (executed = true) {
			executed = false;
			setMessage("Detection terminated！");
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
