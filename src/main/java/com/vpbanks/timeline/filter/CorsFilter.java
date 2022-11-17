package com.vpbanks.timeline.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vpbanks.timeline.constants.ErrorConstant;
import com.vpbanks.timeline.response.ResponseDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(CorsFilter.class);

    @Autowired
    private Environment env;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @SuppressWarnings({ "unchecked", "deprecation" })

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        ApiKeyVerifiRequestWrapper requestWrapper = new ApiKeyVerifiRequestWrapper(request);

        String requestId = "";
        try {
            requestId = requestWrapper.getHeader("request_id");
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }

            JSONParser parser = new JSONParser();
            JSONObject dataRequest = StringUtils.isEmpty(requestWrapper.getBody()) ? new JSONObject()
                    : (JSONObject) parser.parse(requestWrapper.getBody());

            Object reqId = dataRequest.get("request_id");
            if (reqId == null) {
                dataRequest.put("request_id", requestId);
            }
            dataRequest.put("uri", request.getRequestURI());
            requestWrapper.setBody(dataRequest.toString());

            chain.doFilter(requestWrapper, res);
        } catch (Exception e) {
            logger.error(e.toString(), e);
            buildExceptionResponse(response, requestId, e.getMessage());
        }
    }

    @Override
    public void destroy() {
    }

    private byte[] restResponseBytes(ResponseDto responseDto) throws IOException {
        String serialized = new ObjectMapper().writeValueAsString(responseDto);
        return serialized.getBytes();
    }

    private void buildExceptionResponse(HttpServletResponse response, String requestId, String msg) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getOutputStream()
                    .write(restResponseBytes(new ResponseDto(ErrorConstant.ERR_5001001, msg, requestId, 0, null)));
        } catch (IOException e) {
            logger.error(e.toString(), e);
        }
    }

}