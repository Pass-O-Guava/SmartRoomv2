package dao;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import pojo.Arduino;
import util.DBAccess;

public class ArduinoDao {
	// 增
	public static void insert(Arduino record) {
		SqlSession sqlSession = null;
		try {

			sqlSession = DBAccess.getSqlSession();
			sqlSession.insert("ArduinoMapper.insert", record);
			sqlSession.commit();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (sqlSession != null) {

				sqlSession.close();
			}
		}
	}

	// 查
	public static Arduino selectByPrimaryKey(String ip) {
		SqlSession sqlSession = null;
		Arduino ar = new Arduino();
		try {

			sqlSession = DBAccess.getSqlSession();
			ar = sqlSession.selectOne("ArduinoMapper.selectByPrimaryKey", ip);
			sqlSession.commit();

		} catch (IOException e) {
			e.printStackTrace();

		} finally {
			if (sqlSession != null) {
				sqlSession.close();
			}
		}
		return ar;
	}
	
	//通过A2字段-查
	public static Arduino selectByA2(String a2) {
		SqlSession sqlSession = null;
		Arduino ar = new Arduino();
		try {

			sqlSession = DBAccess.getSqlSession();
			ar = sqlSession.selectOne("ArduinoMapper.selectByA2", a2);
			sqlSession.commit();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (sqlSession != null) {

				sqlSession.close();
			}
		}
		return ar;
	}
	
	public static Arduino selectByVersion(String version) {
		SqlSession sqlSession = null;
		Arduino ad = new Arduino();
		try {

			sqlSession = DBAccess.getSqlSession();
			ad = sqlSession.selectOne("ArduinoMapper.selectByVersion", version);
			sqlSession.commit();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (sqlSession != null) {

				sqlSession.close();
			}
		}
		return ad;
	}
    
}
