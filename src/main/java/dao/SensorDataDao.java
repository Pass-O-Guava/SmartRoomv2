package dao;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import pojo.SensorData;
import util.DBAccess;

public class SensorDataDao {
	// Ôö
	public static void insert(SensorData record) {
		SqlSession sqlSession = null;
		try {

			sqlSession = DBAccess.getSqlSession();
			sqlSession.insert("SensorDataMapper.insert", record);
			sqlSession.commit();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (sqlSession != null) {

				sqlSession.close();
			}
		}
	}

	// ²é
	public static SensorData selectByPrimaryKey(String id) {
		SqlSession sqlSession = null;
		SensorData sd = new SensorData();
		try {

			sqlSession = DBAccess.getSqlSession();
			sd = sqlSession.selectOne("SensorDataMapper.selectByPrimaryKey", id);
			sqlSession.commit();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {
			if (sqlSession != null) {

				sqlSession.close();
			}
		}
		return sd;
	}
}
