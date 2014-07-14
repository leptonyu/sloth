package me.icymint.sloth.jetty;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.icymint.sloth.core.defer.Deferred;
import me.icymint.sloth.core.module.Module;
import me.icymint.sloth.core.module.Plugin;
import me.icymint.sloth.jetty.JettyPlugin.HandlerPlugin;

import org.eclipse.jetty.http.HttpMethod;

public class HandlerTreePlugin implements Plugin {
	public static final Pattern URLPATTERN = Pattern
			.compile("\\/([^\\/]*)(\\/.*)?");
	private final Set<HandlerTree> _set = new TreeSet<>((i, j) -> {
		int x = i.priority() - j.priority();
		if (x == 0) {
			x = (i.id() > j.id()) ? 1 : -1;
		}
		return x;
	});
	private final AtomicReference<Queue<HandlerTree>> _list = new AtomicReference<>(
			new LinkedList<>());
	private static final AtomicLong _index = new AtomicLong();

	private static enum Type {
		Normal, Id, Name;
	}

	private class HandlerTree {

		private final long _id;
		private final int _priority;
		private final Map<String, HandlerTree> _map = new ConcurrentHashMap<>();
		private final String _name;
		private final Type _type;
		private final HandlerPlugin _handler;

		public long id() {
			return _id;
		}

		public HandlerTree(int priority, String name, HandlerPlugin handler) {
			_id = _index.incrementAndGet();
			_priority = priority;
			Type type = Type.Normal;
			if (name.equals(":id"))
				type = Type.Id;
			else if (name.equals(":name"))
				type = Type.Name;
			_type = type;
			_name = name;
			_handler = handler;
		}

		private void add(String path, HandlerPlugin handler) {
			if (path.equals("") || path.equals("/"))
				return;
			Matcher m = URLPATTERN.matcher(path);
			HandlerTree tree = this;
			while (m.matches()) {
				String name = m.group(1);
				String next = m.group(2);
				if (null == next || "".equals(next) || "/".equals(next)) {
					tree._map.put(name, new HandlerTree(tree._priority, name,
							handler));
					return;
				} else {
					HandlerTree tree2 = tree._map.get(name);
					if (tree2 == null) {
						tree._map.put(name, new HandlerTree(tree._priority,
								name, null));
					} else {
						tree = tree2;
					}
				}
				m = URLPATTERN.matcher(next);
			}
		}

		public boolean matches(String name) {
			if (type() == Type.Normal || type() == null) {
				return name().equals(name);
			} else if (type() == Type.Id) {
				return name.matches("\\d+");
			} else {
				return true;
			}
		}

		public HandlerTree next(String name) {
			return _map.get(name);
		}

		public Type type() {
			return _type;
		}

		public HandlerPlugin method(HttpMethod method) {
			if (_handler == null)
				return null;
			return _handler.method() == method ? _handler : null;
		}

		public int priority() {
			return _priority;
		}

		public String name() {
			return _name;
		}

	}

	public synchronized long register(int priority, Object impl) {
		if (impl == null)
			return -1;
		HttpSupport hs = impl.getClass().getAnnotation(HttpSupport.class);
		String prefix = hs == null ? "" : hs.value();
		if (prefix.equals("/"))
			prefix = "";
		HandlerTree ht = new HandlerTree(priority, "", null);
		_set.add(ht);
		_list.set(new LinkedList<>(_set));
		return ht.id();
	}

	public synchronized void unregister(long id) {
		Iterator<HandlerTree> it = _set.iterator();
		while (it.hasNext()) {
			if (it.next().id() == id) {
				it.remove();
				_list.set(new LinkedList<>(_set));
				return;
			}
		}
	}

	@Override
	public void initAndDeferClose(Module module, Deferred deferred)
			throws Exception {
	}

	public HandlerPlugin fetch(String target, HttpMethod method) {
		for (HandlerTree tree : _list.get()) {
			Matcher m = URLPATTERN.matcher(target);
			List<String> abc = new LinkedList<>();
			while (m.matches()) {
				String name = m.group(1);
				if (name == null || "".equals(name)) {
					continue;
				}
				abc.add(name);
				tree = tree.next(name);
				if (tree == null) {
					continue;
				}
				String next = m.group(2);
				if (next == null || "".equals(next) || "".equals("/")) {
					break;
				}
				m = URLPATTERN.matcher(next);
			}
			HandlerPlugin hp = tree.method(method);
			if (hp != null)
				return hp;
		}
		return null;

	}
}
