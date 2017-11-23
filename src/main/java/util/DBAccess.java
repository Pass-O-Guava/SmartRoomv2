package util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

public class DBAccess {

	public static SqlSession getSqlSession() throws IOException {
		Reader reader = Resources.getResourceAsReader("Configuration.xml");
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		SqlSession sqlsession=sqlSessionFactory.openSession();
		return sqlsession;
	}
//	public static void main(String[] args) {
//		try {
//			SqlSession sqlsession=DBAccess.getSqlSession();
//			System.out.println(sqlsession);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
