package smarthome.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class WindowsCondition implements Condition {

	private final Logger LOG = LoggerFactory.getLogger(WindowsCondition.class);

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		if (context.getEnvironment().getProperty("os.name").contains("Windows")) {
			LOG.info("Windows OS: true");
			return true;
		}
		LOG.info("Windows OS: false");
		return false;
	}
}