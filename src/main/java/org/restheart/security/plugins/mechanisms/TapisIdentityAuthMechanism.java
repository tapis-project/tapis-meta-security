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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.restheart.security.plugins.ConfigurablePlugin.argValue;

public class TapisIdentityAuthMechanism implements AuthMechanism {
  private final String mechanismName;
  private final String securityKernalEndPoint;
  private final String serviceToken;
  private final String tokenHeader;
  private static final String USER = "user";
  private static final String SERVICE = "service";
  private static final String userHeader = new String("X-Tapis-User");
  private static final String tenantHeader = new String("X-Tapis-Tenant");
  
  
  
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisIdentityAuthMechanism.class);
  
  public TapisIdentityAuthMechanism(String mechanismName, Map<String, Object> args)
      throws ConfigurationException {
    this.mechanismName = mechanismName;
    this.securityKernalEndPoint = argValue(args, "securityKernalEndPoint");
    this.serviceToken = argValue(args, "serviceToken");
    this.tokenHeader = argValue(args, "tokenHeader");
    
  }
  
  /**
   *    Authentication outcomes that allow a request to proceed. It also does permission checking up front
   *    so that a permissions check in the authorization handler is a simple isPermitted call.
   *    1. Reads the JWT from the header
   *    2. Determines
   *
   * @param exchange
   * @param securityContext
   * @return
   */
  @Override
  public AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange,
                                                     SecurityContext securityContext) {
    //      - Reads the jwt assertion header from the exchange.
    //      - Determines whether the JWT is valid verifies the JWT signature
    //      - Extracts the account information.
    //      - Extracts the user name from the JWT claims.
  
    Account sa = null;
    TapisJWTProcessor tapisJWTProcessor = new TapisJWTProcessor();
  
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
  
    try {
      if(!tapisJWTProcessor.verifyToken(encodedToken)){
        securityContext.authenticationFailed("Token couldn't be verified","TapisIdentityAuthenticationManager");
        return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
    } catch (TapisSecurityException e) {
      //TODO need to integrate MsgUtils
      e.printStackTrace();
      securityContext.authenticationFailed("Token couldn't be verified","TapisIdentityAuthenticationManager");
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  
  
    try {
      // import and decode
      tapisJWTProcessor.importAndDecodeJwt(encodedToken);
      
      // if we have a valid token then we can process it
      if(tapisJWTProcessor.isValid){
        
        // get the claims out of the token
        TapisTokenClaims claims = tapisJWTProcessor.getClaims();
        
        // we can now check the type of token we have
        String accountType = claims.getAccountType();
        
        if(accountType.equals(SERVICE)){
          if(!validateServiceRequestHeaders(exchange)){
            LOGGER.debug("If X-Tapis-Headers don't validate deny request");
            securityContext.authenticationFailed("inValid Headers in request","TapisIdentityAuthenticationManager");
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
          }
          // we have valid headers, valid token and ready to populate our empty Account
          sa = processServiceTokenRequest(exchange, claims);
          // processServiceTokenRequest populated our Account in preparation for authorzation check
          // sa Account is put back in the exchange security context to be used authorization
          
        }else {
          // this has to be a user token so we can't have a user header
          // if user header present we need to reject this request.
          if(claims.getAccountType() != USER){
            LOGGER.debug("Not a User token but can't be anything else; deny request");
            securityContext.authenticationFailed("Token not a User token but should be","TapisIdentityAuthenticationManager");
            return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
          }
  
          sa = processUserTokenRequest(exchange, claims);
        }
        
        // Account is valid and verified pass this along to authorization plugin
        securityContext.authenticationComplete(sa, "TapisIdentityAuthenticationManager", true);
        return AuthenticationMechanismOutcome.AUTHENTICATED;
        
      }else{
        LOGGER.debug("Our token failed Validation for some reason. We should not continue");
        securityContext.authenticationFailed("Token inValid","TapisIdentityAuthenticationManager");
        return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
    } catch (TapisSecurityException e) {
      LOGGER.debug("We caught an exception during token processing");
      e.printStackTrace();
      securityContext.authenticationFailed("Token couldn't be processed","TapisIdentityAuthenticationManager");
      return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
    }
  }
  
  /**
   * Validate the proper Headers and values are in the request for Service to Service call
   * @param exchange
   * @return true or false
   */
  private boolean validateServiceRequestHeaders(HttpServerExchange exchange){
    // the headers must contain X-Tapis-User and X-Tapis-Tenant
    // and these must not be null or not empty values
    boolean userHeaderExists = exchange.getRequestHeaders().contains(userHeader);
    boolean tenantHeaderExists = exchange.getRequestHeaders().contains(tenantHeader);
    if (userHeaderExists && tenantHeaderExists){
      String userName = exchange.getRequestHeaders().get(userHeader).getFirst();
      String tenantName = exchange.getRequestHeaders().get(tenantHeader).getFirst();
      if (notEmpty(userName) && notEmpty(tenantName)){
        return true;
      }
    }
    // doesn't meet the criteria
    return false;
  }
  
  /**
   * Simple check to see if header values returned actually have something in them
   * @param str
   * @return true or false
   */
  private static boolean notEmpty(String str){
    if (str != null && !str.trim().isEmpty()) {
      return true;
    } else {
      return false;
    }
  }
  
  private boolean validateUserRequestHeaders(){
    
    return false;
  }
  
  private Account processUserTokenRequest(HttpServerExchange exchange,
                                          TapisTokenClaims claims) {
    Account account = null;
    if (validateUserRequestHeaders()){
  
      TapisMetaSKClient client = new TapisMetaSKClient(this.securityKernalEndPoint,this.serviceToken);
      String user = claims.getUserName();
      String tenant = claims.getTenantId();
      client.setUser(user);
      client.setTenantId(tenant);
  
      // lets convert the uripath and http method  something we can use to call isPermitted on the SK
      String requestPath = exchange.getRelativePath();
      String requestMethod = exchange.getRequestMethod().toString();
  
      TapisSKPermissionsMapper mapper = new TapisSKPermissionsMapper(requestPath,tenant);
      String permSpec = mapper.convert(requestMethod);
  
      boolean permitted = client.isPermitted(user,permSpec);
  
      // we need to create an Account instance to pass to our Authorizer with isPermitted set.
      Set roles = new HashSet();
      account = new TapisAccount(user,roles,tenant, permitted);
  
    }
    return account;
  }
  
  /**
   * Processing a request with a service token and headers
   * @param exchange
   *
   */
  private Account processServiceTokenRequest(HttpServerExchange exchange, TapisTokenClaims claims) {
    // For a service token we must have valid user and tenant headers
    // valid headers requires user and tenant headers exist and non empty
  
    Account account = null;
    
    // what can go wrong?
    //  headers are invalid reject the request
    // the SK can't be contacted so "no can do", rejects our request or processing the response fails
    // we don't initialize the Account because something fails
    if (validateServiceRequestHeaders(exchange)){
      // get user from Header
      String headerUser = exchange.getRequestHeaders().get(userHeader).getFirst();
      // get tenant from Header
      String headerTenant = exchange.getRequestHeaders().get(tenantHeader).getFirst();
      
      TapisMetaSKClient client = new TapisMetaSKClient(this.securityKernalEndPoint,this.serviceToken);
      client.setUser(headerUser);
      client.setTenantId(headerTenant);
      
      // lets convert the uripath and http method  something we can use to call isPermitted on the SK
      String requestPath = exchange.getRelativePath();
      String requestMethod = exchange.getRequestMethod().toString();
      
      TapisSKPermissionsMapper mapper = new TapisSKPermissionsMapper(requestPath,headerTenant);
      String permSpec = mapper.convert(requestMethod);
      
      boolean permitted = client.isPermitted(headerUser,permSpec);
      
      // we need to create an Account instance to pass to our Authorizer with isPermitted set.
      Set roles = new HashSet();
      account = new TapisAccount(headerUser,roles,headerTenant, permitted);
      
      return account;
      
// create a new TapisAccount and get the roles from our response and put them in the Account roles
//      if(skResponse != null){
//        TapisSKRolesToAccountRolesMapper accountRolesMapper = new TapisSKRolesToAccountRolesMapper();
//        Set roles = (Set)accountRolesMapper.getSKRolesList(skResponse);
//        sa = new TapisAccount(claims.getUserName(),roles,claims.getTenantId());
//
//      }else {
//        LOGGER.debug("We got a null security kernel response for some reason and need to abort");
//        securityContext.authenticationFailed("Security Kernel response was empty","TapisIdentityAuthenticationManager");
//        return AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
//      }
    }
    return account;
  
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
  
  private String getHeaderValue(HttpServerExchange hse, String headerName) {
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
