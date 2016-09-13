package com.jimmy.IOTCore;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.ssl.KeyManagerFactory;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.Executors;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


public class KhttpSServer{
	private static HttpsServer server;
	static ISubscriber ISub;
	private static String protocol = "TLS";
	
	public KhttpSServer(int port){
		this.Start(port);
	}
	
	public KhttpSServer(ISubscriber ISub){
		KhttpServer.ISub = ISub;
	}

	private static SSLContext createSSLContext() throws Exception {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream is = new FileInputStream("server.crt");
        InputStream caInput = new BufferedInputStream(is);
        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType); 
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Create a TrustManager that trusts the CAs in our KeyStore
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Create an SSLContext that uses our TrustManager
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);
        
        char[] keystorePassword = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        ks.load(new FileInputStream("filename.jks"), keystorePassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, keystorePassword);
        context.init((KeyManager[]) kmf.getKeyManagers(), null, null);

        return context;
    }

	public void Start(int port) {
		try {
			// load certificate
			/*String keystoreFilename = getPath() + "mycert.keystore";
			char[] storepass = "mypassword".toCharArray();
			char[] keypass = "mypassword".toCharArray();
			String alias = "alias";
			FileInputStream fIn = new FileInputStream(keystoreFilename);
			KeyStore keystore = KeyStore.getInstance("JKS");
			keystore.load(fIn, storepass);
			// display certificate
//			Certificate cert = keystore.getCertificate(alias);
//			System.out.println(cert);

			// setup the key manager factory
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(keystore, keypass);

			// setup the trust manager factory
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(keystore);*/

			// create https server
			server = HttpsServer.create(new InetSocketAddress(port), 0);
			// create ssl context
			//SSLContext sslContext = SSLContext.getInstance(protocol);
			// setup the HTTPS context and parameters
			//sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			//SSLContext sslContext = createSSLContext();
			
			server.setHttpsConfigurator(new HttpsConfigurator(createSSLContext()));

			System.out.println("server started at " + port);
			server.createContext("/test", new Handlers.RootHandler(ISub));
			
			server.setExecutor(Executors.newCachedThreadPool());
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getPath() {
		return this.getClass().getClassLoader().getResource("").getPath() + "com/happylife/demo/";
	}
	
	public void Stop() {
		server.stop(0);
		System.out.println("server stopped");
	}
}
