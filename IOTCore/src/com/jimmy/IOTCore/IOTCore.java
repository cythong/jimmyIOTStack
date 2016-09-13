package com.jimmy.IOTCore;

import java.net.ServerSocket;
import java.util.concurrent.TimeUnit;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/*import java.io.*;
import javax.xml.ws.*;
import javax.xml.ws.http.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;*/

/*import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection; //this is using for request/send the http to a server and get response
import java.net.URL;*/

//The Shutdown class is a sample class to illustrate the
//use of the addShutdownHook method
class Shutdown {
	private Thread thread = null;

	public Shutdown() {
		thread = new Thread("App Shutdown thread") {
			public void run() {
				while (true) {
					//System.out.println("[Sample thread] Sample thread speaking...");
					try {
						Thread.currentThread();
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
						if(Thread.interrupted())
							break;
						else
							continue;
					}
				}
				LogTrace.LogInfo(Shutdown.class, "[App Shutdown] Stopped");
			}
		};
		thread.start();
	}

	public void stopThread() {
		thread.interrupt();
	}
}

// addShutdownHook method
class ShutdownThread extends Thread {
	private Shutdown shutdown = null;

	public ShutdownThread(Shutdown shutdown) {
		super();
		this.shutdown = shutdown;
	}

	public void run() {
		LogTrace.LogInfo(ShutdownThread.class, "[Shutdown thread] Shutting down");
		IOTCore.CloseMqtt();
		IOTCore.httpServer.Stop();
		shutdown.stopThread();
		LogTrace.LogInfo(ShutdownThread.class, "[Shutdown thread] Shutdown complete");
	}
}

public class IOTCore {

	static MqttClient kmqttclient;
	static MemoryPersistence persistence;
	static boolean subscriber = false;
	static boolean publisher = false;
	static boolean mqttsubscribe = false;
	static MQTTPublish publish = null;
	public static KhttpServer httpServer = null;
	public static KhttpSServer httpSecureServer = null;
	public static KhttpClient httpRequest = null;
	private static ReadPropertiesConfig readConfig = null;
	
	private final static String brokerAddr = "mqtt.broker_addr";
	private final static String brokerPort = "mqtt.broker_port";
	
	public static int defaultPort = 1234;
	static ServerSocket theServer;
	static int numberOfThreads = 5;

	public IOTCore(String AppName){
		readConfig = new ReadPropertiesConfig("config/config.properties");
		ConfigureMqtt();
	}
	
	private void ConfigureMqtt(){
		try{
			persistence = new MemoryPersistence();
			kmqttclient = new MqttClient("tcp://" + readConfig.getConfig(brokerAddr) + ":" + readConfig.getConfig(brokerPort), "IOTCore", persistence);	
		}catch (MqttException me){
			LogTrace.LogError(this.getClass(), me);
		}
	}
	
	public static void CloseMqtt(){
		try{
			persistence.clear();
			kmqttclient.disconnect();
		}catch (MqttException me){
			LogTrace.LogError(IOTCore.class, me);
		}
	}
	
	public void initlogger(){
		LogTrace.getInstance(readConfig.getConfig("log_path"));
		LogTrace.LogInfo(this.getClass(), "for testing purpose");
		LogTrace.LogInfo(this.getClass(), readConfig.getConfig(brokerAddr) + " @@@@@@@@@@@@@@@@@@@@@@@@");
	}
	
	public void createMQTTPublisher(){
		boolean loop = true;
		
		while (loop)
		{
			try{
				if(!kmqttclient.isConnected()){
					kmqttclient.connect();
				}
				else {
					loop = false;
					LogTrace.LogInfo(this.getClass(), "createMQTTPublisher - Successful connected mqtt broker ...");
					publish = new MQTTPublish(kmqttclient);
				}
				
			}catch (MqttException me){
				LogTrace.LogDebug(this.getClass(), "createMQTTPublisher - Successful connected mqtt broker ...");
				LogTrace.LogError(this.getClass(), me);
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogTrace.LogError(this.getClass(), e);
				}
			}
		}
	}
	
	public void publish(String topic, String payload){
		if(publisher)
		{
			publish.publishData(topic, payload);
		}
	}
	
	public void SubscriberAll(){
		subscriber = true;
	}
	
	protected void shutdown(){
		
	}
	
	public void runUntilSignal(){
		
		Shutdown shutdown = new Shutdown();
		try {
			Runtime.getRuntime().addShutdownHook(new ShutdownThread(shutdown));
			LogTrace.LogInfo(this.getClass(), "[Main thread] Shutdown hook added");
		} catch (Throwable t) {
			// we get here when the program is run with java
			// version 1.2.2 or older
			LogTrace.LogError(this.getClass(), "[Main thread] Could not add Shutdown hook");
		}
		
		try {
			Thread.currentThread();
			Thread.sleep(10000);
		} catch (InterruptedException ie) {
			System.exit(0);
		}
	}
	
	public void registerSubscriber(ISubscriber ISub){
		//register type of subscriber in mqtt or alljoyn
		subscriber = true;
		boolean loop = true;
		
		while (loop)
		{
			try{
				kmqttclient.setCallback(new MQTTSubscribe(ISub));
				httpServer = new KhttpServer(ISub);
				httpRequest = new KhttpClient(ISub);
				if(!kmqttclient.isConnected()){
					kmqttclient.connect();
				}
				else{
					loop = false;
					LogTrace.LogInfo(this.getClass(), "registerSubscriber - Successful connected mqtt broker ...");
				}
			}catch (MqttException me){
				LogTrace.LogInfo(this.getClass(), "registerSubscriber - Fail to connect mqtt broker - RETRYING....");
				LogTrace.LogError(this.getClass(), me);
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					LogTrace.LogError(this.getClass(), e);
				}
			}
		}
	}
	
	public void registerProvider(){
		publisher = true;
	}
	
	public void subscribe(String topics){ //subscribe to a specific topic
		if(subscriber)
		{
			try
			{
				kmqttclient.subscribe(topics);	
			}catch (MqttException MqttEx){
				LogTrace.LogError(this.getClass(), MqttEx);
			}
		}
	}

	public void startHttpServer(){
		//httpServer = new PoolHttpServer();
		//httpServer.PoolCreation(); //this maybe is using socket programming method. Maybe will try another method using HTTP url connection lib
		//httpServer.HttpServerServiceStart();
		httpServer = new KhttpServer(1111);
	}
	
	public void httpClient(){
		String[] headers = {"Content-type", "application/json"};
		String body = "{'device': {'id': 'aqua1111','provision': {'customId': ['aqua1111'],'template': 'default','type': 'gateway','specificType':['CONCENTRATOR'],'name': ['aqua1111'],'description': ['Aquaponic testing'],'admin': {'organization': 'KontronPoC','channel':'default_channel','administrativeState': 'ACTIVE','serviceGroup': 'emptyServiceGroup'}}}}";
		httpRequest = new KhttpClient("GET", "http://localhost", 1111, "/v70/device_id", headers, body);
	}
	
	// ***************************** this feature will develop in future *****************************
	public void startSecureHttpServer(){
		//httpServer = new PoolHttpServer();
		//httpServer.PoolCreation(); //this maybe is using socket programming method. Maybe will try another method using HTTP url connection lib
		//httpServer.HttpServerServiceStart();
		httpSecureServer = new KhttpSServer(1010);
	}
	// ***************************** this feature will develop in future *****************************
}

