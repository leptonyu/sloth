/**
 * 
 */
package me.icymint.sloth.module;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import me.icymint.sloth.defer.Deferred;

import org.junit.Test;

/**
 * @author Daniel
 *
 */
public class ModuleTest {

	public static class PluginA implements Plugin {

		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			System.out.println("Hello, this Module " + getClass().getName());
		}

	}

	public static class PluginB implements Plugin {

		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			System.out.println("Hello, this Module " + getClass().getName());
		}

	}

	@DepPlugins({ PluginB.class })
	public static class PluginC implements Plugin {
		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			System.out.println("Hello, this Module " + getClass().getName());
		}

	}

	public static class PluginD implements Plugin {
		private static final AtomicLong _id = new AtomicLong();

		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			long id = _id.incrementAndGet();
			System.out.println("Plugin D " + id + " start");
			deferred.defer(() -> {
				// long xid = _id.getAndDecrement();
				// Assert.assertEquals(id, xid);
				System.out.println("Plugin D " + id + " stoping");
				TimeUnit.MILLISECONDS.sleep(100);
				System.out.println("Plugin D " + id + " stop");
			});
		}
	}

	public static class PluginE implements Plugin {

		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
		}
	}

	@Test
	public void testModuleEffective() throws Exception {
		Module abc = Module.create();
		System.out.println(abc);
		abc.init();
		try {
			for (int i = 0; i < 100000; i++) {
				abc.fork(PluginE.class).close();
			}
			for (int i = 0; i < 100000; i++) {
				abc.fork(PluginE.class);
			}
		} finally {
			abc.close();
		}
		System.out.println(Module.maxId());
	}

	@Test
	public void testMultiModule() throws Exception {
		Module abc = Module.create();
		System.out.println(abc);
		abc.init();
		try {
			for (int i = 0; i < 100; i++) {
				Module m = abc.fork(PluginD.class);
				System.out.println(m);
				if (i % 10 == 0) {
					for (int j = 0; j < 10; j++) {
						Module n = m.fork(PluginD.class);
						System.out.println(n);
					}
					m.close();
				}
			}
		} finally {
			abc.close();
		}
	}

	@Test
	public void testPluginDep() throws Exception {
		Module abc = Module.create(PluginC.class);
		abc.init();
		try {
			abc.get(PluginB.class);
			abc.get(PluginC.class);
		} finally {
			abc.close();
		}
	}

	@Test
	public void testPluginFound() throws Exception {
		Module abc = Module.create(PluginA.class);
		abc.init();
		try {
			abc.get(PluginA.class);
		} finally {
			abc.close();
		}
	}

	@Test(expected = PluginNotFoundException.class)
	public void testPluginNotFound() throws Exception {
		Module abc = Module.create();
		abc.init();
		try {
			abc.get(PluginA.class);
		} finally {
			abc.close();
		}
	}

}
