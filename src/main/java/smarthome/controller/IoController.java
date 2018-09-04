package smarthome.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.pi4j.io.gpio.PinState;

import smarthome.dto.GPIOStatus;
import smarthome.service.GPIOService;

@Controller
@RequestMapping("/io")
public class IoController {

	@Autowired
	private GPIOService gpioService;

	@GetMapping("/switchcontrol")
	public String switchcontrol(@RequestParam(name = "switch", required = false, defaultValue = "-1") String address,
			@RequestParam(name = "control", required = false, defaultValue = "off") String state, Model model) {
		gpioService.setState(address, state);
		model.addAttribute("msg", address + " => " + state);
		return "switch";
	}

	@GetMapping("/")
	public String list(Model model) {
		List<GPIOStatus> list = gpioService.listState();
		model.addAttribute("list", list);
		model.addAttribute("stateList", PinState.allStates());
		return "list";
	}

}