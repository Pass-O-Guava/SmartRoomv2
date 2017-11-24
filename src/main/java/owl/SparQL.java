package owl;

import org.apache.jena.query.ParameterizedSparqlString;

public class SparQL{
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
	/*
	// ��ѯ ����״̬
	public static String queryEnviromentState = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:" + "meeting_room_1806"
			+ " SmartMeeting:hasSensor ?x. ?y SmartMeeting:measuredBy ?x. ?y SmartMeeting:hasValue ?z}";
	
	// ���� Device_State_Value
	public static ParameterizedSparqlString upDateCurrentSmartDeviceState = new ParameterizedSparqlString(Constant.PREFIX
		+ "delete {?x SmartMeeting:hasValue ?y }" + "insert { ?x SmartMeeting:hasValue ?StateValue}"
		+ "where  { ?x SmartMeeting:hasService ?Service.?x SmartMeeting:hasValue ?y}");
	
	// ��ѯ Device_Get_URL
	public static String queryCurrentSmartDevcieGetService = Constant.PREFIX + "SELECT ?y ?z " + "WHERE { SmartMeeting:"
		+ "meeting_room_1806"
		+ " SmartMeeting:hasSmartDevice ?x. ?x SmartMeeting:hasService ?y. ?y SmartMeeting:hasServiceType \"get\". ?y SmartMeeting:hasServiceURL ?z.}";
	*/
	
}