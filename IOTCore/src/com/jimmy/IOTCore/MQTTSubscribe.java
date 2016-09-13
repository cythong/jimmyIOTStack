package com.jimmy.IOTCore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTSubscribe implements MqttCallback{
	MqttClient mqttSub;
	private static ISubscriber ISub;
	static public IProxyObject proxyObj = new IProxyObject();
	
	public MQTTSubscribe(ISubscriber ISub){
		MQTTSubscribe.ISub = ISub;
		LogTrace.LogInfo(this.getClass(), "MQTTSubscribe start ...");
	}
	
	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage message) throws Exception {
		// TODO Auto-generated method stub
		// set all the information into proxy object in order to perform a callback to user application
		proxyObj.setTopic(topic);
		proxyObj.setMsg(new String(message.getPayload()));
		proxyObj.setProtocol("MQTT");
		ISub.objectUpdate(proxyObj);
		LogTrace.LogInfo(this.getClass(), "MQTTSubscribe - messageArrived");
	}
}