package org.restheart.security.plugins.mechanisms;

import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HeaderValues;
import io.undertow.util.HttpString;
import org.restheart.security.ConfigurationException;
import org.restheart.security.exceptions.TapisSecurityException;
import org.restheart.security.plugins.AuthMechanism;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static org.restheart.security.plugins.ConfigurablePlugin.argValue;

public class TapisIdentityAuthMechanism implements AuthMechanism {
  private final String mechanismName;
  private final String securityKernalEndPoint;
  private final String serviceToken;
  private final String tokenHeader;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisIdentityAuthMechanism.class);
  
  public TapisIdentityAuthMechanism(String mechanismName, Map<String, Object> args)
      throws ConfigurationException {
    this.mechanismName = mechanismName;
    this.securityKernalEndPoint = argValue(args, "securityKernalEndPoint");
    this.serviceToken = argValue(args, "serviceToken");
    this.tokenHeader = argValue(args, "tokenHeader");
    
  }
  
  @Override
  public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange,
                                                     SecurityContext securityContext) {
    //      - Reads the jwt assertion header from the exchange.
    //      - Determines whether the JWT is valid verifies the JWT signature
    //      - Extracts the account information.
    //      - Extracts the user name from the JWT claims.
  
    Account sa = null;
    
    HeaderMap headerMap = exchange.getRequestHeaders();
    // find our header and process the JWT token
    if (!headerMap.contains(this.tokenHeader)){
      LOGGER.debug("Did not find the Tapis Auth Header");
      LOGGER.debug("We can't proceed with authentication without this token header");
      securityContext.authenticationFailed("Did not find the Tapis Auth Header","TapisIdentityAuthenticationManager");
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    
    // retrieve the encoded token from the Tapis header
    // and if there is no token to process return NOT_AUTHENTICATED
    String encodedToken = getToken(exchange);
    if(encodedToken == null){
      LOGGER.debug("Did not get a token from the header");
      LOGGER.debug("We can't proceed with authentication without this token ");
      securityContext.authenticationFailed("Did not find the Tapis Auth Token","TapisIdentityAuthenticationManager");
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    
    // again if we can't process the token we can't continue and need to return NOT_AUTHENTICATED
    // at this point we have candidate encoded token let's import and decode
    TapisJWTProcessor tapisJWTProcessor = new TapisJWTProcessor();
    try {
      // import and decode
      tapisJWTProcessor.importAndDecodeJwt(encodedToken);
      if(tapisJWTProcessor.isValid){
        TapisTokenClaims claims = tapisJWTProcessor.getClaims();
        
        // we now have enough information to ask the SK for users Roles
        TapisMetaSKClient client = new TapisMetaSKClient(this.securityKernalEndPoint,this.serviceToken);
        String skResponse = client.getSKUserRoles(claims.getUserName());
  
        // create a new TapisAccount and get the roles from our response and put them in the Account roles
        if(skResponse != null){
          TapisSKRolesToAccountRolesMapper accountRolesMapper = new TapisSKRolesToAccountRolesMapper();
          Set roles = (Set)accountRolesMapper.getSKRolesList(skResponse);
          sa = new TapisAccount(claims.getUserName(),roles,claims.getTenantId());
        }
      }
    } catch (TapisSecurityException e) {
      LOGGER.debug("We caught an exception during token prosessing");
      e.printStackTrace();
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
    // lastly check to see if token is verified against the pubic key.
    try {
      if(!tapisJWTProcessor.verifyToken(encodedToken)){
        return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
    } catch (TapisSecurityException e) {
      //TODO need to integrate MsgUtils
      e.printStackTrace();
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  
  
    securityContext.authenticationComplete(sa, "TapisIdentityAuthenticationManager", true);
    return AuthenticationMechanismOutcome.AUTHENTICATED;

  }
  
  @Override
  public ChallengeResult sendChallenge(HttpServerExchange exchange,
                                                               SecurityContext securityContext) {
    return new ChallengeResult(true, 200);
  }
  
  /**
   * @return the mechanismName
   */
  public String getMechanismName() {
    return mechanismName;
  }
  
  /* ---------------------------------------------------------------------- */
  /* getToken:                                                              */
  /* ---------------------------------------------------------------------- */
  /**
   * Gets the jwt header and value out of the request headers
   *   parses the header name for the jwt token string
   * @param hse
   * @return  "jwtToken" string for processing
   */
  private String getToken(HttpServerExchange hse) {
    HeaderMap headers = hse.getRequestHeaders();
    Collection<HttpString> headerNames = headers.getHeaderNames();
    for (HttpString name : headerNames){
      if(name.toString().startsWith(tokenHeader)){
        HeaderValues values = headers.get(name);
        String token = values.getFirst();
        LOGGER.debug("jwt token: "+values.getFirst());
        return token;
      }
    }
    return null;
  }
  
}
