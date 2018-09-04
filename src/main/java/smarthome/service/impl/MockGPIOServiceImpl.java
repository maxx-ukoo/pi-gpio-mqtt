package smarthome.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import com.pi4j.io.gpio.PinState;

import smarthome.config.PinConfig;
import smarthome.dto.GPIOStatus;
import smarthome.dto.GPIOStatus.Direction;
import smarthome.service.GPIOService;
import smarthome.service.MQTTService;
import smarthome.utils.WindowsCondition;

@Service
@Conditional(value = WindowsCondition.class)
public class MockGPIOServiceImpl implements GPIOService {

	@Autowired
	private PinConfig pinConfig;

	@Autowired
	private MQTTService mqttService;

	@Override
	public PinState getState(Integer pin) {
		return PinState.HIGH;
	}

	@Override
	public void setState(Integer address, String state) {
		mqttService.sendPinState(address.toString(), state);
		throw new IllegalStateException("Not implemented");
	}
	
	@Override
	public void setState(String address, String state) {
		mqttService.sendPinState(address, state);
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public List<GPIOStatus> listState() {
		List<GPIOStatus> list = new ArrayList<GPIOStatus>();
		list.add(new GPIOStatus("GPIO 0", Direction.OUTPUT, PinState.HIGH));
		list.add(new GPIOStatus("GPIO 1", Direction.INPUT, PinState.LOW));
		list.add(new GPIOStatus("GPIO 2", Direction.OUTPUT, PinState.LOW));
		list.add(new GPIOStatus("GPIO 3", Direction.INPUT, PinState.HIGH));
		list.add(new GPIOStatus("GPIO 4", Direction.INPUT, PinState.HIGH));
		
		return list;
	}

	@Override
	public void updateState(Integer address, String state) {
		mqttService.sendPinState(address.toString(), state);
	}

}
