package org.restheart.security.plugins.mechanisms;

import org.restheart.security.exceptions.TapisSecurityException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@Test(groups= {"unit"})
public class TapisJWTProcessorTestOrig {
  
  /* ---------------------------------------------------------------------- */
  /* DataProvider: Setting Parameters for the test                          */
  /* ---------------------------------------------------------------------- */
  @DataProvider(name = "keyStoreFileCrendentials")
  public Object[][] createData1() {
    return new Object[][] {
        {"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD81Q"},
        {"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD"},
        {""}
    };
  }
  
  // token
  String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD81Q";
  
  /* ********************************************************************** */
  /*                              Tests                                     */
  /* ********************************************************************** */
  /* ----------------------------------------------------------------------- */
  /* :                                                              */
  /* ----------------------------------------------------------------------- */
  
  @Test(enabled=true)
  public void test() {
  }
  
  public static void main(String[] args) {

    TapisJWTProcessor jwtProcessor = new TapisJWTProcessor();
    String user = null;
    // import and decode a token string from http request headers
    //jwtProcessor.importAndDecodeJwt(token);
    //boolean bool = jwtProcessor.verifyToken(token);
  
    TapisTokenClaims claims = jwtProcessor.getClaims();
    // check to see if jwt validated then spit out some of the claims
    if (jwtProcessor.isValid){
      System.out.println(claims.getUserName()+" "+claims.getIssuer()+" "+claims.getSubject());
    }
    System.out.println();
  }
}
