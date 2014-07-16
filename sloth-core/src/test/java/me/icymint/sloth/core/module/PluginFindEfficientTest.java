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
