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
package me.icymint.sloth.core.context;

import java.io.File;
import java.util.Properties;

import me.icymint.sloth.Deferred;
import me.icymint.sloth.core.module.Module;

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

	@ContextConfiguration("/contextb.properties")
	public static class ContextB extends AbstractPropertiesContext {

		@Override
		protected void initAndDefer(Module context, Deferred deferred,
				Properties properties, File configpath) {
			System.out.println("Hello this is B");
			System.out.println("Class path is " + configpath);
			Assert.assertFalse(properties.stringPropertyNames().isEmpty());
			Assert.assertEquals("world", properties.getProperty("hello"));
		}

	}

	@Test
	public void testConfiguration() throws Exception {
		Module module = Module.create(ContextB.class);
		module.init();
		try {
			module.fetch(ContextB.class);
		} finally {
			module.close();
		}
	}

	@Test
	public void testNonConfiguration() throws Exception {
		Module module = Module.create(ContextA.class);
		module.init();
		try {
			module.fetch(ContextA.class);
		} finally {
			module.close();
		}
	}
}
