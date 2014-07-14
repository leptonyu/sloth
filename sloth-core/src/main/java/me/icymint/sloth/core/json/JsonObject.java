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
package me.icymint.sloth.core.json;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JsonObject {

	private final Object _source;
	private final Map<String, JsonObject> _map;
	private final List<JsonObject> _list;

	public JsonObject(Object source) {
		_map = createMap(source);
		_list = _map == null ? createList(source) : null;
		_source = (_map == null && _list == null) ? source : null;
	}

	private List<JsonObject> createList(Object source) {
		if (List.class.isInstance(source)) {
			@SuppressWarnings("unchecked")
			List<Object> list = List.class.cast(source);
			List<JsonObject> xx = new LinkedList<>();
			list.forEach(obj -> xx.add(new JsonObject(obj)));
			return xx;
		}
		return null;
	}

	private Map<String, JsonObject> createMap(Object source) {
		if (Map.class.isInstance(source)) {
			Map<String, JsonObject> map = new HashMap<>();
			@SuppressWarnings("unchecked")
			Map<String, Object> xx = Map.class.cast(source);
			xx.forEach((k, v) -> map.put(k, new JsonObject(v)));
			return map;
		} else {
			return null;
		}
	}

	private <T> T source(Class<T> clazz) {
		if (_source != null && clazz != null && clazz.isInstance(_source)) {
			return clazz.cast(_source);
		}
		return null;
	}

	public JsonObject getValue(String key) {
		return _map != null ? _map.get(key) : null;
	}

	public JsonObject get(int index) {
		return _list != null ? _list.get(index) : null;
	}

	public String asString() {
		return source(String.class);
	}

	public String asString(String defaultValue) {
		String x = asString();
		return x == null ? defaultValue : x;
	}

	public long asLong(long defaultValue) {
		Long a = source(Long.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	public long asLong() {
		Long a = source(Long.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	public int asInt() {
		Integer a = source(Integer.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	public int asInt(int defaultValue) {
		Integer a = source(Integer.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	public short asShort() {
		Short a = source(Short.class);
		if (a != null)
			return a;
		throw new NumberFormatException();
	}

	public short asShort(short defaultValue) {
		Short a = source(Short.class);
		if (a != null)
			return a;
		return defaultValue;
	}

	public Object asObject() {
		return _source;
	}

}
