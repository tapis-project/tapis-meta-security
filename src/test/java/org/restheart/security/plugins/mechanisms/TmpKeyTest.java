package org.restheart.security.plugins.mechanisms;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class TmpKeyTest {
  public PublicKey getJwtPublicKey()throws NoSuchAlgorithmException, InvalidKeySpecException {
    // String TempPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDJtGvK8P6tP+K35PIxh713Vw0ZecWNaK31Lkz7aSJJYKNZ4gpgS+5+5bRZCzoNs3DSho3wh2g6sipnvOzo35bIo2Pb6SJ3rk3/PJ6SsyR0bh0NF7oSDGVJvNCImZAWRXxh5HENnsfMxJZrVQR9ZDQaaZ9awccX9S2L2WVMMniZMwIDAQAB";
    String TempPublicKey ="MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz7rr5CsFM7rHMFs7uKIdcczn0uL4ebRMvH8pihrg1tW/fp5Q+5ktltoBTfIaVDrXGF4DiCuzLsuvTG5fGElKEPPcpNqaCzD8Y1v9r3tfkoPT3Bd5KbF9f6eIwrGERMTs1kv7665pliwehz91nAB9DMqqSyjyKY3tpSIaPKzJKUMsKJjPi9QAS167ylEBlr5PECG4slWLDAtSizoiA3fZ7fpngfNr4H6b2iQwRtPEV/EnSg1N3Oj1x8ktJPwbReKprHGiEDlqdyT6j58l/I+9ihR6ettkMVCq7Ho/bsIrwm5gP0PjJRvaD5Flsze7P4gQT37D1c5nbLR+K6/T0QTiyQIDAQAB";
    byte[] publicBytes = Base64.getDecoder().decode(TempPublicKey);
    PublicKey publicKey;
    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    publicKey = keyFactory.generatePublic(keySpec);
    return publicKey;
  }
  public static void main(String[] args) throws InvalidKeySpecException, NoSuchAlgorithmException {
    TmpKeyTest tmpKeyTest = new TmpKeyTest();
    String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD81Q";
    //PublicKey publicKey = TapisTenantKeys.getInstance().getTenantPublicKey("dev");
    PublicKey publicKey = tmpKeyTest.getJwtPublicKey();
    Jwt jwt = Jwts.parser().setSigningKey(publicKey).parse(token);
    System.out.println();
    
    
  }
}
