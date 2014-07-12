/**
 * 
 */
package me.icymint.sloth.module;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import me.icymint.sloth.defer.Deferred;

/**
 * @author Daniel
 *
 */
public final class Module implements AutoCloseable {
	public static enum State {
		/**
		 * 新建模块。
		 */
		New,
		/**
		 * 正在启动模块。
		 */
		STARTING,
		/**
		 * 已经准备就绪。
		 */
		READY,
		/**
		 * 正在关闭
		 */
		STOPPING,
		/**
		 * 关闭模块。
		 */
		TERMINATE,
		/**
		 * 模块启动或关闭失败
		 */
		FAILED
	}

	/**
	 * 创建一个具有基础插件的顶级模块。
	 * 
	 * @param plugins
	 *            插件类列表。
	 * @return 顶级模块。
	 */
	@SafeVarargs
	public static final Module create(Class<? extends Plugin>... plugins) {
		return new Module(null, plugins);
	}

	/**
	 * 创建一个空的顶级模块。
	 * 
	 * @return 顶级模块。
	 */
	public static final Module emptyModule() {
		return create();
	}

	private final Class<? extends Plugin>[] plugins;
	private final Map<Class<? extends Plugin>, Plugin> _map = new HashMap<>();
	private final AtomicReference<State> _state = new AtomicReference<>(
			State.New);
	private final Deferred _defferred = Deferred.create();
	private final long id;
	private Module _parent = null;
	private Map<Long, Module> _children = new ConcurrentHashMap<>();
	private static final AtomicLong _index = new AtomicLong(0);

	private Module(Module context, Class<? extends Plugin>[] clzz) {
		_parent = context;
		plugins = clzz;
		id = _index.incrementAndGet();
	}

	/**
	 * 卸载模块。
	 * 
	 * @throws Exception
	 *             无法卸载模块的时候抛出违例。
	 */
	@Override
	public final void close() throws Exception {
		if (_state.compareAndSet(State.READY, State.STOPPING)) {
			try {
				Module parent = _parent;
				if (parent != null) {
					parent._children.remove(id);
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
	 * 创建子模块，其可以构造新的插件实例或者引用父模块的插件实例。
	 * <p>
	 * newPlugins参数表示该子模块需要新建的插件实例，不管父模块是否存在该类型的实例。 除非在newPlugins里面已经明确列出了
	 * ，否则当PluginA在列表中，PluginB不在列表中，则：当父模块中存在PluginB的实例时，子模块将不会创建新的PluginB的实例
	 * ；当父模块中不存在PluginB的实例的时候，子模块会创建一个PluginB的实例。
	 * <p>
	 * 本方法返回的子模块实例已经经过初始化，因此可以直接使用。子模块可以主动关闭，关闭的方式为调用方法 {@link #close()}
	 * 。也可以在不关闭，当其父模块关闭的时候将自动关闭所有子模块。
	 * 
	 * @param newPlugins
	 *            本模块需要新创建的插件类。
	 * @return 子模块实例，其同时已经进行了初始化，可以正常使用。
	 * @throws IllegalStateException
	 *             当本方法在 {@link Plugin#initAndDeferClose(Module, Deferred)}
	 *             方法中调用时，将抛出该违例。
	 * @throws Exception
	 *             无法创建子模块实例。
	 * @see Plugin
	 */
	@SafeVarargs
	public final Module fork(Class<? extends Plugin>... newPlugins)
			throws IllegalStateException, Exception {
		if (!isReady())
			throw new IllegalStateException();
		// 创建模块实例。
		Module mc = new Module(this, newPlugins);
		mc.init();
		// 初始化成功，需要把其卸载方法注册到父模块中。
		_children.put(mc.id, mc);
		return mc;
	}

	/**
	 * 获取本模块的插件实例，当本模块不存在该插件实例的时候，将尝试获取父模块的插件实例，依次直到获取成功，或者无法获取。
	 * 
	 * @param clz
	 *            插件类名称。
	 * @return 可用的插件对象。
	 * @throws PluginNotFoundException
	 */
	public final <M extends Plugin> M get(Class<M> clz)
			throws PluginNotFoundException {
		if (clz != null) {
			Plugin mm = _map.get(clz);
			if (mm != null)
				return clz.cast(mm);
			if (_parent != null) {
				return _parent.get(clz);
			}
		}
		throw new PluginNotFoundException(clz.getName());
	}

	/**
	 * 
	 * @return 获取父模块，顶级模块返回null。
	 */
	public final Module getParent() {
		return _parent;
	}

	/**
	 * 初始化模块，本方法具有自我清理的功能，当初始化失败的时候，会自动调用已经注册成功的卸载操作。
	 * <p>
	 * 可以进行如下操作，初始化方法具有原子性，要么初始化成功，要么失败，不会存在半成功半失败的状态：
	 * 
	 * <pre>
	 * module.init();
	 * try {
	 * 	// 此处不必写模块失败的时候对初始化成功的前半部分模块的清理操作。
	 * 	// 此处写相关逻辑。。
	 * 	//
	 * } finally {
	 * 	module.close();
	 * }
	 * </pre>
	 * 
	 * @throws Exception
	 *             当模块初始化失败的时候，抛出违例。
	 */
	public final void init() throws Exception {
		if (_state.compareAndSet(State.New, State.STARTING)) {
			try {
				// 创建插件实例队列。
				if (plugins != null && plugins.length > 0) {
					Exception ex = new Exception();
					Queue<Plugin> newPlugins = new LinkedList<>();
					// 创建需要创建的插件实例列表，去除重复。
					Arrays.stream(plugins).distinct().forEach(clz -> {
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
					// 对插件进行初始化。
					for (Plugin p : newAllPlugins) {
						p.initAndDeferClose(this, _defferred);
					}
				}
				// 清理fork的模块对象。
				_defferred.defer(() -> {
					Exception eex = new Exception();
					// 并发清理。
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
				// 初始化完成标识。
				_state.compareAndSet(State.STARTING, State.READY);
			} catch (Exception e) {
				// 如果初始化出错则自动清理，保证初始化的原子性。
				try {
					close();
				} catch (Exception ee) {
					e.addSuppressed(ee);
				}
				_state.compareAndSet(State.STARTING, State.FAILED);
				throw e;
			}
		} else {
			// 不允许进行二次初始化！
			throw new IllegalAccessException();
		}
	}

	private final Plugin newPlugin(Class<? extends Plugin> clz)
			throws Exception {
		// 创建实例
		Plugin m = clz.newInstance();
		// 添加实例到本模块的插件池中。
		_map.put(clz, m);
		// 返回该插件实例。
		return m;
	}

	private final void checkAndAdd(Plugin m, Queue<Plugin> newAllPlugins)
			throws Exception {
		// 获取该插件依赖的依赖插件。
		DepPlugins dp = m.getClass().getAnnotation(DepPlugins.class);
		if (dp != null && dp.value().length > 0) {
			// 遍历所有依赖插件类型。
			for (Class<? extends Plugin> clz : dp.value()) {
				try {
					// 如果已经存在该插件的实例，则跳过。
					get(clz);
					continue;
				} catch (Exception e) {
				}
				// 创建并添加依赖插件。
				checkAndAdd(newPlugin(clz), newAllPlugins);
			}
		}
		// 把插件注册到新插件队列里面。放在是为了让其依赖的所有插件都排在该插件实例前面进行初始化。
		newAllPlugins.offer(m);
	}

	/**
	 * 
	 * @return 该模块是否可用。
	 */
	public final boolean isReady() {
		return _state.get() == State.READY;
	}

	/**
	 * 获取模块的当前状态。
	 * 
	 * @return 当前状态。
	 */
	public final State state() {
		return _state.get();
	}

	@Override
	public String toString() {
		Module p = _parent;
		if (p != null) {
			return p.toString() + "-" + id;
		} else {
			return "Module:" + id;
		}
	}

	/**
	 * 
	 * @return 获取当前最大的模块序号。
	 */
	public final static long maxId() {
		return _index.get();
	}

	/**
	 * 
	 * @return 获取本模块的唯一ID。
	 */
	public final long id() {
		return id;
	}
}
