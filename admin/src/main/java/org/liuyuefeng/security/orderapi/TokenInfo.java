package org.liuyuefeng.security.orderapi;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TokenInfo {
  private String access_token;
  private String refresh_token;
  private String token_type;
  private Long expires_in;
  private String scope;

  private LocalDateTime expireTime;

  public TokenInfo init(){
    //考虑传输时延，失效前三秒就判断失效
    expireTime = LocalDateTime.now().plusSeconds(expires_in - 3);
    return this;
  }
  public boolean isExpired(){
    return expireTime.isBefore(LocalDateTime.now());
  }

}
