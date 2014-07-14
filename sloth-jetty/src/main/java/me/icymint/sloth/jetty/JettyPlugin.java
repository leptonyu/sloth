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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.regex.Matcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.icymint.sloth.core.context.AbstractContext;
import me.icymint.sloth.core.defer.Deferred;
import me.icymint.sloth.core.json.JsonObject;
import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.core.module.RequirePlugins;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.yaml.snakeyaml.Yaml;

/**
 * 
 * @author Daniel
 *
 */
@RequirePlugins(HandlerTreePlugin.class)
public class JettyPlugin extends AbstractContext<JsonObject> {

	private class JettyDispatcher extends AbstractHandler {

		private final HandlerTreePlugin _tree;
		private final String _prefix;

		public JettyDispatcher(String api, HandlerTreePlugin tree) {
			_prefix = api;
			_tree = tree;
		}

		@Override
		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			if (target.startsWith(_prefix)) {
				target = target.substring(_prefix.length());
				Matcher m = HandlerTreePlugin.URLPATTERN.matcher(target);
				while (m.matches()) {
					String name = m.group(1);
					if (null != name) {
					}
					String next = m.group(2);
					m = HandlerTreePlugin.URLPATTERN.matcher(next);
				}
			}
		}

	}

	public interface HandlerPlugin {
		void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException;

		HttpMethod method();
	}

	private File _base;
	private File _conf;
	private File _web;
	private File _lib;
	private File _bin;

	@Override
	protected void initAndDefer(Module module, Deferred deferred,
			JsonObject config, File configpath) throws Exception {
		_base = Objects.requireNonNull(configpath).getParentFile();
		_conf = new File(_base, "conf");
		_web = new File(_base, "web");
		_lib = new File(_base, "lib");
		_bin = new File(_base, "bin");
		JsonObject jetty = config.getValue("jetty");
		Server server = new Server(jetty.getValue("port").asInt(8080));
		ResourceHandler handler = new ResourceHandler();
		handler.setBaseResource(Resource.newResource(getWebDirectory()));
		handler.setHandler(new JettyDispatcher(jetty.asString("/api"), module
				.fetch(HandlerTreePlugin.class)));
		server.setHandler(handler);
		server.start();
		deferred.defer(server::stop);
	}

	public File getBaseDirectory() {
		return _base;
	}

	public File getWebDirectory() {
		return _web;
	}

	public File getBinDirectory() {
		return _bin;
	}

	public File getConfigDirectory() {
		return _conf;
	}

	public File getLibDirectory() {
		return _lib;
	}

	@Override
	protected JsonObject loadFromStream(InputStream input) throws IOException {
		return new JsonObject(new Yaml().load(input));
	}

}
