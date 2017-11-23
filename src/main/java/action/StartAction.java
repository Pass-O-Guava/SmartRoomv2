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
	 * ���ܻ�����ʵ������
	 */
	private static final long serialVersionUID = 1L;
	private static boolean executed;
	private String message;
	// ��ʼ��HttpClient
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
		// ���ر���
		SearchDevice.getModel();
		/**
		 * ��ʼ��SPARQL���
		 */
		// ��ѯ�豸get����
		String queryCurrentSmartDevcieGetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"get\". ?y SmartMeeting:hasServiceURL ?z.}";
		// ��ѯ�豸set����
		String queryCurrentSmartDevcieSetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"set\". ?y SmartMeeting:hasServiceURL ?z.}";
		// ��ѯ����������ȫΪget��
		String queryCurrentSensorService = Constant.PREFIX + " SELECT ?y ?z " + "WHERE { SmartMeeting:"
				+ "meeting_room_1806"
				+ " SmartMeeting:hasSensor ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceURL ?z.}";
		// ��ѯ����״̬
		String queryEnviromentState = Constant.PREFIX + "SELECT ?y " + "WHERE { SmartMeeting:" + "meeting_room_1806"
				+ " SmartMeeting:hasState ?x. ?x SmartMeeting:hasValue ?y}";
		// //��ѯ�豸״̬
		// ParameterizedSparqlString queryCurrentSmartDeviceState =new
		// ParameterizedSparqlString( Constant.PREFIX + " SELECT ?y where { ?x
		// SmartMeeting:hasService ?Service. ?x SmartMeeting:hasValue ?y }");
		// //��ѯ����������
		// ParameterizedSparqlString queryCurrentSensorState =new
		// ParameterizedSparqlString( Constant.PREFIX +" SELECT ?z where { ?x
		// SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x. ?y
		// SmartMeeting:hasValue ?z }");

		// ����get���񷵻�ֵ
		ParameterizedSparqlString upDateCurrentServiceReturnValue = new ParameterizedSparqlString(
				Constant.PREFIX + "delete { ?Service SmartMeeting:returnServiceValue ?x}"
						+ "insert { ?Service SmartMeeting:returnServiceValue ?StateValue}"
						+ "where  { ?Service SmartMeeting:returnServiceValue ?x}");
		// �����豸״ֵ̬
		ParameterizedSparqlString upDateCurrentSmartDeviceState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete {?x SmartMeeting:hasValue ?y }" + "insert { ?x SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?x SmartMeeting:hasValue ?y}");
		// ���´���������
		ParameterizedSparqlString upDateCurrentSensorState = new ParameterizedSparqlString(Constant.PREFIX
				+ "delete { ?y SmartMeeting:hasValue ?z}" + "insert { ?y SmartMeeting:hasValue ?StateValue}"
				+ "where  { ?x SmartMeeting:hasService ?Service.?y SmartMeeting:measuredBy ?x.?y SmartMeeting:hasValue ?z}");
		
		
		
		
		// ��ѯ����get���� ��������ֱ����ResultSet �����ᱨ���ݲ�֪Ϊ�Σ��ȷŵ�map�У�������
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
		 * ��⿪ʼ
		 */
		// ѭ������Service
		long start = System.currentTimeMillis();
		while (executed) {
			count++;
			System.out.println("��" + count + "�μ�⣺");
			// ��������豸��ǰ״̬
			Sender.gettoDevice(getService, upDateCurrentServiceReturnValue, upDateCurrentSmartDeviceState, httpclient,
					cookieStore);
			cookieStore.clear();
			
			// ������д�������ǰ����
			Sender.gettoSensor(sensorService, upDateCurrentServiceReturnValue, upDateCurrentSensorState, httpclient,
					cookieStore);
			cookieStore.clear();
			
			// ����ǰ����״̬
			Iterator<QuerySolution> EnviromentState = SearchDevice.runQuery(queryEnviromentState);
			System.out.print("��ǰ����״̬Ϊ��");
			while (EnviromentState.hasNext())
				System.out.println(EnviromentState.next().getLiteral("y").getLexicalForm());
			
			// ���������豸
			Sender.settoSensor(queryCurrentSmartDevcieSetService, httpclient);
			cookieStore.clear();
			System.out.println("--------------------------");
			System.out.println("����Ϊ���ԣ�");
			Sender.testWithoutOWL(httpclient, cookieStore);
			cookieStore.clear();
			
			// ���±��嵽����
			// SearchDevice.writeModel();
		}

		// ���ִ��ʱ��
		long runtime = System.currentTimeMillis() - start;
		double time = new BigDecimal((float) runtime / 1000).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		System.out.println("�����ɹ���������" + count + "��,��ʱ��" + time + "s");
		// ������ӳ�
		ClientUtil.clear();
		return SUCCESS;

	}

	public String Stop() {
		if (executed = true) {
			executed = false;
			setMessage("Detection terminated��");
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
