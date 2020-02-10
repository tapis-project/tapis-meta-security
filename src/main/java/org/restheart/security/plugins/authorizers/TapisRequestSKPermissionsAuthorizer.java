package org.restheart.security.plugins.authorizers;

import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import org.restheart.security.ConfigurationException;
import org.restheart.security.handlers.exchange.ByteArrayRequest;
import org.restheart.security.plugins.Authorizer;
import org.restheart.security.plugins.FileConfigurablePlugin;
import org.restheart.security.plugins.mechanisms.TapisAccount;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;

import static com.google.common.collect.Sets.newHashSet;
import static io.undertow.predicate.Predicate.PREDICATE_CONTEXT;

public class TapisRequestSKPermissionsAuthorizer extends FileConfigurablePlugin implements Authorizer {
  private final String mechanismName;
  // private final String placeHolder;

  public TapisRequestSKPermissionsAuthorizer(String mechanismName, Map<String, Object> configuration)
      throws ConfigurationException {
    this.mechanismName = mechanismName;
    // this.placeHolder = argValue(args, "placeHolder");

    
  }
  
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
  
    TapisAccount tapisAccount = (TapisAccount) exchange.getSecurityContext().getAuthenticatedAccount();
    return tapisAccount.getIsPermitted();
  }
  
  @Override
  public boolean isAuthenticationRequired(HttpServerExchange httpServerExchange) {
    // don't require authentication for OPTIONS requests (we don't use them in Tapis)
    
    // if acl is null return true  acl was populated from the config file   ( we won't use it )
    // except for our liveness and amiup services which are unauthenticated endpoints.
    
    return false;
  }
  
  private Account account(HttpServerExchange exchange) {
    final Account account = exchange.getSecurityContext()
                                    .getAuthenticatedAccount();
    return isAuthenticated(account) ? account : new TapisRequestSKPermissionsAuthorizer.NotAuthenticatedAccount();
  }
  
  private boolean isAuthenticated(Account authenticatedAccount) {
    return authenticatedAccount != null;
  }
  
  @Override
  public Consumer<? super Map<String, Object>> consumeConfiguration() throws ConfigurationException {
    return null;
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

