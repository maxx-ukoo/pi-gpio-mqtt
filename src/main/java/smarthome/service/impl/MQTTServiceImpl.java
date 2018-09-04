package smarthome.service.impl;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import smarthome.config.MQTTConfig;
import smarthome.service.GPIOService;
import smarthome.service.MQTTService;

@Service
public class MQTTServiceImpl implements MQTTService, IMqttMessageListener {

	private final Logger LOG = LoggerFactory.getLogger(MQTTServiceImpl.class);

	private IMqttClient mqttClient;

	@Autowired
	private MQTTConfig mqttConfig;

	@Autowired
	private GPIOService gpioService;

	@PostConstruct
	public void setup() {
		try {
			mqttClient = new MqttClient(mqttConfig.getBrocker(), mqttConfig.getClientId());
			MqttConnectOptions options = new MqttConnectOptions();
			options.setAutomaticReconnect(true);
			options.setCleanSession(true);
			options.setConnectionTimeout(10);
			mqttClient.connect(options);
			mqttClient.setCallback(new MqttCallback() {
				@Override
				public void connectionLost(Throwable cause) { // Called when the client lost the connection to the
																// broker
					LOG.error("Connection lost", cause);
				}

				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
				}

				@Override
				public void deliveryComplete(IMqttDeliveryToken token) {// Called when a outgoing publish is complete
				}
			});
			String subscribeTopic = getOutTopic() + "#";
			mqttClient.subscribe(subscribeTopic, (topic, msg) -> this.messageArrived(topic, msg));
			LOG.info("Subscribed to: " + subscribeTopic);
		} catch (MqttException e) {
			LOG.error("Can't create MQTT client", e);
		}
	}

	private void sendMessage(String topic, String message) {
		if (!mqttClient.isConnected()) {
			LOG.error("Can't send message, client non connected");
			return;
		}
		MqttMessage msg = new MqttMessage(message.getBytes());
		msg.setQos(1);
		msg.setRetained(true);
		try {
			mqttClient.publish(topic, msg);
		} catch (MqttPersistenceException e) {
			LOG.error("Can't send message: {}, topic: {}", message, topic, e);
		} catch (MqttException e) {
			LOG.error("Can't send message: {}, topic: {}", message, topic, e);
		}
	}

	@Override
	public void sendPinState(String pin, String state) {
		sendMessage(getInTopic() + pin, state);
	}

	@Override
	public void messageArrived(String topic, MqttMessage msg) throws Exception {
		LOG.info("Message arrived: {}, {}", topic, new String(msg.getPayload()));
		try {
			gpioService.setState(topicToPinName(topic), new String(msg.getPayload()));
		} catch (Exception e) {
			LOG.error("Error setting state: {}", e.getMessage());
		}
	}

	private String getInTopic() {
		return "/" + mqttConfig.getClientId() + "/gpio/in/";
	}

	private String getOutTopic() {
		return "/" + mqttConfig.getClientId() + "/gpio/out/";
	}

	private String topicToPinName(String topic) {
		return topic.replace(getOutTopic(), "");
	}

}
