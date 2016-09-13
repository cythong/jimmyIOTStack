package com.jimmy.IOTCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class Handlers {
	  
	private final static String GET    = "GET";
	private final static String POST   = "POST";
	private final static String PUT    = "PUT";
	private final static String PATCH  = "PATCH";
	private final static String DELETE = "DELETE";

	static public Response responeData;
	
	public static class RootHandler implements HttpHandler {
		
		static public ISubscriber ISub = null;
		static public IProxyObject proxyObj = new IProxyObject();

		public RootHandler(ISubscriber ISub){
			this.ISub = ISub;
		}
		
		private void httpResponse(HttpExchange he, String query, int status){
			try {
			
				if(query == null || query.length() == 0)
				{
					query = "Ok Created";
				}
				responeData = new Response(status, he.getRequestURI().toString(), query);
				he.sendResponseHeaders(status, query.length());
				OutputStream os = he.getResponseBody();
				os.write(query.getBytes());
				os.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		public void handle(HttpExchange he) throws IOException {
			String protocol = he.getProtocol().toString();
			String query = null;
			
			LogTrace.LogDebug(this.getClass(), protocol.substring(0, protocol.indexOf("/")));
			proxyObj.setProtocol(protocol.substring(0, protocol.indexOf("/")));
			LogTrace.LogDebug(this.getClass(), he.getRequestMethod());
			try {
				switch(he.getRequestMethod().toString()){
					case GET:
						URI requestedUri = he.getRequestURI();
						//query = requestedUri.getRawQuery();
						LogTrace.LogInfo(this.getClass(), "GET request uri " + requestedUri.getHost() + requestedUri.getPath());
						//System.out.println("get handler " + query);
						proxyObj.setHttpData(requestedUri.getPath());
						// add a callback here in order for user to control once having this input come in what should be perform.
						//retrieveData()
						httpResponse(he, proxyObj.getHttpData(), 200);
						break;
					case POST:
					case PUT:
						LogTrace.LogInfo(this.getClass(), "POST request uri " + he.getRequestURI().getHost() + he.getRequestURI().getPath());
						Headers headers = he.getRequestHeaders();
						if(headers.getFirst("Content-type").compareToIgnoreCase("application/json") == 0){
							InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8"); //getRequestBody only will read the body of the http.
							BufferedReader br = new BufferedReader(isr);
							query =  br.readLine();
							while(br.ready())
							{
								query = query + br.readLine();
							}
							
							if(query == null)
							{
								query = "empty"; // cannot null has to put empty string.
							}
							proxyObj.setHttpData(he.getRequestURI().toString());
							proxyObj.setJson(query);
							//call back function is got any 
							ISub.objectUpdate(proxyObj);
							
							// this response will be retrieve information from the callback function and manipulate it and send back
							httpResponse(he, proxyObj.getHttpData(), 200);
						}
						else
						{
							httpResponse(he, "400 Bad Request", 400);
						}
						break;
					case DELETE:
						break;
					default:
						break;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
  }
}
