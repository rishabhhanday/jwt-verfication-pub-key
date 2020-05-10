package com.practice.basicrestapi.controller;

import com.practice.basicrestapi.JwtToken;
import com.practice.basicrestapi.config.PublicKeyProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import java.security.PublicKey;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

  @Autowired
  private PublicKeyProvider publicKeyProvider;
  private PublicKey publicKey;

  @PostConstruct
  private void getPublicKey() {
    this.publicKey = publicKeyProvider.getPublicKey();
  }

  @GetMapping("/testJwt")
  public String testJWT(@RequestBody JwtToken jwtToken) {
    if (publicKey == null) {
      log.error("Public key missing");
      return null;
    }
    try {
      Jws<Claims> claimsJws = Jwts.parser().setSigningKey(publicKey)
          .parseClaimsJws(jwtToken.getJwt());

      return claimsJws.getBody().toString();
    } catch (Exception exception) {
      log.error("Invalid JWT {}", exception.getMessage());
      return null;
    }
  }
}
