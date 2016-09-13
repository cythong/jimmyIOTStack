package com.jimmy.IOTCore;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.HttpServer;

public class KhttpServer{
	private static HttpServer server;
	static ISubscriber ISub;
	
	public KhttpServer(int port){
		this.Start(port);
	}
	
	public KhttpServer(ISubscriber ISub){
		KhttpServer.ISub = ISub;
	}

	public void Start(int port) {
		try {
			server = HttpServer.create(new InetSocketAddress(port), 1);
			
			LogTrace.LogInfo(this.getClass(), "server started at " + port);
			
			server.createContext("/", new Handlers.RootHandler(ISub));
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
				
		} catch (Exception e) {
			e.printStackTrace();
			LogTrace.LogError(this.getClass(), e);
		}
	}

	public void Stop() {
		server.stop(0);
		LogTrace.LogInfo(this.getClass(), "server stopped");
	}
}