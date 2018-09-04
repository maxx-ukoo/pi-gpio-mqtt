package smarthome.dto;

import com.pi4j.io.gpio.PinState;

public class GPIOStatus {
	
	public enum Direction {
		INPUT, OUTPUT
	}
	
	public String name;
	public Direction direction;
	public PinState value;
	
	public GPIOStatus(String name, Direction direction, PinState value) {
		this.name = name;
		this.direction = direction;
		this.value = value;
	}

}
