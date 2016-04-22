package gov.usgs.owi.nldi.dao;

import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountDao extends BaseDao {
	private static final String NS = "count.";

	@Autowired
	public CountDao(SqlSessionFactory sqlSessionFactory) {
		super(sqlSessionFactory);
	}

	public String count(String featureType, Map<String, Object> parameterMap) {
		return Integer.toString(getSqlSession().selectOne(NS + featureType, parameterMap));
	}
}
