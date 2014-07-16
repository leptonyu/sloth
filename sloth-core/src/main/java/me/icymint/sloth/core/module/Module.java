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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import me.icymint.sloth.core.defer.Deferred;

/**
 * A multifurcating tree to manage the plugins and their required plugins. A
 * Module object binds some plugins, it creates, runs and destroys them, it also
 * can fork sub module.
 * 
 * <h2>1. Module</h2> Module binds some of plugins, these plugins' life cycles
 * are the same as the module object they are binding. They are created when the
 * parent module is initializing and destroyed when the parent module is
 * destroying. Module can not create Plugins after it has complete the
 * initialization, but it can always fork new sub module when it is ready. New
 * sub module can create binded plugins when they are initializing.
 * 
 * <h2>2. Plugin</h2> Plugins implement the actual functions, and they have to
 * bind one Module to be created, run and be destroyed. Plugins can have
 * required plugins and all required plugins will be ready before the target
 * {@link Plugin} being initialized.
 * <em>One Module can have only one instance of specific {@link Plugin}.</em>
 * 
 * <h2>3. Sub Module</h2> Module can use method {@link #fork(Class...)} to
 * create sub module. All of sub module will be closed when the parent module is
 * closing, or they can be closed before the parent module is closing. Existence
 * of sub module makes Module a multifurcating tree. A Module that has no parent
 * Module named top Module, method {@link #fork(Class...)} can not create top
 * Module.
 * 
 * @author Daniel
 * @see Plugin
 *
 */
public final class Module implements AutoCloseable {

	/**
	 * States of Module
	 * 
	 * @author Daniel
	 *
	 */
	public static enum State {
		/**
		 * New created Module
		 */
		New,
		/**
		 * Module is starting.
		 */
		STARTING,
		/**
		 * Module is ready.
		 */
		READY,
		/**
		 * Module is stopping.
		 */
		STOPPING,
		/**
		 * Module is terminated.
		 */
		TERMINATE,
		/**
		 * Module initialize failed or destroy failed.
		 */
		FAILED
	}

	/**
	 * Create a top Module
	 * 
	 * @param plugins
	 *            Plugins that are binding to the Module.
	 * @return Module object.
	 */
	@SafeVarargs
	public static final Module create(Class<? extends Plugin>... plugins) {
		return new Module(null, plugins);
	}

	/**
	 * Create an empty top Module
	 * 
	 * @return Module object.
	 */
	public static final Module emptyModule() {
		return create();
	}

	/**
	 * 
	 * @return Get the maximum id of Module.
	 */
	public final static long maxId() {
		return _index.get();
	}

	private static final AtomicLong _index = new AtomicLong(0);
	private final Class<? extends Plugin>[] _plugins;
	private final Map<Class<? extends Plugin>, Plugin> _map = new HashMap<>();
	private final Queue<Class<? extends Plugin>> _pes = new ConcurrentLinkedQueue<>();
	private final AtomicReference<State> _state = new AtomicReference<>(
			State.New);
	private final Deferred _defferred = Deferred.create();
	private final long _id;
	private Module _parent = null;
	private final Map<Long, Module> _children = new ConcurrentHashMap<>();

	private ExecutorService _pool;

	private Module(Module context, Class<? extends Plugin>[] clzz) {
		_parent = context;
		_plugins = clzz;
		_id = _index.incrementAndGet();
	}

	private final void checkAndAdd(Plugin m, Queue<Plugin> newAllPlugins)
			throws Exception {
		Class<? extends Plugin> clzz = m.getClass();
		checkAndAddClass(clzz, newAllPlugins);
		if (Plugin.class.isAssignableFrom(clzz.getSuperclass())) {
			checkAndAddClass(clzz.getSuperclass().asSubclass(Plugin.class),
					newAllPlugins);
		}
		for (Class<?> interzz : clzz.getInterfaces()) {
			if (Plugin.class.isAssignableFrom(interzz)) {
				checkAndAddClass(interzz.asSubclass(Plugin.class),
						newAllPlugins);
			}
		}
		// put the plugin to the initial queue to initialize.
		newAllPlugins.offer(m);
	}

	private void checkAndAddClass(Class<? extends Plugin> clzz,
			Queue<Plugin> newAllPlugins) throws Exception {
		RequirePlugins dp = clzz.getDeclaredAnnotation(RequirePlugins.class);
		if (dp != null && dp.value().length > 0) {
			// Iterate all the required plugins.
			for (Class<? extends Plugin> clz : dp.value()) {

				// if the required plugin object exists then skip.
				if (fetch(clz) != null)
					continue;
				// create and add the required plugin and its required
				// plugins.
				checkAndAdd(newPlugin(clz), newAllPlugins);
			}
		}
	}

	/**
	 * Destroy the module.
	 * 
	 * @throws Exception
	 *             If there are errors during destroy the module, it throws
	 *             Exception.
	 */
	@Override
	public final void close() throws Exception {
		if (_state.compareAndSet(State.READY, State.STOPPING)
				|| _state.compareAndSet(State.STARTING, State.STOPPING)) {
			try {
				Module parent = _parent;
				if (parent != null) {
					parent._children.remove(_id);
				}
				_defferred.close();
				_state.set(State.TERMINATE);
			} catch (Exception e) {
				_state.set(State.FAILED);
				throw e;
			}
		}
	}

	/**
	 * Try to get the instance of specific type of Plugin,if the instance dose
	 * not exist in the current module, then it will search it's parent module
	 * until to the top Module. If can not find an instance then it will return
	 * null.
	 * 
	 * @param pluginclzz
	 *            The actual class of Plugin.
	 * @param <M>
	 *            Actual Plugin type.
	 * @return The instance of the plugin. null for none existence of plugin.
	 */
	public final <M extends Plugin> M fetch(Class<M> pluginclzz)
			throws PluginNotFoundException {
		if (pluginclzz != null) {
			Plugin mm = _map.get(pluginclzz);
			if (mm != null)
				return pluginclzz.cast(mm);
			if (_parent != null) {
				return _parent.fetch(pluginclzz);
			}
		}
		return null;
	}

	/**
	 * Try to get the instance of specific type of Plugin,if the instance dose
	 * not exist in the current module, then it will search it's parent module
	 * until to the top Module. If can not find an instance then it will return
	 * null.
	 * <p>
	 * In this method, it will try to find the Plugin instance as long as it can
	 * case into the target class type. So
	 * 
	 * <pre>
	 * Module module = Module.create(PluginA.class, PluginB.class);
	 * module.init();
	 * try {
	 * 	Plugin plugin = find(Plugin.class);
	 * 	// instance plugin will always be the type PluginA in this case.
	 * } finally {
	 * 	module.close();
	 * }
	 * </pre>
	 * 
	 * @param pluginclzz
	 *            The actual class of Plugin.
	 * @param <M>
	 *            Actual Plugin type.
	 * @return The instance of the plugin. null for none existence of plugin.
	 */
	public final <M extends Plugin> M find(Class<M> pluginclzz) {
		if (pluginclzz != null) {
			if (_plugins != null) {
				Plugin abc = _map.get(pluginclzz);
				if (abc != null)
					return pluginclzz.cast(abc);
				LinkedList<Class<? extends Plugin>> left = new LinkedList<>(
						_map.keySet());
				for (Class<? extends Plugin> tc : _plugins) {
					if (pluginclzz.isAssignableFrom(tc)) {
						Plugin m = _map.get(tc);
						if (m != null)
							return pluginclzz.cast(m);
					}
					left.remove(tc);
				}
				for (Class<? extends Plugin> tc : left) {
					if (pluginclzz.isAssignableFrom(tc)) {
						Plugin m = _map.get(tc);
						if (m != null)
							return pluginclzz.cast(m);
					}
				}
				if (_parent != null) {
					return _parent.find(pluginclzz);
				}
			}
		}
		return null;
	}

	/**
	 * Create sub module.
	 * <p>
	 * newPlugins stand for the plugin list binding to the sub module, then will
	 * be created new instances even if the parent Module has the instances of
	 * them. If PluginA are in the list and PluginB are not, and PluginA
	 * requires PluginB, then sub Module will search the parent Module to try to
	 * get the instance of PluginB, if parent Module has no PluginB then sub
	 * module will create an instance of PluginB.
	 * <p>
	 * The returned sub module instance has been initialized, it is ready. Sub
	 * Module can be closed at any time using {@link #close()}, if not it will
	 * be closed when the parent module is closing. This means parent module
	 * will close all the sub module created by it when it is closing.
	 * 
	 * @param newPlugins
	 *            The plugins binding to the sub module.
	 * @return Sub module instance, it has been initialized and is ready now.
	 * @throws IllegalStateException
	 *             If the module is not ready throws the IllegalStateException.
	 * @throws Exception
	 *             Create or initialize the sub module error throws the
	 *             exception.
	 * @see Plugin
	 */
	@SafeVarargs
	public final Module fork(Class<? extends Plugin>... newPlugins)
			throws IllegalStateException, Exception {
		if (!isReady())
			throw new IllegalStateException();
		// Create new module instance.
		Module mc = new Module(this, newPlugins);
		mc.init();
		// After being initialized, register it to the parent module.
		_children.put(mc._id, mc);
		return mc;
	}

	/**
	 * 
	 * @return Parent Module, top Module will return null.
	 */
	public final Module getParent() {
		return _parent;
	}

	/**
	 * 
	 * @return Thread service pool will be used for running plugin
	 *         asynchronously.
	 */
	private ExecutorService getPool() {
		if (_parent != null) {
			return _parent.getPool();
		} else {
			return _pool;
		}
	}

	/**
	 * 
	 * @return the unique id of this Module.
	 */
	public final long id() {
		return _id;
	}

	/**
	 * Initialize the Module. With the help of Deferred object, we can collect
	 * the clean operation when we do the initialization.
	 * <p>
	 * If error happens in the initializing process, {@link Deferred#close()}
	 * will be invoked automatically to ensure clean the broken module.
	 * <p>
	 * We can write the code like this:
	 * 
	 * <pre>
	 * module.init();
	 * try {
	 * 	// Do something.
	 * 	//
	 * } finally {
	 * 	module.close();
	 * }
	 * </pre>
	 * 
	 * @throws Exception
	 *             Initialize error will throw exception.
	 */
	public final void init() throws Exception {
		if (_state.compareAndSet(State.New, State.STARTING)) {
			try {
				if (_parent == null) {
					_pool = Executors.newCachedThreadPool();
					_defferred.defer(_pool::shutdown);
				}
				// Create plugins binding on this module.
				if (_plugins != null && _plugins.length > 0) {
					Exception ex = new Exception();
					Queue<Plugin> newPlugins = new LinkedList<>();
					// Distinct the plugin list, only create one instance of one
					// type.
					Arrays.stream(_plugins).distinct().forEach(clz -> {
						try {
							newPlugins.offer(newPlugin(clz));
						} catch (Exception e) {
							ex.addSuppressed(e);
						}
					});
					if (ex.getSuppressed().length > 0)
						throw ex;
					Queue<Plugin> newAllPlugins = new LinkedList<>();
					newPlugins.forEach(m -> {
						try {
							checkAndAdd(m, newAllPlugins);
						} catch (Exception e) {
							ex.addSuppressed(e);
						}
					});
					if (ex.getSuppressed().length > 0)
						throw ex;
					// Initialize the plugins.
					for (Plugin p : newAllPlugins) {
						p.initAndDeferClose(this, _defferred);
						// check the plugins.
						if (p instanceof ExecutionThreadPlugin) {
							try {
								Future<Exception> f = getPool().submit(
										() -> {
											try {
												((ExecutionThreadPlugin) p)
														.execute(this);
												return null;
											} catch (Exception e) {
												return e;
											} finally {
												_pes.offer(p.getClass());
											}
										});
								_defferred.defer(() -> {
									while (!f.isDone()) {
										TimeUnit.MILLISECONDS.sleep(10);
									}
									Exception e = f.get();
									if (e != null) {
										throw e;
									}
								});
							} catch (Exception e) {
								ex.addSuppressed(e);
							}
						}
					}
					if (ex.getSuppressed().length > 0)
						throw ex;
				}
				// clean all the sub module created by this module.
				_defferred.defer(() -> {
					Exception eex = new Exception();
					_children.values().parallelStream().forEach(a -> {
						try {
							a.close();
						} catch (Exception e) {
							eex.addSuppressed(e);
						}
					});
					if (eex.getSuppressed().length > 0) {
						throw eex;
					}
				});
				// update state
				_state.compareAndSet(State.STARTING, State.READY);
			} catch (Exception e) {
				// Clean the module if initializing error.
				try {
					close();
				} catch (Exception ee) {
					e.addSuppressed(ee);
				}
				_state.compareAndSet(State.STARTING, State.FAILED);
				throw e;
			}
		} else {
			// can not initialize twice.
			throw new IllegalAccessException();
		}
	}

	/**
	 * 
	 * @return Check the health of module.
	 */
	public final boolean isHealthy() {
		return isReady() && _pes.isEmpty();
	}

	/**
	 * 
	 * @return if the Module is ready.
	 */
	public final boolean isReady() {
		return _state.get() == State.READY;
	}

	private final Plugin newPlugin(Class<? extends Plugin> clz)
			throws Exception {
		// create Plugin instance
		Plugin m = clz.newInstance();
		// add it to the pool.
		_map.put(clz, m);
		return m;
	}

	/**
	 * 
	 * @return All the classes of plugins created by this module.
	 */
	public final Set<Class<? extends Plugin>> plugins() {
		return _map.keySet();
	}

	/**
	 * 
	 * @return Current state of the module.
	 */
	public final State state() {
		return _state.get();
	}

	@Override
	public String toString() {
		Module p = _parent;
		if (p != null) {
			return p.toString() + "-" + _id;
		} else {
			return "Module:" + _id;
		}
	}
}
