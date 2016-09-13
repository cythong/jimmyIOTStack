package com.jimmy.IOTCore;

public class IProxyObject {

	public String getTopic()
	{
		return DataMessageReceive.topic.toString();
	}
	
	public String getMsg()
	{
		return DataMessageReceive.msg.toString();
	}

	public String getProtocol()
	{
		return DataMessageReceive.protocol.toString();
	}
	
	public String getJson()
	{
		return DataMessageReceive.json.toString();
	}

	public String getHttpData()
	{
		return DataMessageReceive.httpdata.toString();
	}
	
	public void setProtocol(String protocol)
	{
		DataMessageReceive.protocol = protocol;
	}
	
	public void setTopic(String topic)
	{
		DataMessageReceive.topic = topic;
	}
	
	public void setMsg(String msg)
	{
		DataMessageReceive.msg = msg;
	}
	
	public void setJson(String json)
	{
		DataMessageReceive.json = json;
	}
	
	public void setHttpData(String httpdata)
	{
		DataMessageReceive.httpdata = httpdata;
	}
}