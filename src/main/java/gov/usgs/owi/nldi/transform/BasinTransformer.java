package gov.usgs.owi.nldi.transform;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import gov.usgs.owi.nldi.dao.BaseDao;

public class BasinTransformer extends MapToGeoJsonTransformer {

	public static final String BASIN_COUNT_HEADER = BaseDao.BASIN;
	public BasinTransformer(HttpServletResponse response, String rootUrl) {
		super(response, rootUrl, BASIN_COUNT_HEADER);
	}

	@Override
	protected void writeProperties(Map<String, Object> resultMap) {
		//Nothing to do for this one.
	}

}
