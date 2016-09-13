package com.jimmy.IOTCore;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class KhttpClient extends Thread {
	Request requestObj = null;
	Response responeObj = null;
	
	static ISubscriber ISub;
	static public IProxyObject proxyObj = new IProxyObject();

	public KhttpClient(ISubscriber ISub){
		KhttpClient.ISub = ISub;
		LogTrace.LogInfo(this.getClass(), "KhttpClient start ...");
	}
	
	public KhttpClient(String method, String baseUrl, int port, String path, String[] headers, String body){
		requestObj = new Request(method, baseUrl, port, path, headers, body);
		this.start();
	}
	
	public void run(){
		this.HttpClientRequest(requestObj);
	}
	
	private void httpGetResponse(int responseCode, HttpURLConnection conn)
	{
        String inputLine;
        StringBuffer response = new StringBuffer();

        try {
        	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
			    response.append(inputLine);
			}
			responeObj = new Response(responseCode, conn.getURL().toString(), response.toString());
	        in.close();
	        
	        proxyObj.setHttpData(responeObj.getMsg().toString());
			if (responeObj.getBody() == null) {

				proxyObj.setJson("empty"); // cannot null has to put empty string.
			} else {
				proxyObj.setJson(responeObj.getBodyAsString());
	        }
			// this call back is required for GET request
			//ISub.objectUpdate(proxyObj);
		} catch (Exception e) {
			LogTrace.LogError(this.getClass(), e);
		}
        
        LogTrace.LogInfo(this.getClass(), responeObj.getMsg() + responeObj.getBody() + responeObj.getStatus());
	}
	
	public void HttpClientRequest(Request reqObj){
		
		String body = null;
		BufferedOutputStream out = null;
		int responseCode = 400;
		
		try {
			URL obj = new URL(reqObj.getBaseUrl()+":"+reqObj.getPort()+reqObj.getPath());
			HttpURLConnection conn = (HttpURLConnection)obj.openConnection();
			conn.setRequestMethod(reqObj.getMethod());
			String[] headers = reqObj.getHeaders();
			if(headers != null)
				for(int i=0; i<headers.length; i+=2)
					conn.setRequestProperty(headers[i], headers[i+1]);
			
			if(conn.getRequestMethod().compareToIgnoreCase("GET") == 0){
				responseCode = conn.getResponseCode();
				httpGetResponse(responseCode, conn);
			}
			else if (conn.getRequestMethod().compareToIgnoreCase("POST") == 0){
				body = reqObj.getBody();
				
				boolean hasBody = body!=null && body.length()>0;
				
				if(hasBody){
					LogTrace.LogInfo(this.getClass(), "post send body " + body);
					byte[] bodyBytes = body.getBytes();
					conn.setFixedLengthStreamingMode(bodyBytes.length);
					conn.setDoOutput(true);
					out = new BufferedOutputStream(conn.getOutputStream());
					out.write(bodyBytes);
					out.flush();
				}
				responseCode = conn.getResponseCode();
				httpGetResponse(responseCode, conn);
			}
			else if (conn.getRequestMethod().compareToIgnoreCase("PUT") == 0){
				
			}
			else if (conn.getRequestMethod().compareToIgnoreCase("DELETE") == 0){
				
			}
			else{
				httpGetResponse(HttpURLConnection.HTTP_BAD_REQUEST, conn); // bad Request
			}
			
			// ****************************************************************************************************************************************************
			/*Socket soc = new Socket(reqObj.getBaseUrl(), reqObj.getPort());
			httpHead = httpHead + reqObj.getMethod() + " HTTP/1.1 ";
			httpHead = httpHead + reqObj.getPath() + " ";
			
			String[] headers = reqObj.getHeaders();
			if(headers != null)
				for(int i=0; i<headers.length; i+=2)
					httpHead = httpHead + headers[i] + headers[i+1] + " ";
			
			out = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream(), "UTF-8"));
			
			boolean hasBody = reqObj.getBody()!=null && reqObj.getBody().length()>0;

			
			// this is sending the request package out
			if(hasBody) {
				//System.out.println("start the httpclient thread");
				//byte[] bodyBytes = reqObj.getBody().getBytes();

				//System.out.println("client send " + reqObj.getBody().toString());
				out.write(httpHead);
				out.write("content-length: " + reqObj.getBody().length() + " ");
				out.write(reqObj.getBody());
				out.flush();
				out.close();
			}
			
			// this getting response from server
			try {
				in = new BufferedInputStream(soc.getInputStream());
			} catch(IOException e) {
				//in = new BufferedInputStream(connection.getErrorStream());
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buf = new byte[BUF_LENGTH];
			try {
				for(int n; (n = in.read(buf, 0, BUF_LENGTH)) != -1;)
					baos.write(buf, 0, n);
			} catch(IOException e) {}
			baos.flush();
			body = new String(baos.toByteArray());
			
			Response responeObj = new Response(200, "Done", body);
			System.out.println("Response " + responeObj.getBody());*/
			// ****************************************************************************************************************************************************
			
			//add a respone code here to send back the input stream information
			
			
			// ****************************************************************************************************************************************************
			// this is using socket method.
			/*Socket soc = new Socket("localhost", 1111);
			String data = "{'device': {'id': 'aqua1111','provision': {'customId': ['aqua1111'],'template': 'default','type': 'gateway','specificType':['CONCENTRATOR'],'name': ['aqua1111'],'description': ['Aquaponic testing'],'admin': {'organization': 'KontronPoC','channel':'default_channel','administrativeState': 'ACTIVE','serviceGroup': 'emptyServiceGroup'}}}}";
		    			
			OutputStream output = soc.getOutputStream();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
			writer.write("POST " + "/v70/device_id" + " http/1.1\r\n");
			writer.write(" Content-Length: " + data.length());
			writer.write(" Content-Type: application/json \r\n");
			writer.write("\r\n");
			writer.write(data);
			writer.write("\r\n");
			
			// for debug purpose
			// System.out.println(data + " data length is " + data.length());
			writer.flush();
			writer.close();
			output.close();
			soc.close();*/
			// ****************************************************************************************************************************************************
			
		} catch (MalformedURLException e) {
			LogTrace.LogError(this.getClass(), e);
		} catch (IOException e) {
			LogTrace.LogError(this.getClass(), e);
		}
	}
	
}