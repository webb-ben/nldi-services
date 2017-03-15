package gov.usgs.owi.nldi.transform;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public class BasinTransformer extends MapToGeoJsonTransformer {

	public static final String BASIN_COUNT_HEADER = BaseDao.BASIN + COUNT_SUFFIX;

	public BasinTransformer(HttpServletResponse response) {
		super(response, BASIN_COUNT_HEADER);
	}

	@Override
	protected void writeProperties(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
		//Nothing to do for this one.
	}
}