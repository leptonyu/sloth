package me.icymint.sloth.context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 以Properties作为配置表文件的上下文。
 * 
 * @author Daniel
 *
 */
public abstract class AbstractPropertiesContext extends
		AbstractContext<Properties> {
	@Override
	protected final Properties loadFromStream(InputStream input)
			throws IOException {
		Properties p = new Properties();
		p.load(input);
		return p;
	}
}
