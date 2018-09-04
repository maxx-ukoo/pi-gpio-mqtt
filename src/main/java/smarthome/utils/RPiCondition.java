package smarthome.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;


public class RPiCondition implements Condition {

	private final Logger LOG = LoggerFactory.getLogger(RPiCondition.class);
	
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		final File file = new File("/etc", "os-release");
		try (FileInputStream fis = new FileInputStream(file);
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis))) {
			String string;
			while ((string = bufferedReader.readLine()) != null) {
				if (string.toLowerCase().contains("raspbian")) {
					if (string.toLowerCase().contains("name")) {
						LOG.info("RPi OS: true");
						return true;
					}
				}
			}
		} catch (final Exception e) {
			
		}
		LOG.info("RPi OS: false");
		return false;
	}
}