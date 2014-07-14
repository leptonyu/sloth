package me.icymint.sloth.core.module;

import me.icymint.sloth.core.defer.Deferred;

import org.junit.Assert;
import org.junit.Test;

public class PluginTest {

	public static class PluginA implements Plugin {

		@Override
		public void initAndDeferClose(Module module, Deferred deferred)
				throws Exception {
			RequirePlugins rp = getClass().getAnnotation(RequirePlugins.class);
			Assert.assertNull(rp);
		}

	}

	@RequirePlugins(PluginA.class)
	public static class PluginB implements Plugin {

		@Override
		public void initAndDeferClose(Module module, Deferred deferred)
				throws Exception {
			RequirePlugins rp = getClass().getAnnotation(RequirePlugins.class);
			Assert.assertNotNull(rp);
			Class<? extends Plugin>[] xx = rp.value();
			Assert.assertEquals(1, xx.length);
			Assert.assertEquals(PluginA.class, xx[0]);
			Assert.assertNull(getClass().getSuperclass().getAnnotation(
					RequirePlugins.class));
		}
	}

	@RequirePlugins
	public static class PluginC extends PluginB {

		@Override
		public void initAndDeferClose(Module module, Deferred deferred)
				throws Exception {
			RequirePlugins rp = getClass().getAnnotation(RequirePlugins.class);
			Assert.assertEquals(rp,
					getClass().getDeclaredAnnotation(RequirePlugins.class));
			Assert.assertNotNull(rp);
			Class<? extends Plugin>[] xx = rp.value();
			Assert.assertEquals(0, xx.length);
			rp = getClass().getSuperclass().getAnnotation(RequirePlugins.class);
			Assert.assertNotNull(rp);
			xx = rp.value();
			Assert.assertEquals(1, xx.length);
			Assert.assertEquals(PluginA.class, xx[0]);
		}

	}

	public static class PluginD extends PluginB {

		@Override
		public void initAndDeferClose(Module module, Deferred deferred)
				throws Exception {
			RequirePlugins rp = getClass().getAnnotation(RequirePlugins.class);
			Assert.assertEquals(rp,
					getClass().getDeclaredAnnotation(RequirePlugins.class));
			rp = getClass().getSuperclass().getAnnotation(RequirePlugins.class);
			Assert.assertNotNull(rp);
			Class<? extends Plugin>[] xx = rp.value();
			Assert.assertEquals(1, xx.length);
			Assert.assertEquals(PluginA.class, xx[0]);
		}
	}

	@Test
	public void testRequiredPluginCanBeInheritFromSuperClassAndInterfaces()
			throws Exception {
		Module module = Module.create();
		module.init();
		try {
			Module a = module.fork(PluginA.class);
			Assert.assertEquals(1, a.plugins().size());
			Module b = module.fork(PluginB.class);
			Assert.assertEquals(2, b.plugins().size());
			Module c = module.fork(PluginC.class);
			Assert.assertEquals(2, c.plugins().size());
			Module d = module.fork(PluginD.class);
			Assert.assertEquals(2, d.plugins().size());
		} finally {
			module.close();
		}
	}
}
