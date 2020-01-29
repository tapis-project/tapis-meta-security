package org.restheart.security.plugins.mechanisms;



import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;

import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.GenericJson;
import com.google.api.client.json.Json;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

public class TapisMetaSKClient {
  private final String skEndpoint;
  private final String serviceToken;
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisMetaSKClient.class);
  
  public TapisMetaSKClient(String skEndpoint, String serviceToken) {
    this.skEndpoint = skEndpoint;
    this.serviceToken = serviceToken;
  }
  
  public String getSKUserRoles(String user){
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
      headers.setContentType("application/json");

      rawResponse = request.execute().parseAsString();
    } catch (IOException e) {
      LOGGER.debug("We caught an exception trying to get roles from the security kernel and need to abort");
      return rawResponse;
    }
    return rawResponse;
  }
  
  public boolean isPermitted(String user, String permSpec){
  
    HttpRequest request = null;
    HttpResponse rawResponse = null;

    GenericUrl url = new GenericUrl(this.skEndpoint+"/user/isPermitted");
    LOGGER.debug("The url used : "+url.toString() );
  
    // post body
    Map<String, Object> data = new LinkedHashMap<>();
    data.put("user", "streamsTACCAdmin");
    data.put("permSpec", "meta:dev:");
  
    // need a concrete implementation for json factory, in this case jackson factory
    JsonFactory jsonFactory = new JacksonFactory();
    HttpContent content = new JsonHttpContent(jsonFactory,data);
    
    HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();

    try {
      request = requestFactory.buildPostRequest(url,content);
      
      HttpHeaders headers = request.getHeaders();
      headers.setUserAgent("MetaV3 Client");
      headers.set("X-Tapis-Token", serviceToken);
      headers.setContentType("application/json");
      
      String result = request.execute().parseAsString();
      System.out.println();
    } catch (IOException e) {
      LOGGER.debug("We caught an exception trying to get authorization from the security kernel and need to abort");
      e.printStackTrace();
      return false;
    }
   return true;
  }
  
  public void kongHttpCall(){
    List<String> resultList = new ArrayList<String>();
    Map<String,String> headerMap = new HashMap<String, String>();
    Map<String,Object> queryMap = new HashMap<String, Object>();
  
    headerMap.put("Content-Type", "application/json");
    headerMap.put("accept", "application/json");
    String jsonPayload = "{\"user\":\"sterry1\", \"permSpec\":\"meta:dev:*  \"}";
    
    HttpResponse<JsonNode> response = Unirest.post(this.skEndpoint+"/user/isPermitted")
                                             .headers(headerMap)
                                             .body(jsonPayload)
                                             .asJson();
    
  }
  

  public static void main(String[] args) {
    String skendpoint = "https://dev.develop.tapis.io/v3/security";
    String metaServiceToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoibWV0YUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6Im1ldGEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNjA0NjgwNjY3fQ.XXd7ZZgZJfP6mcwzYF5PdcHBkTB2dXTJTOljtT9xTIkkz-QV5v-_eHFzZWshJH4Hns4b6nGF-1eBoy_VugmHoMFWPFcC9RijBlM1brReSEr47PXgERPWazPgJecGzwfxSELE4xECIlVKd1GlHiQc7t4B-xhZ1FzbcT1eJmzI5D5Wp54A3Eytq7HU2o6RWN_8ARxgcWece0bKCPWpYF3tVhuH5y_-6lomp6nrcx8JgqdwpmGLtqFqf5zHmVMDt61GlneMou3C4OwVrCvhF0ZNXE4eQDmWgi0-5k9HJDGbrEz5BLT_RuTe6GZ-42DsICrjQHjYQ_m_ALgreDmT-ARgeQ";
    TapisMetaSKClient client = new TapisMetaSKClient(skendpoint,metaServiceToken);
  

  
    client.kongHttpCall();
  
  
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

