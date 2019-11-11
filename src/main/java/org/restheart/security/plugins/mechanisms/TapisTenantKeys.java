package org.restheart.security.plugins.mechanisms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Public keys class for tenant public key lookup
 */
public class TapisTenantKeys {
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisTenantKeys.class);
  
  // private instance, so that it can be
  // accessed by only by getInstance() method
  private static TapisTenantKeys instance;
  public Map<String, String> publicKeys;
  
  /**
   *
   */
  private TapisTenantKeys()
  {
    // Map of tenants and thier public keys to use in JWT verification
    publicKeys = new HashMap<String, String>() {{
      put("dev", "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz7rr5CsFM7rHMFs7uKIdcczn0uL4ebRMvH8pihrg1tW/fp5Q+5ktltoBTfIaVDrXGF4DiCuzLsuvTG5fGElKEPPcpNqaCzD8Y1v9r3tfkoPT3Bd5KbF9f6eIwrGERMTs1kv7665pliwehz91nAB9DMqqSyjyKY3tpSIaPKzJKUMsKJjPi9QAS167ylEBlr5PECG4slWLDAtSizoiA3fZ7fpngfNr4H6b2iQwRtPEV/EnSg1N3Oj1x8ktJPwbReKprHGiEDlqdyT6j58l/I+9ihR6ettkMVCq7Ho/bsIrwm5gP0PjJRvaD5Flsze7P4gQT37D1c5nbLR+K6/T0QTiyQIDAQAB");
      put("nextTenant", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJtGvK8P6tP+K35PIxh713Vw0ZecWNaK31Lkz7aSJJYKNZ4gpgS+5+5bRZCzoNs3DSho3wh2g6sipnvOzo35bIo2Pb6SJ3rk3/PJ6SsyR0bh0NF7oSDGVJvNCImZAWRXxh5HENnsfMxJZrVQR9ZDQaaZ9awccX9S2L2WVMMniZMwIDAQAB");
    }};
  }
  
  /**
   *
   * @param tenantName
   * @return
   */
  public PublicKey getTenantPublicKey(String tenantName){
    byte[] publicBytes = Base64.getDecoder().decode(this.publicKeys.get(tenantName));
    PublicKey publicKey = null;
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
    KeyFactory keyFactory = null;
    try {
      keyFactory = KeyFactory.getInstance("RSA");
      publicKey = keyFactory.generatePublic(keySpec);
    } catch (NoSuchAlgorithmException e) {
      LOGGER.debug("An incorrect or unavailable algorithm was requested");
      e.printStackTrace();
    } catch (InvalidKeySpecException e) {
      LOGGER.debug("An invalid Key construction was attempted.");
      e.printStackTrace();
    }
    return publicKey;
  }
  
  /**
   *
   * @return
   */
  public static TapisTenantKeys getInstance()
  {
    if (instance == null)
    {
      // if instance is null, initialize
      instance = new TapisTenantKeys();
    }
    return instance;
  }
}