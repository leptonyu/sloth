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

import org.junit.Assert;
import org.junit.Test;

import com.google.common.io.Resources;

/**
 * @author Daniel Yu
 */
public class JettyTest {

	public class HelloWorld {
		@HttpMethods(path = "/hello")
		public String hello() {
			return Hello;
		}

		@HttpMethods(path = "/set/:id")
		public String setid() {
			return ID;
		}
	}

	public class HelloWorld2 {
		@HttpMethods(path = "/hello")
		public String hello() {
			return Hello + "+1";
		}

		@HttpMethods(path = "/set/:id")
		public String setid() {
			return ID + "+1";
		}
	}

	public static final String Hello = "Hello,world!";

	public static final String ID = "id";

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
			String abc = Resources.toString(new URL(baseurl + "/hello"),
					Charset.forName("UTF-8"));
			Assert.assertEquals(Hello, abc);
			System.out.println(abc);
			for (int i = 0; i < 100; i++) {
				abc = Resources.toString(new URL(baseurl + "/set/" + i),
						Charset.forName("UTF-8"));
				Assert.assertEquals(ID, abc);
			}
			try {
				Resources.toString(new URL(baseurl + "/set/hello"),
						Charset.forName("UTF-8"));
				Assert.fail("Here must have 400 error!");
			} catch (Exception e) {
			}
			// test priority
			long idx = jp.register(0, new HelloWorld2());
			abc = Resources.toString(new URL(baseurl + "/hello"),
					Charset.forName("UTF-8"));
			Assert.assertEquals(Hello + "+1", abc);
			abc = Resources.toString(new URL(baseurl + "/set/0"),
					Charset.forName("UTF-8"));
			Assert.assertEquals(ID + "+1", abc);
			jp.unregister(idx);
			// recover
			abc = Resources.toString(new URL(baseurl + "/hello"),
					Charset.forName("UTF-8"));
			Assert.assertEquals(Hello, abc);
			abc = Resources.toString(new URL(baseurl + "/set/0"),
					Charset.forName("UTF-8"));
			Assert.assertEquals(ID, abc);
			jp.unregister(id);
			try {
				Resources.toString(new URL(baseurl + "/hello"),
						Charset.forName("UTF-8"));
				Assert.fail("Here must have 400 error!");
			} catch (Exception e) {
			}
		} finally {
			module.close();
		}
	}
}
