package me.icymint.sloth.context;

import java.io.File;
import java.util.Properties;

import me.icymint.sloth.defer.Deferred;
import me.icymint.sloth.module.Module;

import org.junit.Assert;
import org.junit.Test;

public class ContextTest {
	@ContextConfiguration
	public static class ContextA extends AbstractPropertiesContext {

		@Override
		protected void initAndDefer(Module context, Deferred deferred,
				Properties properties, File configpath) {
			System.out.println("Hello");
			Assert.assertNull(properties);
		}

	}

	@ContextConfiguration(path = "/contextb.properties")
	public static class ContextB extends AbstractPropertiesContext {

		@Override
		protected void initAndDefer(Module context, Deferred deferred,
				Properties properties, File configpath) {
			System.out.println("Hello this is B");
			System.out.println("配置文件目录为" + configpath);
			Assert.assertFalse(properties.stringPropertyNames().isEmpty());
			Assert.assertEquals("world", properties.getProperty("hello"));
		}

	}

	@Test
	public void testConfiguration() throws Exception {
		Module module = Module.create(ContextB.class);
		module.init();
		try {
			module.get(ContextB.class);
		} finally {
			module.close();
		}
	}

	@Test
	public void testNonConfiguration() throws Exception {
		Module module = Module.create(ContextA.class);
		module.init();
		try {
			module.get(ContextA.class);
		} finally {
			module.close();
		}
	}
}
