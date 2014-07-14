package me.icymint.sloth.core.module;

import me.icymint.sloth.core.module.PluginTest.PluginA;
import me.icymint.sloth.core.module.PluginTest.PluginC;
import me.icymint.sloth.core.module.PluginTest.PluginD;

import org.junit.Test;

public class PluginFindEfficientTest {

	@Test
	public void testA() throws Exception {
		Module module = Module.create(PluginD.class);
		module.init();
		try {
			long start = System.currentTimeMillis();
			for (int i = 0; i < 10000000; i++) {
				module.find(PluginC.class);
			}
			System.out.println("Find 1 " + (System.currentTimeMillis() - start)
					+ "ms");

			start = System.currentTimeMillis();
			for (int i = 0; i < 10000000; i++) {
				module.find(PluginA.class);
			}
			System.out.println("Find 1 has "
					+ (System.currentTimeMillis() - start) + "ms");

			start = System.currentTimeMillis();
			for (int i = 0; i < 10000000; i++) {
				module.fetch(PluginA.class);
			}
			System.out.println("Fetch has"
					+ (System.currentTimeMillis() - start) + "ms");
		} finally {
			module.close();
		}
	}
}
