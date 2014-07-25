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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jetty.http.HttpMethod;

/**
 * @author Daniel Yu
 */
public class HandlerProvider {
	private static enum Type {
		Normal, Name, Id
	}

	public static final Pattern URLREGX = Pattern
			.compile("^\\/([^\\/]*)(\\/.*)?$");
	private final String _name;
	private final HandlerProvider _parent;
	private final Map<String, HandlerProvider> _children = new ConcurrentHashMap<>();
	private final Map<HttpMethod, SimpleHandler> _handlers = new ConcurrentHashMap<>();
	private volatile HandlerProvider _idchildren = null;
	private volatile HandlerProvider _namechildren = null;
	private final Type type;

	//
	private final long _id;
	private final int _priority;
	private static final AtomicLong _idcreator = new AtomicLong(0);

	public HandlerProvider(int priority) {
		this("", null, priority);
	}

	private HandlerProvider(String name, HandlerProvider parent, int priority) {
		_id = _idcreator.incrementAndGet();
		_priority = priority;
		_name = name;
		Type type = Type.Normal;
		if (name.equals(":id")) {
			type = Type.Id;
		} else if (name.equals(":name")) {
			type = Type.Name;
		}
		this.type = type;
		_parent = parent;
	}

	private HandlerProvider fork(String name) {
		HandlerProvider child = getChild(name);
		if (child == null && name.equals(":id")) {
			child = getIdChild();
		}
		if (child == null && name.equals(":name")) {
			child = getNameChild();
		}
		if (child == null) {
			HandlerProvider hp = new HandlerProvider(name, this, priority());
			if (hp.type == Type.Id) {
				_idchildren = hp;
			} else if (hp.type == Type.Name) {
				_namechildren = hp;
			} else {
				_children.put(name, hp);
			}
			return hp;
		} else {
			return child;
		}
	}

	private HandlerProvider getChild(String name) {
		return _children.get(name);
	}

	private HandlerProvider getIdChild() {
		return _idchildren;
	}

	private HandlerProvider getNameChild() {
		return _namechildren;
	}

	/**
	 * @return
	 */
	public long id() {
		return _id;
	}

	public boolean insert(String path, HttpMethod method, SimpleHandler sh) {
		// System.out.println(name() + "<<" + method + "<<" + sh);
		if (null == path || "".equals(path) || "/".equals(path)) {
			_handlers.put(method, sh);
			return true;
		}
		Matcher abc = URLREGX.matcher(path);
		if (abc.matches()) {
			String name = abc.group(1);
			if (null == name || "".equals(name)) {
				return false;
			}
			return fork(name).insert(abc.group(2), method, sh);
		}
		return false;
	}

	public boolean inserts(String path, HttpMethod[] methods, SimpleHandler sh) {
		if (methods != null) {
			boolean flag = true;
			for (HttpMethod method : methods) {
				flag = insert(path, method, sh) && flag;
			}
			return flag;
		}
		return false;
	}

	public String name() {
		return _name;
	}

	public HandlerProvider parent() {
		return _parent;
	}

	public int priority() {
		return _priority;
	}

	public SimpleHandler search(String path, HttpMethod method) {
		// System.out.println(name() + ">>" + path + ">>>" + method);
		if (null == path || "".equals(path) || "/".equals(path)) {
			return _handlers.get(method);
		}
		Matcher abc = URLREGX.matcher(path);
		if (abc.matches()) {
			String name = abc.group(1);
			if (null == name || "".equals(name)) {
				return null;
			}
			String nextpath = abc.group(2);
			HandlerProvider child = getIdChild();
			if (child != null && name.matches("\\d+")) {
				SimpleHandler sh = child.search(nextpath, method);
				if (sh != null)
					return sh;
			}
			child = getNameChild();
			if (child != null) {
				SimpleHandler sh = child.search(nextpath, method);
				if (sh != null)
					return sh;
			}
			child = getChild(name);
			if (child != null) {
				return child.search(nextpath, method);
			}
		}
		return null;
	}

}
