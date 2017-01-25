package gov.usgs.owi.nldi.transform;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonGenerator;

import gov.usgs.owi.nldi.dao.BaseDao;

public class BasinTransformer extends MapToGeoJsonTransformer {

	public BasinTransformer(HttpServletResponse response) {
		super(response, BaseDao.BASIN);
	}

	@Override
	protected void writeProperties(JsonGenerator jsonGenerator, Map<String, Object> resultMap) {
		//Nothing to do for this one.
	}
}