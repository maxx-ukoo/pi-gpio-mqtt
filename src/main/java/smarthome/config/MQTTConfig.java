package smarthome.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mqtt")
public class MQTTConfig {
	
	@Autowired
	private String clientId;
	
	private String brocker;
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getBrocker() {
		return brocker;
	}
	public void setBrocker(String brocker) {
		this.brocker = brocker;
	}

}
