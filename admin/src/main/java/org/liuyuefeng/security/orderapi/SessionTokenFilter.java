package org.liuyuefeng.security.orderapi;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Component
public class SessionTokenFilter extends ZuulFilter
{
  private RestTemplate restTemplate = new RestTemplate();
  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 0;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();
    TokenInfo tokenInfo = (TokenInfo)request.getSession().getAttribute("token");
    if(tokenInfo != null){
      String tokenValue = tokenInfo.getAccess_token();
      if(tokenInfo.isExpired()){
        String oauthServiceUrl = "http://gateway.security.liuyuefeng.org:9070/token/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("admin", "123456");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", tokenInfo.getRefresh_token());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try{
          ResponseEntity<TokenInfo> newToken = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
          request.getSession().setAttribute("token", newToken.getBody().init());
          tokenValue = newToken.getBody().getAccess_token();
        }catch (Exception e){
          requestContext.setSendZuulResponse(false);
          requestContext.setResponseStatusCode(500);
          requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
          requestContext.getResponse().setContentType("application/json");
        }
      }
      requestContext.addZuulRequestHeader("Authorization", "bearer " + tokenValue);
    }
    return null;
  }
}
