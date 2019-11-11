package org.restheart.security.plugins.mechanisms;

import com.google.common.collect.Sets;
import io.undertow.security.idm.Account;
import org.restheart.security.plugins.authenticators.BasePrincipal;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

public class TapisAccount implements Account {
  
  final private Principal principal;
  final private Set<String> roles;
  final private String tenant;
  
  public TapisAccount(String name, Set<String> _roles, String _tenant ){
    if (name == null) {
      throw new IllegalArgumentException("argument principal cannot be null");
    }
    
    if (_roles == null || _roles.isEmpty()) {
      _roles = Sets.newHashSet();
    }
    
    if (_tenant == null) {
      throw new IllegalArgumentException("argument tenant cannot be null");
    }
    
    this.principal= new BasePrincipal(name);
    roles=_roles;
    this.tenant=_tenant;
  }
  
  @Override
  public Principal getPrincipal() {
    return principal;
  }
  
  @Override
  public Set<String> getRoles() {
    return Collections.unmodifiableSet(roles);
  }
  
  public String getTenant(){
    return tenant;
  }
}
