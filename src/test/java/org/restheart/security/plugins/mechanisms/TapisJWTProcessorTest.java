package org.restheart.security.plugins.mechanisms;

import org.restheart.security.exceptions.TapisSecurityException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class TapisJWTProcessorTest {
  
  // token
  String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD81Q";
  
  
  /* ---------------------------------------------------------------------- */
  /* DataProvider: Setting Parameters for the test                          */
  /* ---------------------------------------------------------------------- */
  @DataProvider(name = "importToken")
  public Object[][] createData1() {
    return new Object[][] {
        {true,"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD81Q"},
        {false,"eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoic3RlcnJ5MUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InN0ZXJyeTEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTczNjcxNDA4fQ.ns-1u-zjGPRYeE3HCRHrLf7zLoKVeOFHx_QwZHjpQMzX2m83BEIL5TGD4aphTecir36LNzxxA_xtNNKgZPuTbDDXhMIGPIOaSTjKic6nPAsUWJVb19OEqtnj-Abkm8iTIBHmPiXRfrZuwaE3hvXoZy8pHy08-rQjZz2eew6ZFLlV9BzeXP73_JVXNBCN7JN_ps8GU2U9M1xOPPm4aLwEK0BFPZjgwP5hwJCvPQUodHeaQMZTXIiIyy50AU6Lbyh9amyxGoe1FJRreP48IFjx8nKP4pwngn1yzwuWmgzwAP_05ISCdV_LxaKycy9nt_TiZ0fUZcfjY1gb5T2MMhD"},
        {false,""},
        {false,null}
    };
  }
  
  /* ********************************************************************** */
  /*                              Tests                                     */
  /* ********************************************************************** */
  /* ----------------------------------------------------------------------- */
  /* :                                                              */
  /* ----------------------------------------------------------------------- */
/*
  @Test
  public void testProcessorTest() {
 
  }
*/
  
  @Test(dataProvider = "importToken")
  public void testImportAndDecodeJwt(boolean expected, String token) {
    TapisJWTProcessor jwtP = new TapisJWTProcessor();
    try {
      jwtP.importAndDecodeJwt(token);
    } catch (TapisSecurityException e) {
      e.printStackTrace();
    }
  }
  
/*
  @Test
  public void testGetRemantJwt() {
  }
  
  @Test
  public void testGetFullJwt() {
  }
  
  @Test
  public void testVerifyToken() {
  }
  
  @Test
  public void testGetClaims() {
  }
*/
}