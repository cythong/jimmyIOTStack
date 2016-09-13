//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;

import com.kontron.IOTCore.*;

class AgentSubscribe implements ISubscriber{

	public void objectUpdate(IProxyObject proxyObj) throws Exception {
		// TODO Auto-generated method stub
		if(proxyObj.getProtocol().compareToIgnoreCase("MQTT") == 0)
		{
			LogTrace.LogInfo(this.getClass(), "-------------------------------------------------");
			LogTrace.LogInfo(this.getClass(), "| Protocol:" + proxyObj.getProtocol());
			LogTrace.LogInfo(this.getClass(), "| Topic:" + proxyObj.getTopic());
			LogTrace.LogInfo(this.getClass(), "| Message: " + proxyObj.getMsg());
			LogTrace.LogInfo(this.getClass(), "-------------------------------------------------");
		}
		else if(proxyObj.getProtocol().compareToIgnoreCase("HTTP") == 0)
		{
			LogTrace.LogInfo(this.getClass(), "-------------------------------------------------");
			LogTrace.LogInfo(this.getClass(), "| HTTP path " + proxyObj.getHttpData());
			LogTrace.LogInfo(this.getClass(), "| HTTP json message " + proxyObj.getJson());
			LogTrace.LogInfo(this.getClass(), "-------------------------------------------------");
		}
	}

	public void retrieveData(IProxyObject proxyObj) throws Exception {
		// TODO Auto-generated method stub
		
	}
}

public class SampleTestIOTCore {
	
	public static void main(String args[]){
		
		//PropertyConfigurator.configure("log4j.properties");
		//log.info("agent IOTCore start....");
		IOTCore core = new IOTCore(SampleTestIOTCore.class.toString());
		core.initlogger();
		// this IOTCore only for MQTT publish and subscribe
		core.registerProvider();
		core.registerSubscriber(new AgentSubscribe());
		core.createMQTTPublisher();
		LogTrace.LogInfo(SampleTestIOTCore.class, "successful connected ....");
		core.publish("topic/test", "2201");
		LogTrace.LogInfo(SampleTestIOTCore.class, "data published");
		core.subscribe("topic/test");
		LogTrace.LogInfo(SampleTestIOTCore.class, "system subscribe");

		LogTrace.LogInfo(SampleTestIOTCore.class, "start http server");
		core.startHttpServer();
		LogTrace.LogInfo(SampleTestIOTCore.class, "idle http server");
		core.httpClient();
		LogTrace.LogInfo(SampleTestIOTCore.class, "idle http req post to server");
		
		// ***************************** this feature will develop in future *****************************
		/*System.out.println("start https server");
		core.startSecureHttpServer();
		//System.out.println("idle https server");
		//core.httpClient();
		System.out.println("idle https req post to server");*/
		// ***************************** this feature will develop in future *****************************
		
		core.runUntilSignal();
	}
}