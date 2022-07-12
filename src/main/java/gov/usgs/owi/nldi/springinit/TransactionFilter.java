package gov.usgs.owi.nldi.springinit;

import gov.usgs.owi.nldi.controllers.BaseController;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class TransactionFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;

        if (httpRequest.getHeader("Accept") == null) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String[] requestHeaders = httpRequest.getHeader("Accept").split("\\s*,\\s*");
        ArrayList<String> headerList = new ArrayList<>();
        Collections.addAll(headerList, requestHeaders);

        // customize enforcement of Accept header order
        // this helps decide when to display html pages
        if (headerList.contains(MediaType.TEXT_HTML_VALUE)) {
            if (headerList.contains(MediaType.APPLICATION_JSON_VALUE)
                    && headerList.indexOf(MediaType.APPLICATION_JSON_VALUE) > headerList.indexOf(MediaType.TEXT_HTML_VALUE)) {
                headerList.remove(MediaType.APPLICATION_JSON_VALUE);
                headerList.remove(MediaType.ALL_VALUE);
            } else if (headerList.contains(BaseController.MIME_TYPE_GEOJSON)
                    && headerList.indexOf(BaseController.MIME_TYPE_GEOJSON) > headerList.indexOf(MediaType.TEXT_HTML_VALUE)) {
                headerList.remove(BaseController.MIME_TYPE_GEOJSON);
                headerList.remove(MediaType.ALL_VALUE);
            } else if (headerList.contains(BaseController.MIME_TYPE_JSONLD)
                    && headerList.indexOf(BaseController.MIME_TYPE_JSONLD) > headerList.indexOf(MediaType.TEXT_HTML_VALUE)) {
                headerList.remove(BaseController.MIME_TYPE_JSONLD);
                headerList.remove(MediaType.ALL_VALUE);
            } else if (headerList.contains(MediaType.APPLICATION_JSON_VALUE)
                    || headerList.contains(BaseController.MIME_TYPE_GEOJSON)
                    || headerList.contains(BaseController.MIME_TYPE_JSONLD)) {
                headerList.remove(MediaType.TEXT_HTML_VALUE);
                headerList.remove(MediaType.ALL_VALUE);
            }
        }

        MutableHttpServletRequest mutableRequest = new MutableHttpServletRequest(httpRequest);
        mutableRequest.putHeader("Accept", String.join(",", headerList));

        filterChain.doFilter(mutableRequest, servletResponse);
    }
}
