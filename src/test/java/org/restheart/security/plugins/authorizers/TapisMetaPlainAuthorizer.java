package org.restheart.security.plugins.authorizers;

import io.undertow.server.HttpServerExchange;
import org.restheart.security.ConfigurationException;
import org.restheart.security.plugins.Authorizer;
import org.restheart.security.plugins.ConfigurablePlugin;

import java.util.Map;

public class TapisMetaPlainAuthorizer implements Authorizer, ConfigurablePlugin {
  private final String authorizerName;
  private final String sk_url;
  
  public TapisMetaPlainAuthorizer(String authorizerName, Map<String, Object> args)
      throws ConfigurationException {
    this.authorizerName = authorizerName;
    this.sk_url = ConfigurablePlugin.argValue(args, "sk_url");
  }
  
  @Override
  /**
   * Checks to see if the requested resource is authorized for access by the user
   * @param exchange
   * @return true if request is allowed
   */  public boolean isAllowed(HttpServerExchange httpServerExchange) {
     // delegate to SecurityKernel
     return false;
  }
  
  @Override
  /**
   *
   * @param exchange
   * @return true if not authenticated user won't be allowed
   */  public boolean isAuthenticationRequired(HttpServerExchange httpServerExchange) {
    return false;
  }
}
