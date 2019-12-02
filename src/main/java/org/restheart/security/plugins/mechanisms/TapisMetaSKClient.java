package org.restheart.security.plugins.mechanisms;


import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TapisMetaSKClient {
  private final String skEndpoint;
  private final String serviceToken;
  private static final Logger LOGGER = LoggerFactory.getLogger(TapisMetaSKClient.class);
  
  public TapisMetaSKClient(String skEndpoint, String serviceToken) {
    this.skEndpoint = skEndpoint;
    this.serviceToken = serviceToken;
  }
  
  public String getSKUserRoles(String user){
    HttpRequestFactory requestFactory
        = new NetHttpTransport().createRequestFactory();
    HttpRequest request = null;
    String rawResponse = null;
    String url = this.skEndpoint+"/user/roles/"+user;
    try {
      request = requestFactory.buildGetRequest(new GenericUrl(url));
      HttpHeaders headers = request.getHeaders();
      headers.setUserAgent("MetaV3 Client");
      headers.set("X-Tapis-Token", serviceToken);
      headers.setContentType("application/json");
      rawResponse = request.execute().parseAsString();
    } catch (IOException e) {
      LOGGER.debug("We caught an exception trying to get roles from the security kernel and need to abort");
      e.printStackTrace();
      return rawResponse;
    }
    return rawResponse;
  }

  public static void main(String[] args) {
    String skendpoint = "https://dev.develop.tapis.io/security/v3/user";
    String metaServiceToken = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL2Rldi5hcGkudGFwaXMuaW8vdG9rZW5zL3YzIiwic3ViIjoibWV0YUBkZXYiLCJ0YXBpcy90ZW5hbnRfaWQiOiJkZXYiLCJ0YXBpcy90b2tlbl90eXBlIjoiYWNjZXNzIiwidGFwaXMvZGVsZWdhdGlvbiI6ZmFsc2UsInRhcGlzL2RlbGVnYXRpb25fc3ViIjpudWxsLCJ0YXBpcy91c2VybmFtZSI6Im1ldGEiLCJ0YXBpcy9hY2NvdW50X3R5cGUiOiJzZXJ2aWNlIiwiZXhwIjoxNjA0NjgwNjY3fQ.XXd7ZZgZJfP6mcwzYF5PdcHBkTB2dXTJTOljtT9xTIkkz-QV5v-_eHFzZWshJH4Hns4b6nGF-1eBoy_VugmHoMFWPFcC9RijBlM1brReSEr47PXgERPWazPgJecGzwfxSELE4xECIlVKd1GlHiQc7t4B-xhZ1FzbcT1eJmzI5D5Wp54A3Eytq7HU2o6RWN_8ARxgcWece0bKCPWpYF3tVhuH5y_-6lomp6nrcx8JgqdwpmGLtqFqf5zHmVMDt61GlneMou3C4OwVrCvhF0ZNXE4eQDmWgi0-5k9HJDGbrEz5BLT_RuTe6GZ-42DsICrjQHjYQ_m_ALgreDmT-ARgeQ";
    TapisMetaSKClient client = new TapisMetaSKClient(skendpoint,metaServiceToken);
    String s = client.getSKUserRoles("sterry1");
    System.out.println();
    //ResultNameArray resultNameArray = client.getSKUserRoles("sterry1");
    //if(resultNameArray != null){
    //  System.out.println("success : "+resultNameArray.toString());
    //}else {
    //  System.out.println("major fail ! ");
    //}
    
  
    //    TapisMetaSKClient client = new TapisMetaSKClient(skendpoint,metaServiceToken);
    //    String body = "{\"user\":\"jstubbs\",\"rolename\":\"vdjUser\"}";
    //    String response = client.getSKUserRoles("sterry1");
    //    System.out.println(response);
    // get the path
  }
  
}

