package smarthome.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import smarthome.config.PinConfig;
import smarthome.dto.GPIOStatus;
import smarthome.dto.GPIOStatus.Direction;
import smarthome.service.GPIOService;
import smarthome.service.MQTTService;
import smarthome.utils.RPiCondition;

@Service
@Conditional(value = RPiCondition.class)
public class GPIOServiceImpl implements GPIOService {
	
	private final Logger LOG = LoggerFactory.getLogger(GPIOServiceImpl.class);

	private final GpioController gpio = GpioFactory.getInstance();
	private final Map<String, GpioPinDigitalInput> gpioDigitalInputList = new HashMap<>();
	private final Map<String, GpioPinDigitalOutput> gpioDigitalOutputList = new HashMap<>();

	@Autowired
	private PinConfig pinConfig;

	@Autowired
	private MQTTService mqttService;

	@PostConstruct
	public void setup() {
		pinConfig.getOutput().stream().forEach(pin -> {
			Pin raspiPin = RaspiPin.getPinByAddress(pin);
			gpioDigitalOutputList.put(raspiPin.getName(), setPinAsDigitalOutput(raspiPin));
		});

		pinConfig.getInput().stream().forEach(pin -> {
			Pin raspiPin = RaspiPin.getPinByAddress(pin);
			GpioPinDigitalInput input = setPinAsDigitalInput(raspiPin);
			gpioDigitalInputList.put(raspiPin.getName(), input);
			input.addListener(new GpioPinListenerDigital() {
				@Override
				public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
					mqttService.sendPinState(event.getPin().getPin().getName(), event.getState().toString());
					LOG.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				}
			});
		});
	}

	@Override
	public void setState(Integer address, String state) {
		setState(RaspiPin.getPinByAddress(address), state);
	}
	
	@Override
	public void setState(String address, String state) {
		setState(RaspiPin.getPinByName(address), state);
	}
	
	private void setState(Pin pin, String state) {
		GpioPinDigitalOutput gpioPin = gpioDigitalOutputList.get(pin.getName());
		if ("HIGH".equalsIgnoreCase(state) || "1".equals(state) || "ON".equals(state)) {
			gpioPin.high();
		} else {
			gpioPin.low();
		}
	}

	@Override
	public void updateState(Integer address, String state) {
		mqttService.sendPinState(address.toString(), state);
		setState(address, state);
	}

	@Override
	public PinState getState(Integer pin) {
		GpioPinDigitalInput gpioPin = gpioDigitalInputList.get(pin);
		return gpioPin.getState();
	}

	private GpioPinDigitalOutput setPinAsDigitalOutput(Pin raspiPin) {
		return gpio.provisionDigitalOutputPin(raspiPin, raspiPin.getName(), PinState.LOW);
	}

	private GpioPinDigitalInput setPinAsDigitalInput(Pin raspiPin) {
		return gpio.provisionDigitalInputPin(raspiPin, raspiPin.getName(), PinPullResistance.PULL_UP);
	}

	@Override
	public List<GPIOStatus> listState() {
		List<GPIOStatus> list = new ArrayList<GPIOStatus>();
		gpioDigitalOutputList.entrySet().stream().forEach(entry -> {
			list.add(new GPIOStatus(entry.getKey(), Direction.OUTPUT, entry.getValue().getState()));
		});
		gpioDigitalInputList.entrySet().stream().forEach(entry -> {
			list.add(new GPIOStatus(entry.getKey(), Direction.INPUT, entry.getValue().getState()));
		});
		return list;
	}

}
