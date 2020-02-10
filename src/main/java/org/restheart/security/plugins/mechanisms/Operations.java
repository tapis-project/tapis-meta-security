package org.restheart.security.plugins.mechanisms;

public enum Operations {
  GET("GET"), 
  POST("POST"), 
  PUT("PUT"), 
  DELETE("DELETE");
  
  Operations(String op) { }
  
}
