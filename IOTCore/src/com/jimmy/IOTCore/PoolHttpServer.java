package com.jimmy.IOTCore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

//import org.apache.http.*;
//import org.apache.http.protocol.HTTP;



public class PoolHttpServer extends Thread{
	
  private final static String GET = "GET";
  private final static String POST = "POST";
  private final static String PUT = "PUT";
  private final static String DELETE = "DELETE";
  
  private final static String HTTPMETHOD = "method";
  private final static String HTTPPATH = "path";
  private final static String PROTOCOL = "protocol";
  private final static String JSONDATA = "jsonData";
  private final static String CONTENT_TYPE = "Content-type";
  private final static String CONTENT_LENGTH = "Content-length";
  private final static String JSON_TYPE = "application/json";

  private int defaultPort;

  private int numberOfThreads;
  private final Executor fThreadPool = Executors.newFixedThreadPool(10);
  private static ServerSocket conn = null;
  private static Socket tempSoc = null;
  private static ISubscriber ISub;
  
  static public IProxyObject proxyObj = new IProxyObject();
  
  public PoolHttpServer(ISubscriber ISub){
	  // start the httpserver thread
	  this.ISub = ISub;
	  //this.start();
  }
  
  public void run(){
	  this.PoolCreation();
  }
  
  public void HttpServerServiceStart(){
	  this.start();
  }
  
  public void SocketClose(){
		try {
			if((tempSoc != null) && (!tempSoc.isConnected()))
				tempSoc.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("PoolHttpServer - SocketClose");
		}
  }
  
  private void ServerSocketClose(){
	  try {
		  conn.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("PoolHttpServer - Serversocketclose");
	}
  }

  protected void PoolCreation(){	  
	System.out.println("Webserver starting up on port 1111");
    System.out.println("(press ctrl-c to exit)");
	try {
		
		conn = new ServerSocket(1111);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		System.out.println("PoolHttpServer - ServerSocket no created");
		ServerSocketClose();
	}
	
	System.out.println("Waiting for connection");
	while(true)
	{
		try{
			final Socket connection = conn.accept();
			tempSoc = connection;
			if(connection.isConnected()){
				Runnable task = new Runnable(){
					@Override 
					public void run() { 
						HandleRequest(connection); 
					}
				};
				fThreadPool.execute(task);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("PoolHttpServer - connection no accept");
		}
	}
  }
  
  public static Map<String, String> parse(String httpInputData){
	
	Map<String, String> returnString = new HashMap<String,String>();
	String headerLine = httpInputData;
	String protocol = null;
	int count = 0;
	
	// this part of parse can get the http method GET, POST, DELETE or PUT, http path and protocol type
	StringTokenizer tokenizer = new StringTokenizer(headerLine);
	
	returnString.put(HTTPMETHOD, tokenizer.nextToken());
	returnString.put(HTTPPATH, tokenizer.nextToken());
	protocol = tokenizer.nextToken();
	returnString.put(PROTOCOL, protocol.substring(0, protocol.indexOf("/")));
	
	// this part will get the content type for the query and the content length for the query data
	String [] query = headerLine.split(" ");
	for (String word: query){
		if(word.compareToIgnoreCase(CONTENT_TYPE + ":") == 0){
			returnString.put(CONTENT_TYPE, query[count+1].toString());
		}
		else if(word.compareToIgnoreCase(CONTENT_LENGTH + ":") == 0){
			returnString.put(CONTENT_LENGTH, query[count+1].toString());
		}
		count++;
	}

	// this part is get the query of the data we now only focus on json data
	//if(returnString.get(CONTENT_TYPE).compareToIgnoreCase(JSON_TYPE) == 0 ){
		int content_len = Integer.parseInt(returnString.get(CONTENT_LENGTH).toString());
		int totel_len = headerLine.length();
		returnString.put(JSONDATA, headerLine.substring(totel_len-content_len));
	//}
	
	// for debug parser purpose
	/*System.out.println(returnString.get(HTTPMETHOD));
	System.out.println(returnString.get(HTTPPATH));
	System.out.println(returnString.get(PROTOCOL));
	System.out.println(returnString.get(CONTENT_TYPE));
	System.out.println(returnString.get(CONTENT_LENGTH));
	System.out.println(returnString.get(JSONDATA));*/
	

		
	return returnString;
  }
  
  private static void SendResponse(Socket s, Response responeObj){
	  	PrintWriter out = null;
		try {
			out = new PrintWriter(s.getOutputStream(), true);
		  	out.println( "HTTP/1.1 " + responeObj.getStatus());
			out.println("Content-type: text/html");
			out.println("Content-length: " + responeObj.getMsg().length());
			out.println(responeObj.getMsg());
			out.println("");
			out.flush();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  @SuppressWarnings("null")
  private static void HandleRequest(Socket s) {
	  Response responeObj = null;
		try {
			String webServerAddress = s.getInetAddress().toString();
			String remoteClientAddress = s.getRemoteSocketAddress().toString();
			System.out.println("New Connection " + webServerAddress);
			System.out.println("New Connection from remote " + remoteClientAddress);
			BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			
			// this reading method if the entire data is come in without any carridge return and new line.
			// this line will already read all the information and store in the string.
			// the ready() will be true and keep looping the until end of the buffer sending.
			// else the ready() will be false and stop the loop.
			String request = in.readLine(); 
			System.out.println("server " + request);

			while (in.ready()) //this loop is for store all the input stream data into a string
			{
				request = request + " " + in.readLine(); // need to put a space in the line interval in order to perform split in parse process.
			}
			
			System.out.println("request " + request);
			Map<String,String> objTemp = parse(request);
			
			// Response back if the input stream data is correct
			switch(objTemp.get(HTTPMETHOD)){
			case GET:
				proxyObj.setProtocol(objTemp.get(PROTOCOL).toUpperCase());	
				proxyObj.setHttpData(objTemp.get(HTTPPATH));	
				// if the GET method is wish to direct response some information back then just put the callback function here and update into the ProxyObject
				// then use the order and populate back the information back to this response API.
				responeObj = new Response(200, "Done", null);
				SendResponse(s, responeObj);
				break;
			case POST:
			case PUT:
				if(objTemp.get(CONTENT_TYPE).compareToIgnoreCase(JSON_TYPE) == 0){
					proxyObj.setProtocol(objTemp.get(PROTOCOL).toUpperCase());
					proxyObj.setJson(objTemp.get(JSONDATA));
					proxyObj.setHttpData(objTemp.get(HTTPPATH));
					responeObj = new Response(201, "Created", null);
					SendResponse(s, responeObj);
				}
				else{
					responeObj = new Response(400, "ERR_BAD_REQUEST", null);
					SendResponse(s, responeObj);
				}
				break;
			case DELETE:
				break;
			default:
				responeObj = new Response(400, "ERR_BAD_REQUEST", null);
				SendResponse(s, responeObj);
				break;
			}			
			
			System.out.println("After the callback function will the program counter come to here?");
			in.close();
			s.close();
			ISub.objectUpdate(proxyObj);
		} // end try
		catch (Exception ex) {
			ex.printStackTrace();
			responeObj = new Response(400, "ERR_BAD_REQUEST", null);
			SendResponse(s, responeObj);
			System.out.println("PoolHttpServer - HandleRequest");
		}
		finally{
			if( (s != null) || (s.isConnected()))
			{
				try{
					s.close();
					tempSoc = null;
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return;
  } // end run
 
}