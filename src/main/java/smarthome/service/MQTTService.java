package smarthome.service;

public interface MQTTService {
	void sendPinState(String pin, String state);
}
