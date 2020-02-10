package org.restheart.security.plugins.mechanisms;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.restheart.security.exceptions.TapisSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes the JWT Header token and verifies it against Tapis public tenant key
 *
 * @author sterry1
 */public class TapisJWTProcessor {
  /* ********************************************************************** */
  /*                               Constants                                */
  /* ********************************************************************** */
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisJWTProcessor.class);
 
  // Header key for jwts.
  private static final String TAPIS_JWT_HEADER = "X-Tapis-Token";
  
  /* ********************************************************************** */
  /*                                Fields                                  */
  /* ********************************************************************** */
  // The public key used to check the JWT signature.
  private static String _jwtPublicKey;
  // the decoded remnant jwt that can produce a json version of claims body.
  private Jwt remantJwt = null;
  // full jwt that can be verified.
  private Jwt fullJwt = null;
  
  private TapisTokenClaims claims = null;
  // will set to true if jwt token can be decoded and claims extracted.
  public boolean isValid = false;
  public boolean isVerified = false;
  
  
  public Jwt getRemantJwt() {
    return remantJwt;
  }
  public Jwt getFullJwt() { return fullJwt; }

  /* ---------------------------------------------------------------------- */
  /* Import and Decode Jwt:                                                             */
  /* ---------------------------------------------------------------------- */
  /**
   * Import & Decode the jwt
   *
   * @param encodedJWT the JWT from the request header
   * @return the decoded jwt or null if invalid or can't be decoded
   */
  public Jwt importAndDecodeJwt(String encodedJWT) throws TapisSecurityException {
    // defence.
    if (encodedJWT == null) {
      LOGGER.debug("The encoded jwt was null");
      return null;
    }
    
    // import jwt token to decode.
    String remnant = encodedJWT;
    int lastDot = encodedJWT.lastIndexOf(".");
    if (lastDot + 1 < encodedJWT.length()) // should always be true
      remnant = encodedJWT.substring(0, lastDot + 1);
     
    // We parse the encoded jwt into valid jwt structure or we throw an exception
    // to let us know what happened
    try {
      this.remantJwt = Jwts.parser().parse(remnant);
    }
    catch (Exception e) {
      // The decode may have detected an expired JWT.
      String msg;
      String emsg = e.getMessage();
      if (emsg != null && emsg.startsWith("JWT expired at"))
        msg = "TAPIS_SECURITY_JWT_EXPIRED";
      else
        msg = "TAPIS_SECURITY_JWT_PARSE_ERROR";
      
      LOGGER.error(msg, e);
      throw new TapisSecurityException(msg, e);
    }
    // assume decoded jwt is ready to extract claims
    if (this.remantJwt !=null){
      // return the claims
      this.claims = new TapisTokenClaims((Claims) this.remantJwt.getBody());
      this.isValid = true;
    }
    return this.remantJwt;
  }
  
  public boolean verifyToken(String encodedJWT) throws TapisSecurityException {
    // defence.
    if (encodedJWT == null) {
      LOGGER.debug("The encoded jwt was null");
      return isVerified;
    }
    try {
      TapisTenantKeys tapisTenantKeys = TapisTenantKeys.getInstance();
      this.fullJwt = Jwts.parser().setSigningKey(tapisTenantKeys.getTenantPublicKey("dev")).parse(encodedJWT);
      isVerified = true;
    }
    catch (Exception e) {
      // The decode may have detected an expired JWT.
      String msg;
      String emsg = e.getMessage();
      if (emsg != null && emsg.startsWith("JWT expired at"))
        msg = "TAPIS_SECURITY_JWT_EXPIRED";
      else
        msg = "TAPIS_SECURITY_JWT_PARSE_ERROR";
      
      LOGGER.error(msg, e);
      throw new TapisSecurityException(msg, e);
    }
    return isVerified;
  }

  /* ---------------------------------------------------------------------- */
  /* getClaims:                                                             */
  /* ---------------------------------------------------------------------- */
  /**
   * get the current claims; assumes jwt has been decoded
   * @return the claims or null if none found
   */
  public TapisTokenClaims getClaims(){
    // claims have already been extracted
    if (this.claims != null){
      return this.claims;
    }
    return this.claims;
  }
  
  
  /* ---------------------------------------------------------------------- */
  /* getJwtPublicKey:                                                       */
  /* ---------------------------------------------------------------------- */
  /** Return the cached public key if it exists.  If it doesn't exist, load it
   * and then return it.
   *
   * @return jwt Public Key
   */
  private String getJwtPublicKey() throws TapisSecurityException {
    // Use the cached copy if it has already been loaded.
    if (_jwtPublicKey != null) return _jwtPublicKey;
    
    //_jwtPublicKey = TapisTenantKeys.getInstance();
    return _jwtPublicKey;
  }
  
  public static void main(String[] args) {
    TapisJWTProcessor jwtp = new TapisJWTProcessor();
  }
}
