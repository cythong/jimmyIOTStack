package com.jimmy.IOTCore;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class MQTTPublish extends Thread{
	
	MqttClient mqttPub;
	MqttMessage msg;
	MqttTopic topic;
	
	public MQTTPublish(MqttClient mqttPublish){
		this.mqttPub = mqttPublish;
		LogTrace.LogInfo(this.getClass(), "mqttPublish start ...");
	}
	
	public void publishData(String topics, String payloads){ //1st parameter MqttTopic topic just pass in string will do
		topic = mqttPub.getTopic(topics);
		msg = new MqttMessage(payloads.getBytes());
		msg.setQos(0);
		msg.setRetained(false);
		this.start();
	}
	
	@SuppressWarnings("deprecation")
	public void run(){
		MqttDeliveryToken token = null;
    	try {
    		// publish message to broker
			token = topic.publish(msg);
	    	// Wait until the message has been delivered to the broker
			// token.waitForCompletion();
			token.isComplete();
			this.stop();
		} catch (Exception e) {
			LogTrace.LogError(this.getClass(), e);
		}
	}			
}