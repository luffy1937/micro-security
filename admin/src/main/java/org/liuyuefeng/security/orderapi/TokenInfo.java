package org.liuyuefeng.security.orderapi;

import lombok.Data;

@Data
public class TokenInfo {
  private String access_token;
  private String token_type;
  private String expires_in;
  private String scope;
}
