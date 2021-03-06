package com.jimmy.IOTCore;

import org.json.JSONArray;
import org.json.JSONObject;


public class Request
{
	public final static String GET    = "GET";
	public final static String POST   = "POST";
	public final static String PUT    = "PUT";
	public final static String PATCH  = "PATCH";
	public final static String DELETE = "DELETE";

	public final static String K_METHOD  = "method";
	public final static String K_HEADER  = "headers";
	public final static String K_BODY    = "body";
	public final static String K_BASEURL = "baseurl";
	public final static String K_PATH    = "path";
	
	/**
	 * Create a new instance of this class.
	 * @param method the HTTP method, one of {@link #GET}, {@link #POST}, {@link #PUT}, {@link #PATCH} or {@link #DELETE}
	 * @param baseUrl the base URL (without the path part) of this request
	 * @param path the path part of the request URL
	 * @param headers array of headers to send during the request; the even elements are the header names, while the odd ones are the
	 *                corresponding values
	 * @param body the body of the HTTP request or null for empty body
	 */
	public Request(String method, String baseUrl, int port, String path, String[] headers, String body) {
		this.method  = method;
		this.baseUrl = baseUrl;
		this.port	 = port;
		this.path    = path;
		this.headers = headers;
		this.body    = body;
	}

	/**
	 * Create a new instance of this class from a JSON string (previously produced by calling {@link #toString()}).
	 * @param json the JSON string to parse for creating this request
	 */
	public Request(String json) {
		fromString(json);
	}

	/**
	 * Create a new instance of this class from a JSON object (previously produced by calling {@link #toJSON()}).
	 * @param jso the JSON object to use for creating this request
	 */
	public Request(JSONObject jso) {
		fromJSON(jso);
	}

	/**
	 * @return the asynchronous execution identifier of this request or 0 if it was not been enqueued
	 */
	public int getId() {
		return id;
	}

	/**
	 * HTTP method getter.
	 * @return the HTTP method of this request
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * HTTP base URL getter.
	 * @return the HTTP base URL of this request
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
	
	/**
	 * HTTP port getter.
	 * @return the HTTP port of this request
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * HTTP URL path getter.
	 * @return the HTTP URL path of this request
	 */
	public String getPath() {
		return path;
	}

	/**
	 * HTTP full URL getter.
	 * @return the HTTP full URL of this request
	 */
	public String getUrl() {
		return baseUrl + path;
	}

	/**
	 * HTTP headers getter.
	 * @return the array of all headers of this request; the even elements are the header names, while the odd ones are the
	 *         corresponding values
	 */
	public String[] getHeaders() {
		return headers;
	}

	/**
	 * HTTP body getter.
	 * @return the HTTP body of this request of null for empty body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @return true if the URL of this request is using HTTPS, false if it is a plain HTTP or if this request has not any URL
	 */
	public boolean hasSSL() { return baseUrl!=null && baseUrl.startsWith("https"); }

	/**
	 * Make a JSON object representation of this request.
	 * Such JSON instance can be used to create a new request instance by calling {@link #Request(JSONObject)}.
	 * @return the JSON object representation of this request
	 * @throws Exception 
	 */
	public JSONObject toJSON() throws Exception {
		try {
			JSONObject jso = new JSONObject();
			jso.put(K_METHOD , method );
			jso.put(K_BASEURL, baseUrl);
			jso.put(K_PATH   , path   );
			if(headers != null) {
				JSONArray arr = new JSONArray();
				for(int i=0, n=headers.length; i<n; ++i)
					arr.put(headers[i]);
				jso.put(K_HEADER , arr);
			}
			if(body != null) jso.put(K_BODY, body);
			return jso;
		} catch(Exception e) {
			//throw new RestException(e);
			LogTrace.LogError(this.getClass(), e);
		}
		return null;
	}

	/**
	 * Make a JSON string representation of this request.
	 * Such JSON instance can be used to create a new request instance by calling {@link #Request(String)}.
	 * @return the JSON string representation of this request
	 */
	@Override
	public String toString() {
		try {
			return toJSON().toString();
		} catch (Exception e) {
			LogTrace.LogError(this.getClass(), e);
		}
		return baseUrl;
	}

	protected void fromJSON(JSONObject jso) {
		try {
			String method  = jso.getString(K_METHOD);
			String baseUrl = jso.getString(K_BASEURL);
			String path    = jso.getString(K_PATH);
			String body    = jso.has(K_BODY) ? jso.getString(K_BODY) : null;
			String[] headers = null;
			if(jso.has(K_HEADER)) {
				JSONArray arr = jso.getJSONArray(K_HEADER);
				int n = arr.length();
				if(n > 0) {
					headers = new String[n];
					for(int i=0; i<n; ++i)
						headers[i] = arr.getString(i);
				}
			}
			this.method  = method;
			this.baseUrl = baseUrl;
			this.path    = path;
			this.headers = headers;
			this.body    = body;
		} catch(Exception e) {
			//throw new RestException(e);
			LogTrace.LogError(this.getClass(), e);
		}
	}

	protected void fromJSON(String json) {
		try {
			fromJSON(new JSONObject(json));
		} catch(Exception e) {
			//throw new RestException(e);
			//throw new Exception(e);
			LogTrace.LogError(this.getClass(), e);
		}
	}

	protected void fromString(String json) {
		fromJSON(json);
	}

	int              id = 0;
	private String   method;
	private String   baseUrl;
	private int	     port;
	private String   path;
	private String[] headers;
	private String   body;
}