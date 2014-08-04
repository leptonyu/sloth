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
package me.icymint.sloth.jetty;

import java.net.URL;
import java.nio.charset.Charset;

import javax.json.JsonObject;

import me.icymint.sloth.core.module.Module;

import org.eclipse.jetty.http.HttpMethod;
import org.junit.Test;

import com.google.common.io.Resources;

/**
 * @author Daniel Yu
 */
public class JettyEffiicentTest {
	private class HelloWorld {
		@HttpMethods(path = "/hello")
		public String hello() {
			return "HEllo";
		}

		@HttpMethods(path = "/helloa", value = { HttpMethod.POST })
		public String helloa(String name) {
			return String.format("Hello, %s !", name);
		}
	}

	@Test
	public void checkJettyWorksTest() throws Exception {
		Module module = Module.create(JettyPlugin.class);
		module.init();
		try {
			JettyPlugin jp = module.fetch(JettyPlugin.class);
			long id = jp.register(1, new HelloWorld());
			JsonObject config = jp.configuration().getJsonObject("jetty");
			String baseurl = "http://127.0.0.1:" + config.getInt("port", 8080)
					+ config.getString("api", "/api");
			long start = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				Resources.toString(new URL(baseurl + "/hello"),
						Charset.forName("UTF-8"));
			}
			System.out.println("Total " + (System.currentTimeMillis() - start)
					+ "ms");
			jp.unregister(id);
		} finally {
			module.close();
		}
	}
}
