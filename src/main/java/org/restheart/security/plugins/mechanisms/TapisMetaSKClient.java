package org.restheart.security.plugins.mechanisms;



import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TapisMetaSKClient {
  private final String skEndpoint;
  private final String serviceToken;
  private String tenantId;
  private String user;
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisMetaSKClient.class);
  
  /**
   * Constructor for the SK client. When a new client connection is needed for getting roles
   * or permissions. This class sets up and returns data from a connection to the SK server
   * @param skEndpoint     Where our connection endpoint is located
   * @param serviceToken   Our service token currently meta as user
   */
  public TapisMetaSKClient(String skEndpoint, String serviceToken) {
    this.skEndpoint = skEndpoint;
    this.serviceToken = serviceToken;
  }
  
  /**
   *
   * @return String representation of an array of roles from the SK
   */
  public String getSKUserRoles(){
    // we need to check to see if user and tenantId have been set. since our contract with SK requires
    // both of these be in place with our service token request.
    
    HttpRequest request = null;
    String rawResponse = null;

    GenericUrl url = new GenericUrl(this.skEndpoint+"/user/roles/"+user);
  
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
    
    try {
      // build the request
      request = requestFactory.buildGetRequest(url);
      
      // populate the headers
      HttpHeaders headers = request.getHeaders();
      headers.setUserAgent("MetaV3 Client");
      headers.set("X-Tapis-Token", serviceToken);
      headers.set("X-Tapis-Tenant", tenantId);
      headers.set("X-Tapis-User", user);
      headers.setContentType("application/json");

      rawResponse = request.execute().parseAsString();
    } catch (IOException e) {
      LOGGER.debug("We caught an exception trying to get roles from the security kernel and need to abort");
      return rawResponse;
    }
    return rawResponse;
  }
  
  public boolean isPermitted(String user, String permSpec){
    boolean isPermitted = false;
    HttpRequest request = null;
    HttpResponse rawResponse = null;

    GenericUrl url = new GenericUrl(this.skEndpoint+"/user/isPermitted");
    LOGGER.debug("The url used : "+url.toString() );
  
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

    String contentString = createPostContent(user,permSpec);
    LOGGER.debug("The permSpec post : "+contentString );
    
    try {
      request = requestFactory.buildPostRequest(url,ByteArrayContent.fromString(null,contentString));
      
      HttpHeaders headers = request.getHeaders();
      headers.setUserAgent("MetaV3 Client");
      headers.set("X-Tapis-Token", serviceToken);
      headers.set("X-Tapis-Tenant", tenantId);
      headers.set("X-Tapis-User", user);
      headers.setContentType("application/json");
      
      String result = request.execute().parseAsString();
      LOGGER.debug("The result returned : "+result);
      isPermitted = extractSKisPermittedResult(result);
      
    } catch (IOException e) {
      LOGGER.debug("We caught an exception trying to get authorization from the security kernel and need to abort");
      e.printStackTrace();
      return false;
    }
   return isPermitted;
  }
  
  private boolean extractSKisPermittedResult(String result){
    boolean isAuthorized = false;
    if(notEmpty(result)){
      JsonObject jsonObject = new Gson().fromJson(result,JsonObject.class);
      if(jsonObject != null){
        JsonPrimitive isPermitted;
        try {
          isPermitted = jsonObject.getAsJsonObject("result").getAsJsonPrimitive("isAuthorized");
          isAuthorized = isPermitted.getAsBoolean();
          return isAuthorized;
        }catch (NullPointerException npe){
          LOGGER.debug("We caught an exception trying to parse the result from SK call to isPermitted");
          return isAuthorized;
        }
      }
    }
    return isAuthorized;
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
  
  private String createPostContent(String user, String permSpec){
    String tmpltString = "{\"user\":\"userVar\",\"permSpec\":\"permSpecVar\"}";
    String target = "userVar";
    String processed = tmpltString.replace(target, user);
    target = "permSpecVar";
    String contentString = processed.replace(target,permSpec);
    return contentString;
  }
  
/*
  public void kongHttpCall(){
    List<String> resultList = new ArrayList<String>();
    Map<String,String> headerMap = new HashMap<String, String>();
    Map<String,Object> queryMap = new HashMap<String, Object>();
  
    headerMap.put("Content-Type", "application/json");
    headerMap.put("accept", "application/json");
    headerMap.put("accept", "application/json");
    headerMap.put("accept", "application/json");
    
    String jsonPayload = "{\"user\":\"sterry1\", \"permSpec\":\"meta:dev:*  \"}";
    
    HttpResponse<JsonNode> response = Unirest.post(this.skEndpoint+"/user/isPermitted")
                                             .headers(headerMap)
                                             .body(jsonPayload)
                                             .asJson();
    
  }
*/
  
  public String getTenantId() {
    return tenantId;
  }
  
  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }
  
  public String getUser() {
    return user;
  }
  
  public void setUser(String user) {
    this.user = user;
  }
  
  public static void main(String[] args) {
    String skendpoint = "https://dev.develop.tapis.io/v3/security";
    String metaServiceToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdjMvdG9rZW5zIiwic3ViIjoidGFjY01ldGFBZG1pbkBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6InRhY2NNZXRhQWRtaW4iLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNTkwNDIxMjMwfQ.LdJ5EnimPGkWSqEc2akOl_GJ24m87tOFwTKWtWo-SYV7jzbrkXjr6sWQVaQMKNvhUxld9dLUlWgZmhZXBgI-uJvCCDU2YvBLLfUBBBkvRIjBCE96RLrw6TwcJBLTC3ID8nx-N0pozXBeP7dpKsYsVQraePVz8q996ST_PxpBmJYrWhFZOaOIhOGVtNDJUvfKBSivQNTTmfQikG1TwHZp0S8RcY1L1mlgBU0IHUWU36I0cFtY28CxTWdOyKktYoMRMOezGwqS8zn4hksfEcyqbZrW2aHHAwiGnm4dYqW5rlj6Nq8b1Bh3P4KoiwvHBuVmwg940EnD3c_-nWvco8Kj5g";
    String tenantId = "dev";
    String user = "streamsTACCAdmin";
    String permSpec = "meta:dev:GET:::StreamsTACCDB";
    TapisMetaSKClient client = new TapisMetaSKClient(skendpoint,metaServiceToken);
    client.setTenantId(tenantId);
    client.setUser(user);
  
    //String s = client.getSKUserRoles();
    boolean s = client.isPermitted(user,permSpec);
    System.out.println(s);
    //String s = client.getSKUserRoles("pysdk");
    // System.out.println();
    //ResultNameArray resultNameArray = client.getSKUserRoles("sterry1");
    //if(resultNameArray != null){
    //  System.out.println("success : "+resultNameArray.toString());
    //}else {
    //  System.out.println("major fail ! ");
    //}
    //client.isPermitted("sterry1","meta:dev:GET:");
  
    //    TapisMetaSKClient client = new TapisMetaSKClient(skendpoint,metaServiceToken);
    //    String body = "{\"user\":\"pysdk\",\"rolename\":\"vdjAdmin\"}";
    //    String response = client.getSKUserRoles("sterry1");
    //    System.out.println(response);
    // get the path
    
    
    
  }
  
}

