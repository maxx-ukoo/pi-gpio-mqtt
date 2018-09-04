package smarthome.utils;

import com.pi4j.io.gpio.PinState;

import smarthome.dto.GPIOStatus.Direction;

public class SearchCriteria  {
	public static boolean isSelected(PinState option, PinState value) {
		return option.equals(value);
	}
	
	public static boolean showSelection(Direction direction) {
		return Direction.OUTPUT.equals(direction);
	}
}
