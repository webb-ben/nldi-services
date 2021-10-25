package gov.usgs.owi.nldi.services;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import gov.usgs.owi.nldi.services.ConfigurationService;
import org.springframework.util.StringUtils;

@Component
public class PyGeoApiService {
    private static final Logger LOG = LoggerFactory.getLogger(PyGeoApiService.class);

    // 15 minutes, default is infinite
    private static int connection_ttl            = 15 * 60 * 1000;
    private static int connections_max_total     = 256;
    private static int connections_max_route     = 32;
    // 5 minutes, default is infinite
    private static int client_socket_timeout     = 5 * 60 * 1000;
    // 15 seconds, default is infinite
    private static int client_connection_timeout = 15 * 1000;

    private String BASE_URL;
    private final String FLOWTRACE_PATH = "/processes/nldi-flowtrace/jobs";
    private final String SPLIT_CATCHMENT_PATH = "/processes/nldi-splitcatchment/jobs";

    // key and value constants for pygeoapi requests
    private final String INPUTS = "inputs";
    private final String LAT = "lat";
    private final String LON = "lon";
    private final String RAINDROP_TRACE = "raindroptrace";
    private final String UPSTREAM = "upstream";
    private final String ID = "id";
    private final String TYPE = "type";
    private final String VALUE = "value";
    private final String TEXT_PLAIN = "text/plain";
    private final String DIRECTION = "direction";

    public enum Direction {
        UP,
        DOWN,
        NONE
    }

    PyGeoApiService(ConfigurationService configurationService) {
        BASE_URL = StringUtils.trimTrailingCharacter(configurationService.getPygeoapiUrl(), '/');
    }

    private HttpClient getHttpClient() {
        PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(
                connection_ttl, TimeUnit.MILLISECONDS);
        clientConnectionManager.setMaxTotal(connections_max_total);
        clientConnectionManager.setDefaultMaxPerRoute(connections_max_route);

        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(client_connection_timeout)
                .setSocketTimeout(client_socket_timeout)
                .build();

        HttpClient httpClient = HttpClients.custom()
                .setConnectionManager(clientConnectionManager)
                .setDefaultRequestConfig(config)
                .build();

        return httpClient;
    }

    public JSONObject nldiFlowTrace(String lat, String lon, Boolean raindroptrace, Direction direction) throws JSONException, IOException {

        // see https://labs.waterdata.usgs.gov/api/nldi/pygeoapi/openapi?f=html#/nldi-splitcatchment/executeNldi-splitcatchmentJob
        // for json request structure
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter jsonStringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonStringWriter);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart(INPUTS);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, LAT);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, lat);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, LON);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, lon);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, RAINDROP_TRACE);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, Boolean.toString(raindroptrace));
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, DIRECTION);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, direction.name().toLowerCase());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        StringEntity requestEntity = new StringEntity(
                jsonStringWriter.toString(),
                ContentType.APPLICATION_JSON
        );

        HttpClient httpClient = getHttpClient();

        HttpPost postMethod = new HttpPost(BASE_URL + FLOWTRACE_PATH);
        postMethod.setEntity(requestEntity);

        // send post request
        HttpResponse httpResponse = httpClient.execute(postMethod);

        StatusLine responseStatus = httpResponse.getStatusLine();
        // throw exception on 404 or 500 status codes
        if (responseStatus.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ||
            responseStatus.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            LOG.error(responseStatus.getReasonPhrase());
            throw new RuntimeException("Failed to get response for flow trace");
        }

        // return response
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        JSONObject returnJson = new JSONObject(responseString);
        return returnJson;
    }

    public Map<String, String> getNldiFlowTraceIntersectionPoint(String lat, String lon, Boolean raindroptrace, Direction direction) throws Exception {
        JSONObject flowtraceJson = nldiFlowTrace(lat, lon, raindroptrace, direction);

        if (!flowtraceJson.has("outputs")) {
            throw new Exception("Malformed response from pygeoapi nldi-flowtrace.");
        }

        JSONArray featureArray = flowtraceJson.getJSONObject("outputs").getJSONArray("features");

        Map<String, String> intersection = new HashMap<>();

        JSONObject currentObject;
        // find the feature that contains the intersection point
        for (int i = featureArray.length() - 1; i >= 0; i--) {
            currentObject = featureArray.getJSONObject(i);
            if (currentObject.has("properties") &&
                    currentObject.getJSONObject("properties").has("intersectionPoint")) {
                JSONArray intersectionPoint = currentObject.getJSONObject("properties").getJSONArray("intersectionPoint");
                intersection.put(LAT, intersectionPoint.getString(0));
                intersection.put(LON, intersectionPoint.getString(1));
                break;
            }
        }

        return intersection;
    }

    public JSONObject nldiSplitCatchment(String lat, String lon, Boolean upstream) throws JSONException, IOException {

        // see https://labs.waterdata.usgs.gov/api/nldi/pygeoapi/openapi?f=html#/nldi-splitcatchment/executeNldi-splitcatchmentJob
        // for json request structure
        JsonFactory jsonFactory = new JsonFactory();
        StringWriter jsonStringWriter = new StringWriter();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(jsonStringWriter);
        jsonGenerator.writeStartObject();
        jsonGenerator.writeArrayFieldStart(INPUTS);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, LAT);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, lat);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, LON);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, lon);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField(ID, UPSTREAM);
        jsonGenerator.writeStringField(TYPE, TEXT_PLAIN);
        jsonGenerator.writeStringField(VALUE, Boolean.toString(upstream));
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        StringEntity requestEntity = new StringEntity(
                jsonStringWriter.toString(),
                ContentType.APPLICATION_JSON
        );

        HttpClient httpClient = getHttpClient();

        HttpPost postMethod = new HttpPost(BASE_URL + SPLIT_CATCHMENT_PATH);
        postMethod.setEntity(requestEntity);

        // send post request
        HttpResponse httpResponse = httpClient.execute(postMethod);

        StatusLine responseStatus = httpResponse.getStatusLine();
        // throw exception on 404 or 500 status codes
        if (responseStatus.getStatusCode() == HttpStatus.SC_INTERNAL_SERVER_ERROR ||
                responseStatus.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
            LOG.error(responseStatus.getReasonPhrase());
            throw new RuntimeException("Failed to get response for split catchment");
        }

        // return response
        String responseString = EntityUtils.toString(httpResponse.getEntity());
        JSONObject returnJson = new JSONObject(responseString);
        return returnJson;
    }

}
