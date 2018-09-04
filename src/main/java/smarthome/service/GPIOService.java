package smarthome.service;

import java.util.List;

import com.pi4j.io.gpio.PinState;

import smarthome.dto.GPIOStatus;

public interface GPIOService {

	public PinState getState(Integer pin);
	public void setState(Integer address, String state);
	public void setState(String address, String state);
	public void updateState(Integer address, String state);
	public List<GPIOStatus> listState();

}
