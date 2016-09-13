
package com.jimmy.IOTCore;

public interface ISubscriber{
	
	public void objectUpdate(IProxyObject proxyObj) throws Exception;
	
	public void retrieveData(IProxyObject proxyObj) throws Exception;
}
