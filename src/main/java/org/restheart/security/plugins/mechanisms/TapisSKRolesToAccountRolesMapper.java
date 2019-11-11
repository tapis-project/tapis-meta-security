package org.restheart.security.plugins.mechanisms;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// import org.apache.commons.lang3.StringUtils;

public class TapisSKRolesToAccountRolesMapper {
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisSKRolesToAccountRolesMapper.class);
  
  /**
   * This
   * @param response
   * @return
   */
  public Set<String> getSKRolesList(String response){
    Set<String> roles = new HashSet<>();
    // let's return an empty map if response is null or empty
    // if(StringUtils.isBlank(response)){
    if(response == null || response.isBlank()){
      return roles;
    }
    
    JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
    if(jsonObject == null){
      return roles;
    }
    JsonArray skroles = null;
    try {
      skroles = jsonObject.getAsJsonObject("result").getAsJsonArray("names");
    }catch (NullPointerException npe){
      return roles;
    }

    Iterator<JsonElement> iterator = skroles.iterator();
    
    while(iterator.hasNext()){
      String role = iterator.next().getAsString();
      roles.add(role);
    }
    return roles;
  }
  
  public static void main(String[] args) {
    TapisSKRolesToAccountRolesMapper mapper = new TapisSKRolesToAccountRolesMapper();
    String r0 = "{\"result\":{\"names\":[\"vdjAdmin\",\"vdjUser\"]},\"status\":\"success\",\"message\":\"TAPIS_FOUND Roles found: 2 roles\",\"version\":\"0.0.1\"}";
    String r1 = "{\"result\":{\"names\":[]},\"status\":\"success\",\"message\":\"TAPIS_FOUND Roles found: 2 roles\",\"version\":\"0.0.1\"}";
    String r2 = "{}";
    String r3 = "not json";
    mapper.getSKRolesList(r0);
    
    
  }
}
