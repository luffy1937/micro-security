package org.liuyuefeng.security.orderapi;


import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieTokenFilter extends ZuulFilter{

  private RestTemplate restTemplate = new RestTemplate();

  @Override
  public String filterType() {
    return "pre";
  }

  @Override
  public int filterOrder() {
    return 1;
  }

  @Override
  public boolean shouldFilter() {
    return true;
  }

  @Override
  public Object run() throws ZuulException {
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();
    HttpServletResponse response = requestContext.getResponse();

    String accessToken = getCookie("liuyuefeng_access_token");
    if(StringUtils.isNotBlank(accessToken)){
      requestContext.addZuulRequestHeader("Authorization", "bearer" + accessToken);
    }else {
      String refreshToken = getCookie("liuyuefeng_refresh_token");
      if(StringUtils.isNotBlank(refreshToken)){
        String oauthServiceUrl = "http://gateway.security.liuyuefeng.org:9070/token/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("admin", "123456");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        try{
          ResponseEntity<TokenInfo> newToken = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
          request.getSession().setAttribute("token", newToken.getBody().init());
          Cookie accessTokenCookie = new Cookie("liuyuefeng_access_token", newToken.getBody().getAccess_token());
          accessTokenCookie.setMaxAge(newToken.getBody().getExpires_in().intValue());
          accessTokenCookie.setDomain("security.liuyuefeng.org");
          accessTokenCookie.setPath("/");
          response.addCookie(accessTokenCookie);
          Cookie refreshTokenCookie = new Cookie("liuyuefeng_refresh_token", newToken.getBody().getAccess_token());
          //过期时间设置一个较大的值即可
          accessTokenCookie.setMaxAge(259000);
          accessTokenCookie.setDomain("security.liuyuefeng.org");
          accessTokenCookie.setPath("/");
          response.addCookie(refreshTokenCookie);

          requestContext.addZuulRequestHeader("Authorization", "bearer" + newToken.getBody().getAccess_token());
        }catch (Exception e){
          requestContext.setSendZuulResponse(false);
          requestContext.setResponseStatusCode(500);
          requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
          requestContext.getResponse().setContentType("application/json");
        }
      }else {
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(500);
        requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
        requestContext.getResponse().setContentType("application/json");
      }
    }
    return null;
  }

  private String getCookie(String name) {
    RequestContext requestContext = RequestContext.getCurrentContext();
    HttpServletRequest request = requestContext.getRequest();

    Cookie[] cookies = request.getCookies();
    for(Cookie cookie: cookies){
      if(StringUtils.equals(cookie.getName(), name)){
        return name;
      }
    }
    return null;
  }
}
