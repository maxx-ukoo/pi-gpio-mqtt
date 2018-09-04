package smarthome.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "io")
public class PinConfig {
	
	@Autowired
	private String name; 
	private List<Integer> input;
	private List<Integer> output;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Integer> getInput() {
		return input;
	}
	public void setInput(List<Integer> input) {
		this.input = input;
	}
	public List<Integer> getOutput() {
		return output;
	}
	public void setOutput(List<Integer> output) {
		this.output = output;
	}
	
}
