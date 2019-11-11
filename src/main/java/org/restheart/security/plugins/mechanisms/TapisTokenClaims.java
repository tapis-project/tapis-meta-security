package org.restheart.security.plugins.mechanisms;

import io.jsonwebtoken.Claims;

import java.util.Date;


public class TapisTokenClaims {
  private Claims _claims;
  // Tapis claim keys.
  private static final String CLAIM_TENANT         = "tapis/tenant_id";
  private static final String CLAIM_USERNAME       = "tapis/username";
  private static final String CLAIM_TOKEN_TYPE     = "tapis/token_type";
  private static final String CLAIM_ACCOUNT_TYPE   = "tapis/account_type";
  private static final String CLAIM_DELEGATION     = "tapis/delegation";
  private static final String CLAIM_DELEGATION_SUB = "tapis/delegation_sub";
  
  // The token types this filter expects.
  private static final String TOKEN_ACCESS = "access";
  
  public TapisTokenClaims(Claims claims) {
      this._claims = claims;
  }
  
  public String getIssuer(){
    return _claims.getIssuer();
  }
  public String getSubject(){
    return _claims.getSubject();
  }
  
  public String getTenantId(){
    return (String)_claims.get(TapisTokenClaims.CLAIM_TENANT);
  }
  
  public String getTokenType(){
    return (String)_claims.get(TapisTokenClaims.CLAIM_TOKEN_TYPE);
  }
  
  /**
   *
   * @return
   */
  public boolean getDelegation(){
    return (boolean)_claims.get(TapisTokenClaims.CLAIM_DELEGATION);
  }
  
  /**
   *
   * @return
   */
  public String getDelegationSub(){
    return (String)_claims.get(TapisTokenClaims.CLAIM_DELEGATION_SUB);
  }
  
  /** Get the user name or return null.
   *
   * @return the simple username  or null
   */
  public String getUserName(){
    // The enduser name may have extraneous information around it.
    String s = (String)_claims.get(TapisTokenClaims.CLAIM_USERNAME);
  
    if (s == null || s.isBlank()) return null;
    else return s;
  }
  
  public String getAccountType(){
    return (String)_claims.get(TapisTokenClaims.CLAIM_ACCOUNT_TYPE);
  }
  
  public Date getExpiration(){
    return _claims.getExpiration();
  }
  
}
