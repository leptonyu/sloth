package me.icymint.sloth.web.security;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.orm.jpa.SpringNamingStrategy;
import org.springframework.context.ApplicationListener;

public class DataSourcePrefixListener implements
		ApplicationListener<ApplicationEnvironmentPreparedEvent> {
	public static class ModifiedSpringNamingStrategy extends
			SpringNamingStrategy {
		/**
 * 
 */
		private static final long serialVersionUID = 6039872661453530103L;

		@Override
		public String tableName(String tableName) {
			return System.getProperty("spring.datasource.prefix")
					+ addUnderscores(tableName);
		}
	}

	private static final String PREFIX = "spring_sec_";

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		String str = "spring.jpa.hibernate.naming_strategy";
		if (!event.getEnvironment().containsProperty(str)) {
			System.setProperty(str,
					ModifiedSpringNamingStrategy.class.getName());
		}
		System.setProperty("spring.datasource.prefix", event.getEnvironment()
				.getProperty("spring.datasource.prefix", PREFIX));
	}

}
