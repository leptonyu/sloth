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
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.icymint.sloth.core.context.AbstractContext;
import me.icymint.sloth.core.context.ContextConfiguration;
import me.icymint.sloth.core.defer.Deferred;
import me.icymint.sloth.core.module.Module;

import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.resource.Resource;
import org.yaml.snakeyaml.Yaml;

/**
 * A simple jetty plugin used to create the RESTful service easily.
 * <p>
 * This plugin use jetty.yml as the only one input configuration file, and it
 * use the format of YAML and be parsed by <a
 * href="http://www.snakeyaml.org">snakeyaml</a>.
 * <p>
 * {@link #register(int, Object)} and {@link #unregister(long)} make this plugin
 * has the abilities to dynamically install or uninstall the api handlers. And
 * it is thread safe to do so.
 * 
 * @author Daniel Yu
 *
 */
@ContextConfiguration("/jetty.yml")
public class JettyPlugin extends AbstractContext<JsonObject> {
	private class MainHandler extends AbstractHandler {
		private final String api;
		private final AtomicReference<ArrayList<HandlerProvider>> _providers = new AtomicReference<>();
		private final Set<HandlerProvider> _list = new TreeSet<>((i, j) -> {
			int dif = i.priority() - j.priority();
			if (dif == 0) {
				return (i.id() > j.id()) ? 1 : -1;
			}
			return dif;
		});

		private MainHandler(String api) {
			this.api = api;
		}

		@Override
		public void handle(String target, Request baseRequest,
				HttpServletRequest request, HttpServletResponse response)
				throws IOException, ServletException {
			if (target.startsWith(api)) {
				target = target.substring(api.length());
				HttpMethod method = HttpMethod.fromString(baseRequest
						.getMethod());
				if (method == null) {
					return;
				}
				SimpleHandler sh = null;
				for (HandlerProvider hp : _providers.get()) {
					sh = hp.search(target, method);
					if (sh != null) {
						try {
							sh.handle(target, baseRequest, request, response);
						} catch (IOException | ServletException e) {
							throw e;
						} catch (Exception e) {
							throw new ServletException(e);
						}
						return;
					}
				}
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				baseRequest.setHandled(true);
			}
		}

		/*
		 * This method provide the support of which method can be wrapped into
		 * an SimpleHandler.
		 */
		private void parseMethod(HandlerProvider hps, Object object,
				Method method, String path, String shortpath, HttpMethod[] hms) {
			if (hms.length == 0)
				return;
			Class<?> rt = method.getReturnType();
			Class<?>[] pts = method.getParameterTypes();
			// String xxx()
			if (rt == String.class) {
				if (pts.length == 0) {
					// System.out.println(path + ">>" + hm + ">>"
					// + method.getName());
					hps.inserts(path, hms, (a, b, c, d) -> {
						Object value = method.invoke(object);
						if (value != null) {
							d.getWriter().write(value.toString());
						}
						d.setStatus(HttpServletResponse.SC_OK);
						b.setHandled(true);
					});
					return;
				} else if (pts.length == 1 && pts[0] == String.class) {
					hps.inserts(
							path,
							hms,
							(a, b, c, d) -> {
								Object value = method.invoke(object,
										IO.toString(b.getReader()));
								if (value != null) {
									d.getWriter().write(value.toString());
								}
								d.setStatus(HttpServletResponse.SC_OK);
								b.setHandled(true);
							});
				}
			}
			if (rt == Void.class || rt == void.class) {
				if (pts.length == 0) {
					hps.inserts(path, hms, (a, b, c, d) -> {
						try {
							method.invoke(object);
						} catch (Exception e) {
							throw new ServletException(e);
						}
					});
				} else if (pts.length == 4 && pts[0] == String.class
						&& pts[1] == Request.class
						&& pts[2] == HttpServletRequest.class
						&& pts[3] == HttpServletResponse.class) {
					hps.inserts(path, hms, (a, b, c, d) -> {
						try {
							method.invoke(object, shortpath, b, c, d);
						} catch (Exception e) {
							throw new ServletException(e);
						}
					});
				} else if (pts.length == 4 && pts[0] == String[].class
						&& pts[1] == Request.class
						&& pts[2] == HttpServletRequest.class
						&& pts[3] == HttpServletResponse.class) {
					String[] sp = shortpath.split("\\/");
					hps.inserts(path, hms,
							(a, b, c, d) -> method.invoke(object, sp, b, c, d));
				}
			}
		}

		/**
		 * Parse the target object to create a {@link HandlerProvider}.
		 * 
		 * @param priority
		 *            Priority the new {@link HandlerProvider}, dispatcher will
		 *            use the priority to determine which
		 *            {@link HandlerProvider} should be searched first.
		 * @param object
		 *            target object.
		 * @return the unique id of the {@link HandlerProvider}, and this id can
		 *         be use for unregister the {@link HandlerProvider}.
		 */
		public long register(int priority, Object object) {
			if (object == null) {
				return -1;
			}
			HandlerProvider hps = new HandlerProvider(priority);
			HttpSupport hs = object.getClass().getAnnotation(HttpSupport.class);
			String prefix = hs == null ? "" : hs.value();
			for (Method method : object.getClass().getMethods()) {
				HttpMethods hms = method.getAnnotation(HttpMethods.class);
				if (hms != null) {
					String shortpath = hms.path();
					parseMethod(hps, object, method, prefix + shortpath,
							shortpath, hms.value());
				}
			}
			synchronized (_providers) {
				_list.add(hps);
				_providers.set(new ArrayList<>(_list));
			}
			return hps.id();
		}

		/**
		 * unregister the {@link HandlerProvider} with the given id.
		 * 
		 * @param id
		 *            the id of {@link HandlerProvider} needed to be removed.
		 */
		public void unregister(long id) {
			synchronized (_providers) {
				Iterator<HandlerProvider> it = _list.iterator();
				while (it.hasNext()) {
					HandlerProvider hp = it.next();
					if (hp.id() == id) {
						it.remove();
						_providers.set(new ArrayList<>(_list));
						return;
					}
				}
			}
		}
	}

	private File _base;
	private File _conf;
	private File _web;
	private MainHandler _mainhandler;

	/**
	 * Base directory of the app, all the files of the app should in this
	 * directory.
	 * 
	 * @return Base directory of the app.
	 */
	public File getBaseDirectory() {
		return _base;
	}

	/**
	 * Configuration directory, jetty.yml is in this directory.
	 * 
	 * @return configuration directory.
	 */
	public File getConfigDirectory() {
		return _conf;
	}

	/**
	 * Web directory, all of html, javascript, css files should in this
	 * directory. And also with an index.html in it.
	 * 
	 * @return web directory
	 */
	public File getWebDirectory() {
		return _web;
	}

	@Override
	protected void initAndDefer(Module module, Deferred deferred,
			JsonObject config, File configpath) throws Exception {
		_base = Objects.requireNonNull(configpath).getParentFile();
		_conf = new File(_base, "conf");
		_web = new File(_base, "web");
		JsonObject jetty = config.getJsonObject("jetty");
		Server server = new Server(jetty.getInt("port", 8080));
		ContextHandler handler = new ContextHandler();
		handler.setBaseResource(Resource.newResource(getWebDirectory()));
		_mainhandler = new MainHandler(jetty.getString("api", "/api"));
		handler.setHandler(_mainhandler);
		server.setHandler(handler);
		server.start();
		deferred.defer(server::stop);
	}

	@Override
	protected JsonObject loadFromStream(InputStream input) throws IOException {
		return (JsonObject) xxx(new Yaml().load(input));
	}

	private Object xxx(Object obj) {
		if (obj instanceof List) {
			JsonArrayBuilder ja = Json.createArrayBuilder();
			for (Object o : List.class.cast(obj)) {
				o = xxx(o);
				if (o instanceof JsonValue) {
					ja.add((JsonValue) o);
				} else if (o instanceof Boolean) {
					ja.add((boolean) o);
				} else if (o instanceof Integer) {
					ja.add((int) o);
				} else {
					ja.add(o.toString());
				}
			}
			return ja.build();
		} else if (obj instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> yaml = Map.class.cast(obj);
			JsonObjectBuilder jo = Json.createObjectBuilder();
			for (String key : yaml.keySet()) {
				Object o = xxx(yaml.get(key));
				if (o instanceof JsonValue) {
					jo.add(key, (JsonValue) o);
				} else if (o instanceof Boolean) {
					jo.add(key, (boolean) o);
				} else if (o instanceof Integer) {
					jo.add(key, (int) o);
				} else {
					jo.add(key, o.toString());
				}
			}
			return jo.build();
		} else {
			return obj;
		}
	}

	/**
	 * Parse the target object to create a {@link HandlerProvider}.
	 * 
	 * @param priority
	 *            Priority the new {@link HandlerProvider}, dispatcher will use
	 *            the priority to determine which {@link HandlerProvider} should
	 *            be searched first.
	 * @param object
	 *            target object.
	 * @return the unique id of the {@link HandlerProvider}, and this id can be
	 *         use for unregister the {@link HandlerProvider}.
	 */
	public long register(int priority, Object object) {
		return _mainhandler.register(priority, object);
	}

	/**
	 * unregister the {@link HandlerProvider} with the given id.
	 * 
	 * @param id
	 *            the id of {@link HandlerProvider} needed to be removed.
	 */
	public void unregister(long id) {
		_mainhandler.unregister(id);
	}

}
