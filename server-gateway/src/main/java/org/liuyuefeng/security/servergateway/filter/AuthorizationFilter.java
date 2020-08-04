package org.liuyuefeng.security.servergateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AuthorizationFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("authorization start");
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        if(isNeedAuth(request)){
            TokenInfo tokenInfo = (TokenInfo)request.getAttribute("tokenInfo");
            if(tokenInfo != null && tokenInfo.isActive()){
                if(!hasPermission(tokenInfo, request)){
                    log.info("audit log update fail 403");
                    handleError(403, requestContext);
                }
                requestContext.addZuulRequestHeader("username", tokenInfo.getUser_name());
            }else{
                if(!StringUtils.startsWith(request.getRequestURI(), "/token")){
                    log.info("audit log update fail 401");
                    handleError(401, requestContext);
                }
            }
        }
        return null;
    }

    private boolean hasPermission(TokenInfo tokenInfo, HttpServletRequest request) {
        return RandomUtils.nextInt()%2 == 0;
    }

    private void handleError(int status, RequestContext requestContext) {
        requestContext.getResponse().setContentType("application/json");
        requestContext.setResponseStatusCode(status);
        requestContext.setResponseBody("{\"message\":\"auth fail\"}");
        //不往下走
        requestContext.setSendZuulResponse(false);
    }

    private boolean isNeedAuth(HttpServletRequest request) {
        return true;
    }
}
