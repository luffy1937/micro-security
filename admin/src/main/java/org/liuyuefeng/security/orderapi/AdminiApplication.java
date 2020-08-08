package org.liuyuefeng.security.orderapi;

import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@RestController
@EnableZuulProxy
@Slf4j
//./ng build --watch 动态更新angular
public class AdminiApplication {
  private RestTemplate restTemplate = new RestTemplate();
/*  不在客户端应用认证，直接在认证服务器
@PostMapping("/login")
  public void login(@RequestBody Credentials credentials, HttpServletRequest request){
    String oauthServiceUrl = "http://localhost:9070/token/oauth/token";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth("admin", "123456");

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("username", credentials.getUsername());
    params.add("password", credentials.getPassword());
    params.add("grant_type", "password");
    params.add("scope", "read write");
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
    ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
    request.getSession().setAttribute("token", response.getBody());

  }*/


@GetMapping("/me")
  public TokenInfo me(HttpServletRequest request){
  TokenInfo info = (TokenInfo)request.getSession().getAttribute("token");
  return info;
  }

  @GetMapping("/oauth/callback")
  public void callback(@RequestParam String code, String state, HttpServletRequest request, HttpServletResponse response) throws IOException {
    log.info("state is " + state);
    String oauthServiceUrl = "http://gateway.security.liuyuefeng.org:9070/token/oauth/token";
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth("admin", "123456");
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("code", code);
    params.add("grant_type", "authorization_code");
    //认证服务器会对比浏览器发出的redirect_uri与此是否一致
    params.add("redirect_uri", "http://admin.security.liuyuefeng.org:9001/oauth/callback");
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
    ResponseEntity<TokenInfo> token = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
   // request.getSession().setAttribute("token", token.getBody().init());
    /**
     * token sso
     */
    Cookie accessTokenCookie = new Cookie("liuyuefeng_access_token", token.getBody().getAccess_token());
    accessTokenCookie.setMaxAge(token.getBody().getExpires_in().intValue());
    accessTokenCookie.setDomain("security.liuyuefeng.org");
    accessTokenCookie.setPath("/");
    response.addCookie(accessTokenCookie);

    Cookie refreshTokenCookie = new Cookie("liuyuefeng_refresh_token", token.getBody().getAccess_token());
    //过期时间设置一个较大的值即可
    accessTokenCookie.setMaxAge(259000);
    accessTokenCookie.setDomain("security.liuyuefeng.org");
    accessTokenCookie.setPath("/");
    response.addCookie(refreshTokenCookie);
    //跳回主页
    response.sendRedirect("/");
  }


  @PostMapping("/logout")
  public void logout(HttpServletRequest request){
    request.getSession().invalidate();
  }

	public static void main(String[] args) {
		SpringApplication.run(AdminiApplication.class, args);
	}

}
