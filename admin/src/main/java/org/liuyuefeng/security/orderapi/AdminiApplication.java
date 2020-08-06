package org.liuyuefeng.security.orderapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@RestController
@EnableZuulProxy
//./ng build --watch 动态更新angular
public class AdminiApplication {
  private RestTemplate restTemplate = new RestTemplate();
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

  }
  @PostMapping("/logout")
  public void logout(HttpServletRequest request){
    request.getSession().invalidate();
  }

	public static void main(String[] args) {
		SpringApplication.run(AdminiApplication.class, args);
	}

}
