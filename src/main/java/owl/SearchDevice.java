package owl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.sparql.serializer.SerializationContext;
import org.apache.jena.update.UpdateAction;
import org.apache.jena.update.UpdateRequest;


public class SearchDevice {
	protected static SerializationContext context = null;

	private static Model model;// 本体模型
	private static QueryExecution qex;
	private static ResultSet results;


	// 推理部分 (添加自身推理机和rule文件)
	private static List<Rule> rules = new ArrayList<Rule>();
	private static GenericRuleReasoner rea;
	private static InfModel inf;

	// 本体文件路径
	private static String root = null;
	//private static String owlpath = "/WEB-INF/SmartMeetingService.owl";
	//private static String rulepath = "/WEB-INF/SmartMeeting.rules";
	private static String owlpath_update = "/WEB-INF/SmartMeetingService-AddAC-update.owl";
	private static String owlpath = "/WEB-INF/SmartMeetingService-AddAC.owl";
	private static String rulepath = "/WEB-INF/SmartMeeting-AddAC.rules";

	static {
		// 获取当前web项目目录
		File file;
		try {
			file = new File(new File(SearchDevice.class.getResource("/").toURI()).getParentFile().getParentFile()
					.getAbsolutePath());
			root = file.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载本地文件形式的本体模型，失败系统退出
	 */
	public static boolean getModel() {

		// 创建模型，自带OWL full 推理机
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF);

		InputStreamReader in;
		try {
			// 读取本体文件
			FileInputStream file = new FileInputStream(root + owlpath);
			in = new InputStreamReader(file, "UTF-8");// 处理中文
			model.read(in, null);
			in.close();
			// 基于rules文件创建自定义推理机
			rules = Rule.rulesFromURL(root + rulepath);
			rea = new GenericRuleReasoner(rules);
			// 创建推理模型
			inf = ModelFactory.createInfModel(rea, model);

		} catch (FileNotFoundException e) {
			System.out.println("无法打开本体文件，程序将终止");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		return true;
	}

	/**
	 * 修改某个实例的某个数据属性值后，更新模型并推理 
	 * @param queryStr
	 */
	public static boolean UpdateModel(UpdateRequest uq) {
		// 更新模型
		UpdateAction.execute(uq, model);
		// 在更新后的模型基础上，创建推理模型
		inf = ModelFactory.createInfModel(rea, model);
		return true;
	}

	/**
	 * 保存本体到本地
	 */
	public static void writeModel() {
		try {
			FileOutputStream file = new FileOutputStream(root + owlpath_update);
			model.write(file);
			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("无法打开本体文件，程序将终止");
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

	}
	/**
	 * 提交sparql的查询，输出返回结果
	 * 
	 * @param Query q
	 */
	public static ResultSet runQuery(Query q) {
		qex = QueryExecutionFactory.create(q, inf);
		results = qex.execSelect();
		return results;
		// qe.close();
	}
	/**
	 * 提交sparql的查询，输出返回结果
	 * 
	 * @param String sq
	 */
	public static ResultSet runQuery(String sq) {
		Query query = QueryFactory.create(sq) ;
		qex = QueryExecutionFactory.create(query, inf);
		results = qex.execSelect();
		return results;
		// qe.close();
	}
	
	
	
//	/**
//	 * 将查询到的结果信息存入List中（不带URL前缀） 将查询结果从RDF层面的每一条陈述，转换为二维链表的形式
//	 * @param queryString
//	 */
//	public static List<List<String>> SearchInformationList1(String queryString) {
//
//		List<List<String>> resultList = new ArrayList<List<String>>();// 查询结果List
//		SearchDevice.runQuery(queryString);// 查询信息
//
//		while (results.hasNext()) {
//			List<String> oneRowresult = new ArrayList<String>();// 存放查询后的每一条（行）结果
//			QuerySolution soln = results.next();// 查询结果中的每一条（称之为满足条件的一个solution）
//			Iterator<String> vars = soln.varNames();// 查询变量名
//			while (vars.hasNext()) {
//				String var = vars.next();
//				RDFNode rn = soln.get(var);
//				if (rn.isResource()) {
//					// 若是资源节点Resource
//					oneRowresult.add(pm.shortForm(soln.get(var).toString()));// 去前缀后转换为String加入二维链表
//				} else {
//					// 若是文字节点Literal
//					oneRowresult.add(soln.get(var).asLiteral().getLexicalForm());// 直接转换为文本加入二维链表
//				}
//			}
//			resultList.add(oneRowresult);
//		}
//
//		qe.close();
//		return resultList;
//	}
//	/**
//	 * 自定义打印结果集
//	 * @param resultList
//	 */
//	public static void print(List<List<String>> resultList) {
//		for (int i = 0; i < resultList.size(); i++) {
//			for (int j = 0; j < resultList.get(i).size(); j++) {
//				System.out.print(resultList.get(i).get(j) + "   |   ");
//			}
//			System.out.println();
//		}
//	}
	//
	// /**
	// * 将宾语值（RDFNode类型）转换为String,并去除URL前綴
	// *
	// * @param rBind,
	// * varName
	// */
	// private static String getVarValueAsString(RDFNode obj) {
	// context = new SerializationContext(query);
	//
	// if (obj == null) {
	// return " ";
	// }
	// return FmtUtils.stringForRDFNode(obj, context);
	// }
	// // 将查询到的结果信息存入List中(带URL前缀)
	// public List<List<String>> SearchInformationList(String queryString) {
	// SearchDevice myLearn = new SearchDevice();
	//
	// myLearn.runQuery(queryString);// 查询信息
	// int column = results.getResultVars().size();// 查询变量个数
	// List<String> var = results.getResultVars();// 查询的变量数组
	// List<List<String>> resultList = new ArrayList<List<String>>();// 查询结果List
	// // resultList.add(var);
	// while (results.hasNext()) {
	//
	// QuerySolution soln = results.nextSolution();//
	// 查询结果中的每一条（称之为满足条件的一个solution）
	// List<String> oneRowresult = new ArrayList<String>();// 存放查询后的每一条（行）结果
	// for (int i = 0; i < column; i++) {
	// String oneResult = soln.get(var.get(i)).toString();
	// oneRowresult.add(oneResult);
	// }
	// resultList.add(oneRowresult);
	// }
	// qe.close();
	// return resultList;
	// }

	// 字符串分割，提取出本体中命名空间后面的部分
	// public static String ExtNameFromResource(String str) {
	//
	// String[] strarray = str.split("#");
	// if (strarray.length == 2) {
	// return strarray[1];
	// } else
	// return str;
	// }

	// public static String combinationString(String s) {
	// return "<" + s + ">";
	// }

}
