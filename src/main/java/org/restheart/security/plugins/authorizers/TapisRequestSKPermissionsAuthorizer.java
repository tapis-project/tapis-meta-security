package org.restheart.security.plugins.authorizers;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import org.restheart.security.plugins.Authorizer;

import java.security.Principal;
import java.util.Set;
import java.util.TreeMap;

import static com.google.common.collect.Sets.newHashSet;
import static io.undertow.predicate.Predicate.PREDICATE_CONTEXT;

public class TapisRequestSKPermissionsAuthorizer implements Authorizer {
  
  @Override
  public boolean isAllowed(HttpServerExchange exchange) {
    // does our account have roles?
    // this fixes undertow bug 377
    // https://issues.jboss.org/browse/UNDERTOW-377
    if (exchange.getAttachment(PREDICATE_CONTEXT) == null) {
      exchange.putAttachment(PREDICATE_CONTEXT, new TreeMap<>());
    }
  
    // Predicate.resolve() uses getRelativePath() that is the path relative to
    // the last PathHandler We want to check against the full request path
    // see https://issues.jboss.org/browse/UNDERTOW-1317
    exchange.setRelativePath(exchange.getRequestPath());
  
  
    return false;
  }
  
  @Override
  public boolean isAuthenticationRequired(HttpServerExchange httpServerExchange) {
    // don't require authentication for OPTIONS requests (we don't use them in Tapis)
    
    // if acl is null return true  acl was populated from the config file   ( we won't use it )
    // except for our liveness and amiup services which are unauthenticated endpoints.
    
    
    // is this a service account jwt?
    // or
    // user account jwt
    
    // check the path against the role ???
    // ok how do we map roles to dbs?
    
    
    
    
    
    
    
    return true;
  }
  
  private Account account(HttpServerExchange exchange) {
    final Account account = exchange.getSecurityContext()
                                    .getAuthenticatedAccount();
    return isAuthenticated(account) ? account : new TapisRequestSKPermissionsAuthorizer.NotAuthenticatedAccount();
  }
  
  private boolean isAuthenticated(Account authenticatedAccount) {
    return authenticatedAccount != null;
  }
 
  private static class NotAuthenticatedAccount implements Account {
    
    private static final long serialVersionUID = 3124L;
    
    @Override
    public Principal getPrincipal() {
      return null;
    }
    
    @Override
    public Set<String> getRoles() {
      return newHashSet("$unauthenticated");
    }
  }
  
  
}

