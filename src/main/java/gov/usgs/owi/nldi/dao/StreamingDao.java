package gov.usgs.owi.nldi.dao;

import java.util.Map;

import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StreamingDao extends BaseDao {
	String QUERY_SELECT_ID = ".select";
	
	@Autowired
	public StreamingDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}
	
	public void stream(String nameSpace, Map<String, Object> parameterMap, ResultHandler<?> handler) {
		if (null == handler) {
			throw new IllegalArgumentException("A ResultHandler is required for the StreamingDao.stream");
		}
		getSqlSession().select(nameSpace + QUERY_SELECT_ID, parameterMap, handler);
	}

	public void navigate(String nameSpace, Map<String, Object> parameterMap) {
		getSqlSession().selectOne(nameSpace + ".navigate");
	}

}
