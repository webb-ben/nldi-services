package gov.usgs.owi.nldi.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DemoStreamingDao extends BaseDao {
	String QUERY_SELECT_ID = ".select";
	
	@Autowired
	public DemoStreamingDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}
	
	public void stream(String nameSpace, Map<String, Object> parameterMap, ResultHandler<?> handler) {
		if (null == handler) {
			throw new IllegalArgumentException("A ResultHandler is required for the DemoStreamingDao.stream");
		}
		getSqlSession().select(nameSpace + QUERY_SELECT_ID, parameterMap, handler);
	}

	public LinkedHashMap<?,?> navigate(String nameSpace, Map<String, Object> parameterMap) {
		return getSqlSession().selectOne(nameSpace + ".navigate");
	}

}
