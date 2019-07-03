package gov.usgs.owi.nldi.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import gov.usgs.owi.nldi.dao.BaseDao;
import gov.usgs.owi.nldi.dao.LookupDao;
import gov.usgs.owi.nldi.dao.StreamingDao;
import gov.usgs.owi.nldi.services.ConfigurationService;
import gov.usgs.owi.nldi.services.LogService;
import gov.usgs.owi.nldi.services.Navigation;
import gov.usgs.owi.nldi.services.Parameters;
import gov.usgs.owi.nldi.transform.CharacteristicDataTransformer;
import gov.usgs.owi.nldi.transform.CharacteristicMetadataTransformer;

@Controller
public class CharacteristicsController extends BaseController {
//	private static final Logger LOG = LoggerFactory.getLogger(CharacteristicsController.class);

	protected ConfigurationService configurationService;

	@Autowired
	public CharacteristicsController(LookupDao inLookupDao, StreamingDao inStreamingDao, Navigation inNavigation, Parameters inParameters, ConfigurationService configurationService, LogService inLogService) {
		super(inLookupDao, inStreamingDao, inNavigation, inParameters, configurationService.getRootUrl(), inLogService);
	}

	@GetMapping(value="{characteristicType}/characteristics")
	public void getCharacteristics(HttpServletRequest request, HttpServletResponse response, @PathVariable(Parameters.CHARACTERISTIC_TYPE) String characteristicType) throws Exception {
		BigInteger logId = logService.logRequest(request);
//		try (CharacteristicMetadataTransformer transformer = new CharacteristicMetadataTransformer(response)) {
			CharacteristicMetadataTransformer transformer = new CharacteristicMetadataTransformer(response);
			Map<String, Object> parameterMap = new HashMap<> ();
			parameterMap.put(Parameters.CHARACTERISTIC_TYPE, characteristicType.toLowerCase());
			addContentHeader(response);
			streamResults(transformer, BaseDao.CHARACTERISTICS_METADATA, parameterMap);
//		} catch (Exception e) {
//			LOG.error(e.getLocalizedMessage());
//			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
//		}
		logService.logRequestComplete(logId, response.getStatus());
	}

	@GetMapping(value="{featureSource}/{featureID}/{characteristicType}")
	public void getCharacteristicData(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(LookupDao.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID,
			@PathVariable(Parameters.CHARACTERISTIC_TYPE) String characteristicType,
			@RequestParam(value=Parameters.CHARACTERISTIC_ID, required=false) String[] characteristicIds) throws IOException {
		BigInteger logId = logService.logRequest(request);
		String comid = getComid(featureSource, featureID);
//		try (CharacteristicDataTransformer transformer = new CharacteristicDataTransformer(response)) {
			CharacteristicDataTransformer transformer = new CharacteristicDataTransformer(response);
			Map<String, Object> parameterMap = new HashMap<> ();
			parameterMap.put(Parameters.CHARACTERISTIC_TYPE, characteristicType.toLowerCase());
			parameterMap.put(Parameters.COMID, NumberUtils.parseNumber(comid, Integer.class));
			parameterMap.put(Parameters.CHARACTERISTIC_ID, characteristicIds);
			addContentHeader(response);
			streamResults(transformer, BaseDao.CHARACTERISTIC_DATA, parameterMap);
//		} catch (Exception e) {
//			LOG.error(e.getLocalizedMessage());
//			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
//		}
		logService.logRequestComplete(logId, response.getStatus());
	}

	@GetMapping(value="{featureSource}/{featureID}/basin")
	public void getBasin(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(LookupDao.FEATURE_SOURCE) String featureSource,
			@PathVariable(Parameters.FEATURE_ID) String featureID) throws Exception {
		BigInteger logId = logService.logRequest(request);
//		try {
			streamBasin(response, getComid(featureSource, featureID));
//		} catch (Exception e) {
//			LOG.error(e.getLocalizedMessage());
//			response.sendError(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
//		}
		logService.logRequestComplete(logId, response.getStatus());
	}
}