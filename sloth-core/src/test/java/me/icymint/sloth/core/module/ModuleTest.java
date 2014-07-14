/*
 * Copyright (C) 2014 Daniel Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.icymint.sloth.core.module;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import me.icymint.sloth.core.defer.Deferred;

import org.junit.Assert;
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
			System.out.println("Hello, this Module " + name());
		}

	}

	public static class PluginB implements Plugin {

		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			System.out.println("Hello, this Module " + name());
		}

	}

	@RequirePlugins({ PluginB.class })
	public static class PluginC implements Plugin {
		@Override
		public void initAndDeferClose(Module context, Deferred deferred)
				throws Exception {
			System.out.println("Hello, this Module " + name());
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
				TimeUnit.MILLISECONDS.sleep(10);
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
			for (int i = 0; i < 10000; i++) {
				abc.fork(PluginE.class).close();
			}
			for (int i = 0; i < 10000; i++) {
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
			abc.fetch(PluginB.class);
			abc.fetch(PluginC.class);
		} finally {
			abc.close();
		}
	}

	@Test
	public void testPluginFound() throws Exception {
		Module abc = Module.create(PluginA.class);
		abc.init();
		try {
			abc.fetch(PluginA.class);
		} finally {
			abc.close();
		}
	}

	@Test
	public void testPluginNotFound() throws Exception {
		Module abc = Module.create();
		abc.init();
		try {
			Assert.assertNull(abc.fetch(PluginA.class));
		} finally {
			abc.close();
		}
	}

}
