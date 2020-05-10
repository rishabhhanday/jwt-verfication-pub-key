package com.practice.basicrestapi.config;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PublicKeyProvider {

  public static final String PUB_KEY_LOCATION =
      ResourceLoader.CLASSPATH_URL_PREFIX + "keys/pub_key.pem";
  private static final String PUB_KEY_PREFIX = "-----BEGIN PUBLIC KEY-----";
  private static final String PUB_KEY_POSTFIX = "-----END PUBLIC KEY-----";
  @Autowired
  private ResourceLoader resourceLoader;

  private PublicKey generatePublicKey() {
    try {
      String publicKey = getPublicKeyFromPemFile();

      if (publicKey != null) {
        byte[] decode = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpecX509);
      }
      return null;
    } catch (Exception ex) {
      log.warn("Error generating public key");
      return null;
    }
  }

  private String getPublicKeyFromPemFile() throws IOException {
    Resource resource = resourceLoader.getResource(PUB_KEY_LOCATION);

    if (resource.exists()) {
      File f = resource.getFile();

      try (DataInputStream dis = new DataInputStream(new FileInputStream(f))) {
        byte[] keyBytes = new byte[(int) f.length()];
        dis.readFully(keyBytes);
        String publicKey = new String(keyBytes);

        return publicKey.replaceAll("\\r\\n", "").replace(PUB_KEY_PREFIX, "")
            .replace(PUB_KEY_POSTFIX, "");
      }
    } else {
      log.warn("Public key missing");
      return null;
    }
  }

  public PublicKey getPublicKey() {
    return generatePublicKey();
  }
}
